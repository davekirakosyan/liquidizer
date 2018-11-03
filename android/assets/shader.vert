attribute vec4 a_position;
attribute vec2 a_texCoord0;
attribute vec4 a_color;


uniform mat4 u_projTrans;

varying vec2 v_texCoords;
varying vec4 v_color;

void main() {

    gl_Position = u_projTrans * a_position;
    v_texCoords = a_texCoord0;
    v_color = a_color;
}