package org.chaloupka.lwjgl
import org.lwjgl.opengl.GL20

trait FragmentShader extends Shader {
  lazy val shaderType = GL20.GL_FRAGMENT_SHADER
}
