#version 150

uniform sampler2D DiffuseSampler;

in vec2 texCoord;

uniform float Time;
uniform vec2 Frequency;
uniform vec2 WobbleAmount;

out vec4 fragColor;

void main() {
    float time = Time * 3.1415926535 * 2.0;
    vec2 offset = vec2(
            sin(texCoord.y * Frequency.x + time) * WobbleAmount.x,
            cos(texCoord.x * Frequency.y + time * 1.37) * WobbleAmount.y
    );
    fragColor = texture(DiffuseSampler, texCoord + offset);
}
