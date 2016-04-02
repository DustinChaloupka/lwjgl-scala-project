package org.chaloupka.lwjgl
import org.chaloupka.lwjgl.PrimitiveUtils.FloatSize
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL30.glBindFragDataLocation

trait ShaderProgram {
  def shaders: List[Shader]
  def vertexArrayObject: VertexArrayObject

  lazy val id = glCreateProgram()

  def init(): Unit = {
    vertexArrayObject.bind()
    shaders.foreach(_.init())
    attachShaders()
    link()
  }

  def attachShaders(): Unit = {
    shaders.foreach(shader => glAttachShader(id, shader.id))
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
    vertexArrayObject.delete()
    shaders.foreach { shader =>
      glDetachShader(id, shader.id)
      shader.delete()
    }

    glDeleteProgram(id)
  }

  private[this] val maxPositionAttributes = 3
  private[this] val maxColorAttributes = 4
  def specifyVertexAttributes(): Unit = {
    val positionAttribute = getAttributeLocation("position")
    glEnableVertexAttribArray(positionAttribute)
    glVertexAttribPointer(positionAttribute, maxPositionAttributes, GL_FLOAT, false, (maxPositionAttributes + maxColorAttributes) * FloatSize, 0)

    val colorAttribute = getAttributeLocation("color")
    glEnableVertexAttribArray(colorAttribute)
    glVertexAttribPointer(colorAttribute, maxColorAttributes, GL_FLOAT, false, (maxPositionAttributes + maxColorAttributes) * FloatSize, maxPositionAttributes * FloatSize)
  }
}
