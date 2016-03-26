package org.chaloupka.lwjgl
import java.nio.{FloatBuffer, IntBuffer}
import org.lwjgl.opengl.GL15._

trait BufferObject {
  def bufferType: Int

  lazy val id = glGenBuffers()

  def bind(): Unit = {
    glBindBuffer(bufferType, id)
  }

  def uploadData(data: FloatBuffer, usage: Int): Unit = {
    glBufferData(bufferType, data, usage)
  }

  def uploadData(data: IntBuffer, usage: Int): Unit = {
    glBufferData(bufferType, data, usage)
  }

  def delete(): Unit = {
    glDeleteBuffers(id)
  }
}
