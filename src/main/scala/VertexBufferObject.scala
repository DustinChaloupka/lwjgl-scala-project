package org.chaloupka.lwjgl
import org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER

class VertexBufferObject extends BufferObject {
  lazy val bufferType = GL_ARRAY_BUFFER
}
