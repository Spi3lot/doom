const int displayWidth = 600;
uniform isampler1D wallHeights;
uniform isampler1D colors;
uniform int backgroundColor;
uniform ivec2 resolution;

vec4 intToColor(int color) {
	return vec4(color & 0xFF, (color >> 8) & 0xFF, (color >> 16) & 0xFF, (color >> 24) & 0xFF) / 255.0;
}

void main() {
	int x = int(gl_FragCoord.x);
	int y = int(gl_FragCoord.y);
	int wallHeight = texelFetch(wallHeights, x, 0).r;
	int color = texelFetch(colors, x, 0).r;
	bool isWall = ((resolution.y - wallHeight) / 2 <= y && y < (resolution.y + wallHeight) / 2);
	gl_FragColor = (isWall) ? intToColor(color) : intToColor(backgroundColor);
}