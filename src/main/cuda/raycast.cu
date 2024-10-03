#include <jni.h>
#include <cuda_runtime.h>
#include <stdio.h>

__device__ float distance(float2 a, float2 b)
{
    return hypotf(b.x - a.x, b.y - a.y);
}

__device__ float2 lerp(float2 a, float2 b, float t)
{
    return make_float2(fmaf(t, b.x - a.x, a.x), fmaf(t, b.y - a.y, a.y));
}

__global__ void castRaysKernel(
    int *map,
    int mapWidth,
    int mapHeight,
    float worldScale,
    int windowWidth,
    int windowHeight,
    int maxSteps,
    float epsilon,
    float2 playerPos,
    float playerHeading,
    float2 leftMostRayDirection,
    float2 rightMostRayDirection,
    int *wallHeights,
    int *colors)
{
    int x = blockIdx.x * blockDim.x + threadIdx.x;
    if (x >= windowWidth)
        return;

    float t = x / (float)windowWidth;
    float2 rayPos = playerPos;
    float2 rayDir = lerp(leftMostRayDirection, rightMostRayDirection, t);
    float rayHeading = atan2f(rayDir.y, rayDir.x);

    for (int step = 0; step < maxSteps; ++step)
    {
        int mapX = (int)floorf(rayPos.x);
        int mapY = (int)floorf(rayPos.y);

        if (mapX >= 0 && mapX < mapWidth && mapY >= 0 && mapY < mapHeight)
        {
            int color = map[mapY * mapWidth + mapX];

            if (color != 0) // TODO: NULL instead of 0
            {
                float dist = worldScale * distance(rayPos, playerPos);
                float adjustedDist = dist * cosf(rayHeading - playerHeading);
                wallHeights[x] = (int) (windowHeight / fmaxf(1.0f, adjustedDist));
                colors[x] = color;
                return;
            }
        }

        // Step the ray
        float targetX = (rayDir.x > 0) ? floorf(rayPos.x + 1) : ceilf(rayPos.x - 1);
        float targetY = (rayDir.y > 0) ? floorf(rayPos.y + 1) : ceilf(rayPos.y - 1);
        float stepSize = fminf((targetX - rayPos.x) / rayDir.x, (targetY - rayPos.y) / rayDir.y);
        rayPos.x += rayDir.x * (stepSize + epsilon);
        rayPos.y += rayDir.y * (stepSize + epsilon);
    }
}

extern "C" JNIEXPORT void JNICALL Java_org_spi3lot_rendering_RaycastGpu_castCudaRays(
    JNIEnv *env,
    jobject obj,
    jobjectArray map,
    jfloat worldScale,
    jint windowWidth,
    jint windowHeight,
    jint maxSteps,
    jfloat epsilon,
    jfloat playerX,
    jfloat playerY,
    jfloat playerHeading,
    jfloat leftMostRayDirectionX,
    jfloat leftMostRayDirectionY,
    jfloat rightMostRayDirectionX,
    jfloat rightMostRayDirectionY,
    jintArray wallHeightsArray,
    jintArray colorsArray)
{
    // Convert Java arrays to native arrays
    jint *wallHeights = env->GetIntArrayElements(wallHeightsArray, 0);
    jint *colors = env->GetIntArrayElements(colorsArray, 0);

    // Get map data from Java object
    int mapHeight = env->GetArrayLength(map);
    int mapWidth = env->GetArrayLength((jintArray)env->GetObjectArrayElement(map, 0));

    // Allocate and copy map data to device
    int *h_map = new int[mapWidth * mapHeight];
    for (int i = 0; i < mapHeight; ++i)
    {
        jintArray row = (jintArray)env->GetObjectArrayElement(map, i);
        int *rowData = env->GetIntArrayElements(row, 0);
        memcpy(h_map + i * mapWidth, rowData, mapWidth * sizeof(int));
        env->ReleaseIntArrayElements(row, rowData, 0);
    }

    int *d_map;
    cudaMalloc(&d_map, mapWidth * mapHeight * sizeof(int));
    cudaMemcpy(d_map, h_map, mapWidth * mapHeight * sizeof(int), cudaMemcpyHostToDevice);
    delete[] h_map;

    // Allocate device memory for results
    int *d_wallHeights;
    int *d_colors;
    cudaMalloc(&d_wallHeights, windowWidth * sizeof(float));
    cudaMalloc(&d_colors, windowWidth * sizeof(int));

    // Define player position and ray directions
    float2 playerPos = make_float2(playerX, playerY);
    float2 leftMostRay = make_float2(leftMostRayDirectionX, leftMostRayDirectionY);
    float2 rightMostRay = make_float2(rightMostRayDirectionX, rightMostRayDirectionY);

    // Launch the kernel
    int blockSize = 256;
    int numBlocks = (windowWidth + blockSize - 1) / blockSize;

    castRaysKernel<<<numBlocks, blockSize>>>(
        d_map,
        mapWidth,
        mapHeight,
        worldScale,
        windowWidth,
        windowHeight,
        maxSteps,
        epsilon,
        playerPos,
        playerHeading,
        leftMostRay,
        rightMostRay,
        d_wallHeights,
        d_colors);

    // Copy results back to host
    cudaMemcpy(wallHeights, d_wallHeights, windowWidth * sizeof(float), cudaMemcpyDeviceToHost);
    cudaMemcpy(colors, d_colors, windowWidth * sizeof(int), cudaMemcpyDeviceToHost);

    // Release device memory
    cudaFree(d_wallHeights);
    cudaFree(d_colors);
    cudaFree(d_map);

    // Release Java arrays
    env->ReleaseIntArrayElements(wallHeightsArray, wallHeights, 0);
    env->ReleaseIntArrayElements(colorsArray, colors, 0);
}