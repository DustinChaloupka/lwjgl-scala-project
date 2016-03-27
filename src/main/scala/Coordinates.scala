package org.chaloupka.lwjgl

object Coordinates {
  sealed trait Position {
    def x: Float
    def y: Float
    def z: Float

    def onSameXAxisAs(that: Position): Boolean = {
      that.y == y && that.z == z
    }

    def onSameYAxisAs(that: Position): Boolean = {
      that.x == x && that.z == z
    }

    def onSameZAxisAs(that: Position): Boolean = {
      that.x == x && that.y == y
    }

    def onSameXYPlaneAs(that: Position): Boolean = {
      that.z == z
    }

    def onSameXZPlaneAs(that: Position): Boolean = {
      that.y == y
    }

    def onSameYZPlaneAs(that: Position): Boolean = {
      that.x == x
    }
  }

  case class XYZ(x: Float, y: Float, z: Float) extends Position
  case class XY(x: Float, y: Float) extends Position {
    lazy val z = 0f
  }

  case class XZ(x: Float, z: Float) extends Position {
    lazy val y = 0f
  }

  case class X(x: Float) extends Position {
    lazy val y = 0f
    lazy val z = 0f
  }

  case class YZ(y: Float, z: Float) extends Position {
    lazy val x = 0f
  }

  case class Y(y: Float) extends Position {
    lazy val x = 0f
    lazy val z = 0f
  }

  case class Z(z: Float) extends Position {
    lazy val x = 0f
    lazy val y = 0f
  }
}
