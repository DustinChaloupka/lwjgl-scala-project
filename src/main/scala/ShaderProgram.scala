package games.fantasy.lwjgl
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL30.glBindFragDataLocation

case class ShaderProgram(id: Int = glCreateProgram()) {
  def attachShader(shader: Shader): Unit = {
    glAttachShader(id, shader.id)
  }

  def bindFragDataLocation(number: Int, name: String): Unit = {
    glBindFragDataLocation(id, number, name)
  }

  def link(): Unit = {
    glLinkProgram(id)
  }

  def getAttributeLocation(name: String): Int = {
    glGetAttribLocation(id, name)
  }

  def getUniformLocation(name: String): Int = {
    glGetUniformLocation(id, name)
  }

  def setUniformMatrix4(location: Int, model: Matrix4): Unit = {
    glUniformMatrix4fv(location, false, model.getBuffer)
  }

  def getStatus(): Int = {
    glGetProgrami(id, GL_LINK_STATUS)
  }

  def use(): Unit = {
    glUseProgram(id)
  }

  def delete(): Unit = {
    glDeleteProgram(id)
  }
}
