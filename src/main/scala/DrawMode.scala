package org.chaloupka.lwjgl
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL32

object DrawMode extends Enumeration {
  val Points = Value(GL11.GL_POINTS)
  val LineStrip = Value(GL11.GL_LINE_STRIP)
  val LineLoop = Value(GL11.GL_LINE_LOOP)
  val Lines = Value(GL11.GL_LINES)
  val TriangleStrip = Value(GL11.GL_TRIANGLE_STRIP)
  val TriangleFan = Value(GL11.GL_TRIANGLE_FAN)
  val Triangles = Value(GL11.GL_TRIANGLES)
  val LinesAdjacency = Value(GL32.GL_LINES_ADJACENCY)
  val LineStripAdjacency = Value(GL32.GL_LINE_STRIP_ADJACENCY)
  val TriangleStripAdjacency = Value(GL32.GL_TRIANGLE_STRIP_ADJACENCY)
  val TrianglesAdjacency = Value(GL32.GL_TRIANGLES_ADJACENCY)
}
