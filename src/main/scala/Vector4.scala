package org.chaloupka.lwjgl
import java.nio.FloatBuffer
import org.lwjgl.BufferUtils

case class Vector4(x: Float, y: Float, z: Float, w: Float) {
  def lengthSquared: Float = {
    x * x + y * y + z * z + w * w
  }

  def length: Float = {
    Math.sqrt(lengthSquared).toFloat
  }

  def normalize(): Vector4 = {
    divide(length)
  }

  def add(that: Vector4): Vector4 = {
    val newX = x + that.x
    val newY = y + that.y
    val newZ = z + that.z
    val newW = w + that.w
    Vector4(newX, newY, newZ, newW)
  }

  def +(that: Vector4): Vector4 = {
    this.add(that)
  }

  def negate(): Vector4 = {
    scale(-1f)
  }

  def subtract(that: Vector4): Vector4 = {
    this.add(that.negate())
  }

  def -(that: Vector4): Vector4 = {
    this.subtract(that)
  }

  def scale(scalar: Float): Vector4 = {
    val newX = x * scalar
    val newY = y * scalar
    val newZ = z * scalar
    val newW = w * scalar
    Vector4(newX, newY, newZ, newW)
  }

  def divide(scalar: Float): Vector4 = {
    scale(1f / scalar)
  }

  def /(scalar: Float): Vector4 = {
    this.divide(scalar)
  }

  def dot(that: Vector4): Float = {
    x * that.x + y * that.y + z * that.z + w * that.w
  }

  def lerp(that: Vector4, alpha: Float): Vector4 = {
    this.scale(1f - alpha).add(that.scale(alpha))
  }

  def getBuffer: FloatBuffer = {
    val buffer = BufferUtils.createFloatBuffer(4)
    buffer.put(x).put(y).put(z).put(w)
    buffer.flip()
    buffer
  }
}
