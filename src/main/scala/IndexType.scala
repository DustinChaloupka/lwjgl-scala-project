package org.chaloupka.lwjgl
import org.lwjgl.opengl.GL11

object IndexType extends Enumeration {
  val UnsignedInt = Value(GL11.GL_UNSIGNED_INT)
  val UnsignedShort = Value(GL11.GL_UNSIGNED_SHORT)
  val UnsignedByte = Value(GL11.GL_UNSIGNED_BYTE)
}
