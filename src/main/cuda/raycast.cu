#include <jni.h>
#include <cuda_runtime.h>

__device__ float distance(float2 a, float2 b)
{
    return hypotf(b.x - a.x, b.y - a.y);
}

__device__ float2 lerp(float2 a, float2 b, float t)
{
    return make_float2(fmaf(t, b.x - a.x, a.x), fmaf(t, b.y - a.y, a.y));
}

__global__ void castRaysKernel(
    int width,
    int maxSteps,
    float epsilon,
    float2 playerPos,
    float playerHeading,
    float2 leftMostRayDirection,
    float2 rightMostRayDirection,
    float *wallHeights,
    int *colors,
    int *map,
    int mapWidth,
    int mapHeight)
{
    int x = blockIdx.x * blockDim.x + threadIdx.x;
    if (x >= width)
        return;

    float t = x / (float)width;
    float2 rayDir = lerp(leftMostRayDirection, rightMostRayDirection, t);
    float2 rayPos = playerPos;

    for (int step = 0; step < maxSteps; ++step)
    {
        int mapX = (int)floorf(rayPos.x);
        int mapY = (int)floorf(rayPos.y);

        if (mapX >= 0 && mapX < mapWidth && mapY >= 0 && mapY < mapHeight)
        {
            int color = map[mapY * mapWidth + mapX];
            if (color != 0)  // TODO: NULL instead of 0
            {
                float dist = distance(rayPos, playerPos);
                float adjustedDist = dist * cosf(rayDir.x - playerHeading);
                wallHeights[x] = 1.0f / fmaxf(1.0f, adjustedDist);
                colors[x] = color;
                return;
            }
        }

        // Step the ray
        float targetX = (rayDir.x > 0) ? floorf(rayPos.x + 1) : ceilf(rayPos.x - 1);
        float targetY = (rayDir.y > 0) ? floorf(rayPos.y + 1) : ceilf(rayPos.y - 1);
        float stepSize = fminf((targetX - rayPos.x) / rayDir.x, (targetY - rayPos.y) / rayDir.y);
        rayPos.x += rayDir.x * stepSize;
        rayPos.y += rayDir.y * stepSize;
    }
}

extern "C" JNIEXPORT void JNICALL Java_org_spi3lot_rendering_RaycastGpu_castCudaRays(
    JNIEnv *env,
    jobject obj,
    jobjectArray map,
    jint width,
    jint maxSteps,
    jfloat epsilon,
    jfloat playerX,
    jfloat playerY,
    jfloat playerHeading,
    jfloat leftMostRayDirectionX,
    jfloat leftMostRayDirectionY,
    jfloat rightMostRayDirectionX,
    jfloat rightMostRayDirectionY,
    jfloatArray wallHeightsArray,
    jintArray colorsArray)
{
    // Convert Java arrays to native arrays
    jfloat *wallHeights = env->GetFloatArrayElements(wallHeightsArray, 0);
    jint *colors = env->GetIntArrayElements(colorsArray, 0);

    // Get map data from Java object
    int mapHeight = env->GetArrayLength(map);
    int mapWidth = env->GetArrayLength((jintArray)env->GetObjectArrayElement(map, 0));

    // Allocate and copy map data to device
    int *h_map = new int[mapWidth * mapHeight];
    for (int i = 0; i < mapHeight; ++i)
    {
        jintArray row = (jintArray)env->GetObjectArrayElement(map, i);
        jint *rowData = env->GetIntArrayElements(row, 0);
        memcpy(h_map + i * mapWidth, rowData, mapWidth * sizeof(int));
        env->ReleaseIntArrayElements(row, rowData, 0);
    }

    int *d_map;
    cudaMalloc(&d_map, mapWidth * mapHeight * sizeof(int));
    cudaMemcpy(d_map, h_map, mapWidth * mapHeight * sizeof(int), cudaMemcpyHostToDevice);
    delete[] h_map;

    // Allocate device memory for results
    float *d_wallHeights;
    int *d_colors;
    cudaMalloc(&d_wallHeights, width * sizeof(float));
    cudaMalloc(&d_colors, width * sizeof(int));

    // Define player position and ray directions
    float2 playerPos = make_float2(playerX, playerY);
    float2 leftMostRay = make_float2(leftMostRayDirectionX, leftMostRayDirectionY);
    float2 rightMostRay = make_float2(rightMostRayDirectionX, rightMostRayDirectionY);

    // Launch the kernel
    int blockSize = 256;
    int numBlocks = (width + blockSize - 1) / blockSize;

    castRaysKernel<<<numBlocks, blockSize>>>(
        width,
        maxSteps,
        epsilon,
        playerPos,
        playerHeading,
        leftMostRay,
        rightMostRay,
        d_wallHeights,
        d_colors,
        d_map,
        mapWidth,
        mapHeight);

    // Copy results back to host
    cudaMemcpy(wallHeights, d_wallHeights, width * sizeof(float), cudaMemcpyDeviceToHost);
    cudaMemcpy(colors, d_colors, width * sizeof(int), cudaMemcpyDeviceToHost);

    // Release device memory
    cudaFree(d_wallHeights);
    cudaFree(d_colors);
    cudaFree(d_map);

    // Release Java arrays
    env->ReleaseFloatArrayElements(wallHeightsArray, wallHeights, 0);
    env->ReleaseIntArrayElements(colorsArray, colors, 0);
}