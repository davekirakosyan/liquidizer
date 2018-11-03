#ifdef GL_ES
    #define LOWP lowp
    precision mediump float;
#else
    #define LOWP
#endif

// Varyings
varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;

void main() {

	vec4 color = v_color * texture2D(u_texture, v_texCoords);

    float top = color.a;

    top -= texture2D(u_texture, v_texCoords + vec2(0, -0.015)).a;
    top = step(0.6, top);

    color.rgb += vec3(top, top, top);

	gl_FragColor = color;

	//gl_FragColor.a = smoothstep(0.5, 0.6, color.a);

	gl_FragColor.a = smoothstep(0.4, 0.45, color.a);
}