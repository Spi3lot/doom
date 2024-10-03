uniform ivec2 resolution;
uniform vec4 backgroundColor;
uniform sampler2D texture;

int vec4ToInt(vec4 v) {
	int a = int(v.a * 255f) << 24;
	int r = int(v.r * 255f) << 16;
	int g = int(v.g * 255f) << 8;
	int b = int(v.b * 255f);
	return a | r | g | b;
}

void main() {
	int x = int(gl_FragCoord.x);
	int y = int(gl_FragCoord.y);
	int wallHeight = vec4ToInt(texelFetch(texture, ivec2(x, 0), 0));
	bool isWall = ((resolution.y - wallHeight) < y * 2 && y * 2 < (resolution.y + wallHeight));
	gl_FragColor = (isWall) ? texelFetch(texture, ivec2(x, 1), 0) : backgroundColor;
}