package games.fantasy.lwjgl

import java.nio.FloatBuffer

import org.lwjgl.opengl.GL15._

class VertexArrayBuffer(id: Int = glGenBuffers()) {
  def bind(target: Int): Unit = {
    glBindBuffer(target, id)
  }

  def uploadData(target: Int, data: FloatBuffer, usage: Int): Unit = {
    glBufferData(target, data, usage)
  }

  def delete(): Unit = {
    glDeleteBuffers(id)
  }
}
