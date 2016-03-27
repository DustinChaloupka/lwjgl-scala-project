package org.chaloupka.lwjgl
import org.lwjgl.opengl.GL20

class FragmentShader extends Shader {
  lazy val shaderType = GL20.GL_FRAGMENT_SHADER
  lazy val source = """#version 150 core

in vec4 vertexColor;

out vec4 fragColor;

void main() {
  fragColor = vertexColor;
}
"""
}
