package org.chaloupka.lwjgl
import org.lwjgl.opengl.GL15

object VertexUsage extends Enumeration {
  val Static = Value(GL15.GL_STATIC_DRAW)
  val Dynamic = Value(GL15.GL_DYNAMIC_DRAW)
  val Stream = Value(GL15.GL_STREAM_DRAW)
}
