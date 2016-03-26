package org.chaloupka.lwjgl
import org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER

class ElementBufferObject extends BufferObject {
  lazy val bufferType = GL_ELEMENT_ARRAY_BUFFER
}
