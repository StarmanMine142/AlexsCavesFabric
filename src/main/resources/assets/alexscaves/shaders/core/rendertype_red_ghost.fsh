#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
in vec4 vertexColor;
in vec4 lightMapColor;
in vec4 overlayColor;
in vec2 texCoord0;
in vec4 normal;

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0) * vertexColor * ColorModulator;
    if (color.a < 0.1) {
        discard;
    }
    color.rgb = mix(overlayColor.rgb, color.rgb, overlayColor.a);
    color *= lightMapColor;

    float targetR = .9;
    float targetG = .3;
    float targetB = .05;

    float colorVal = color.r;
    if(color.g > colorVal){
        colorVal = color.g;
    }
    if(color.b > colorVal){
        colorVal = color.b;
    }
    vec4 ghostColor = vec4(colorVal * targetR, colorVal * targetG, colorVal * targetB, color.a);
    fragColor = linear_fog(ghostColor, vertexDistance, FogStart, FogEnd, FogColor);
}