package org.chaloupka.lwjgl

class DefaultShaderProgram extends ShaderProgram {
  lazy val vertexArrayObject = new VertexArrayObject()
  lazy val vertexShader = new DefaultVertexShader()
  lazy val fragmentShader = new DefaultFragmentShader()
  lazy val shaders = List(vertexShader, fragmentShader)
}
