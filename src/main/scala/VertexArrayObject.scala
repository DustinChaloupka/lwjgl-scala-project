package org.chaloupka.lwjgl

import org.lwjgl.opengl.GL30._

class VertexArrayObject(id: Int = glGenVertexArrays()) {
  def bind(): Unit = {
    glBindVertexArray(id)
  }

  def delete(): Unit = {
    glDeleteVertexArrays(id)
  }
}
