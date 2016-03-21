#version 150 core

in vec3 position;
in vec3 color;

out vec3 vertexColor;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main() {
  vertexColor = color;
  mat4 mvp = projection * view * model;
  gl_Position = mvp * vec4(position, 1.0);
}
