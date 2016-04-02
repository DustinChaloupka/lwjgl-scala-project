package org.chaloupka.lwjgl
import org.lwjgl.opengl.GL20

trait VertexShader extends Shader {
  lazy val shaderType = GL20.GL_VERTEX_SHADER
}
