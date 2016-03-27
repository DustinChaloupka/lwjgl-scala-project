package org.chaloupka.lwjgl
import java.nio.{FloatBuffer, IntBuffer}
import org.lwjgl.opengl.GL15._

trait BufferObject {
  def bufferType: Int

  lazy val id = glGenBuffers()

  def uploadData(data: FloatBuffer, usage: Int): Unit = {
    bind()
    glBufferData(bufferType, data, usage)
  }

  def uploadData(data: IntBuffer, usage: Int): Unit = {
    bind()
    glBufferData(bufferType, data, usage)
  }

  def delete(): Unit = {
    glDeleteBuffers(id)
  }

  private[this] def bind(): Unit = {
    glBindBuffer(bufferType, id)
  }
}

object BufferObject {
  lazy val AttributeSize = 7
}
