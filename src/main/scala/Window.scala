package org.chaloupka.lwjgl
import org.lwjgl.system.MemoryUtil._
import org.lwjgl.glfw.GLFWKeyCallback
import org.lwjgl.glfw.GLFW._
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL20._

case class Window(width: Int, height: Int, title: String) {
  lazy val id = glfwCreateWindow(width, height, title, NULL, NULL)

  def setDefaultWindowHints(): Unit = {
    glfwDefaultWindowHints()
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2)
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
    glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE)
  }

  def makeContextCurrent(): Unit = {
    glfwMakeContextCurrent(id)
  }

  def createCapabilities(): Unit = {
    GL.createCapabilities()
  }

  def setKeyCallback(keyCallback: GLFWKeyCallback): Unit = {
    glfwSetKeyCallback(id, keyCallback)
  }

  def dispose(): Unit = {
    glfwDestroyWindow(id)
  }
}
