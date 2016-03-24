package org.chaloupka.lwjgl
import java.nio.FloatBuffer
import org.lwjgl.BufferUtils

case class Matrix4(row1: Vector4, row2: Vector4, row3: Vector4, row4: Vector4) {
  lazy val column1 = Vector4(row1.x, row2.x, row3.x, row4.x)
  lazy val column2 = Vector4(row1.y, row2.y, row3.y, row4.y)
  lazy val column3 = Vector4(row1.z, row2.z, row3.z, row4.z)
  lazy val column4 = Vector4(row1.w, row2.w, row3.w, row4.w)

  def add(that: Matrix4): Matrix4 = {
    val newRow1 = row1 + that.row1
    val newRow2 = row2 + that.row2
    val newRow3 = row3 + that.row3
    val newRow4 = row4 + that.row4

    Matrix4(newRow1, newRow2, newRow3, newRow4)
  }

  def +(that: Matrix4): Matrix4 = {
    this.add(that)
  }

  def negate(): Matrix4 = {
    multiply(-1f)
  }

  def subtract(that: Matrix4): Matrix4 = {
    this.add(that.negate())
  }

  def -(that: Matrix4): Matrix4 ={
    this.subtract(that)
  }

  def multiply(scalar: Float): Matrix4 = {
    val newRow1 = row1.scale(scalar)
    val newRow2 = row2.scale(scalar)
    val newRow3 = row3.scale(scalar)
    val newRow4 = row4.scale(scalar)

    Matrix4(newRow1, newRow2, newRow3, newRow4)
  }

  def *(scalar: Float): Matrix4 = {
    this.multiply(scalar)
  }

  def multiply(vector: Vector4): Vector4 = {
    val newX = row1 dot vector
    val newY = row2 dot vector
    val newZ = row3 dot vector
    val newW = row4 dot vector

    Vector4(newX, newY, newZ, newW);
  }

  def *(vector: Vector4): Vector4 = {
    this.multiply(vector)
  }

  def multiply(that: Matrix4): Matrix4 = {
    val row1X = row1 dot that.column1
    val row1Y = row1 dot that.column2
    val row1Z = row1 dot that.column3
    val row1W = row1 dot that.column4

    val row2X = row2 dot that.column1
    val row2Y = row2 dot that.column2
    val row2Z = row2 dot that.column3
    val row2W = row2 dot that.column4

    val row3X = row3 dot that.column1
    val row3Y = row3 dot that.column2
    val row3Z = row3 dot that.column3
    val row3W = row3 dot that.column4

    val row4X = row4 dot that.column1
    val row4Y = row4 dot that.column2
    val row4Z = row4 dot that.column3
    val row4W = row4 dot that.column4

    Matrix4(Vector4(row1X, row1Y, row1Z, row1W),
            Vector4(row2X, row2Y, row2Z, row2W),
            Vector4(row3X, row3Y, row3Z, row3W),
            Vector4(row4X, row4Y, row4Z, row4W))
  }

  def *(that: Matrix4): Matrix4 = {
    this.multiply(that)
  }

  def transpose(): Matrix4 = {
    Matrix4(column1,
            column2,
            column3,
            column4)
  }

  def getBuffer: FloatBuffer = {
    val buffer = BufferUtils.createFloatBuffer(16)
    buffer.put(row1.x).put(row2.x).put(row3.x).put(row4.x)
    buffer.put(row1.y).put(row2.y).put(row3.y).put(row4.y)
    buffer.put(row1.z).put(row2.z).put(row3.z).put(row4.z)
    buffer.put(row1.w).put(row2.w).put(row3.w).put(row4.w)
    buffer.flip()
    buffer
  }
}

object Matrix4 {
  def identity(): Matrix4 = {
    Matrix4(Vector4(1f, 0f, 0f, 0f),
            Vector4(0f, 1f, 0f, 0f),
            Vector4(0f, 0f, 1f, 0f),
            Vector4(0f, 0f, 0f, 1f))
  }

  def orthographic(left: Float, right: Float, bottom: Float, top: Float, near: Float, far: Float): Matrix4 = {
    val tx = -(right + left) / (right - left)
    val ty = -(top + bottom) / (top - bottom)
    val tz = -(far + near) / (far - near)

    val newRow1X = 2f / (right - left)
    val newRow2Y = 2f / (top - bottom)
    val newRow3Z = -2f / (far - near)

    Matrix4(Vector4(newRow1X, 0f, 0f, tx),
            Vector4(0f, newRow2Y, 0f, ty),
            Vector4(0f, 0f, newRow3Z, tz),
            Vector4(0f, 0f, 0f, 1f))
  }

  def frustum(left: Float, right: Float, bottom: Float, top: Float, near: Float, far: Float): Matrix4 = {
    val a = (right + left) / (right - left)
    val b = (top + bottom) / (top - bottom)
    val c = -(far + near) / (far - near)
    val d = -(2f * far * near) / (far - near)

    val newRow1X = (2f * near) / (right - left)
    val newRow2Y = (2f * near) / (top - bottom)

    Matrix4(Vector4(newRow1X, 0f, a, 0f),
            Vector4(0f, newRow2Y, b, 0f),
            Vector4(0f, 0f, c, d),
            Vector4(0f, 0f, -1f, 0f))
  }

  def perspective(fovy: Float, aspect: Float, near: Float, far: Float): Matrix4 = {
    val f = (1f / Math.tan(Math.toRadians(fovy) / 2f)).toFloat

    val newRow1X = f / aspect
    val newRow3Z = (far + near) / (near - far)
    val newRow3W = (2f * far * near)/ (near - far)

    Matrix4(Vector4(newRow1X, 0f, 0f, 0f),
            Vector4(0f, f, 0f, 0f),
            Vector4(0f, 0f, newRow3Z, newRow3W),
            Vector4(0f, 0f, -1f, 0f))
  }

  def translate(x: Float, y: Float, z: Float): Matrix4 = {
    Matrix4(Vector4(1f, 0f, 0f, x),
            Vector4(0f, 1f, 0f, y),
            Vector4(0f, 0f, 1f, z),
            Vector4(0f, 0f, 0f, 1f))
  }

  def rotate(angle: Float, x: Float, y: Float, z: Float): Matrix4 = {
    val c = Math.cos(Math.toRadians(angle)).toFloat
    val s = Math.sin(Math.toRadians(angle)).toFloat

    def createRotationMatrix(newX: Float, newY: Float, newZ: Float): Matrix4 = {
      val newRow1X = x * x * (1f - c) + c
      val newRow1Y = x * y * (1f - c) - z * s
      val newRow1Z = x * z * (1f - c) + y * s

      val newRow2X = y * x * (1f - c) + z * s
      val newRow2Y = y * y * (1f - c) + c
      val newRow2Z = y * z * (1f - c) - x * s

      val newRow3X = x * z * (1f - c) - y * s
      val newRow3Y = y * z * (1f - c) + x * s
      val newRow3Z = z * z * (1f - c) + c

      Matrix4(Vector4(newRow1X, newRow1Y, newRow1Z, 0f),
              Vector4(newRow2X, newRow2Y, newRow2Z, 0f),
              Vector4(newRow3X, newRow3Y, newRow3Z, 0f),
              Vector4(0f, 0f, 0f, 1f))
    }

    val vector = Vector4(x, y, z, 0f)

    if (vector.length != 1f) {
      val normalizedVector = vector.normalize()
      createRotationMatrix(normalizedVector.x, normalizedVector.y, normalizedVector.z)
    } else {
      createRotationMatrix(x, y, z)
    }
  }

  def scale(x: Float, y: Float, z: Float): Matrix4 = {
    Matrix4(Vector4(x, 0f, 0f, 0f),
            Vector4(0f, y, 0f, 0f),
            Vector4(0f, 0f, z, 0f),
            Vector4(0f, 0f, 0f, 1f))
  }
}
