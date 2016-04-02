package org.chaloupka.lwjgl

class DefaultFragmentShader extends FragmentShader {
  lazy val source = """#version 150 core

in vec4 vertexColor;

out vec4 fragColor;

void main() {
  fragColor = vertexColor;
}
"""
}
