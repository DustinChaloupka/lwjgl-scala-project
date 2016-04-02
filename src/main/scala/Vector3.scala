package org.chaloupka.lwjgl
import java.nio.FloatBuffer
import org.lwjgl.BufferUtils

case class Vector3(x: Float, y: Float, z: Float) {
  def lengthSquared: Float = {
    x * x + y * y + z * z
  }

  def length: Float = {
    Math.sqrt(lengthSquared).toFloat
  }

  def normalize(): Vector3 = {
    divide(length)
  }

  def add(that: Vector3): Vector3 = {
    val newX = x + that.x
    val newY = y + that.y
    val newZ = z + that.z
    Vector3(newX, newY, newZ)
  }

  def +(that: Vector3): Vector3 = {
    this.add(that)
  }

  def negate(): Vector3 = {
    scale(-1f)
  }

  def subtract(that: Vector3): Vector3 = {
    this.add(that.negate())
  }

  def -(that: Vector3): Vector3 = {
    this.subtract(that)
  }

  def scale(scalar: Float): Vector3 = {
    val newX = x * scalar
    val newY = y * scalar
    val newZ = z * scalar
    Vector3(newX, newY, newZ)
  }

  def divide(scalar: Float): Vector3 = {
    scale(1f / scalar)
  }

  def /(scalar: Float): Vector3 = {
    this.divide(scalar)
  }

  def dot(that: Vector3): Float = {
    x * that.x + y * that.y + z * that.z
  }

  def cross(that: Vector3): Vector3 = {
    val newX = y * that.z - z * that.y
    val newY = z * that.x - x * that.z
    val newZ = x * that.y - y * that.x
    Vector3(newX, newY, newZ)
  }

  def lerp(that: Vector3, alpha: Float): Vector3 = {
    this.scale(1f - alpha).add(that.scale(alpha))
  }

  def getBuffer: FloatBuffer = {
    val buffer = BufferUtils.createFloatBuffer(3)
    buffer.put(x).put(y).put(z)
    buffer.flip()
    buffer
  }
}
