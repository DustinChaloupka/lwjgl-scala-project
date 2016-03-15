package games.fantasy.lwjgl
import org.lwjgl.glfw.GLFW._
import org.lwjgl.opengl.GL
import org.lwjgl.system.MemoryUtil._

import Callbacks._

object Run extends GameLoop {

  def init(): Long = {
    glfwSetErrorCallback(errorCallback)

    if (glfwInit() != GLFW_TRUE) {
      throw new IllegalStateException("Unable to initialize GLFW")
    }

    lazy val window = glfwCreateWindow(640, 480, "Simple example", NULL, NULL)
    if (window == NULL) {
      glfwTerminate()
      throw new RuntimeException("Failed to create the GLFW window")
    }

    glfwSetKeyCallback(window, keyCallback)

    glfwMakeContextCurrent(window)
    GL.createCapabilities()

    window
  }

  def dispose(window: Long): Unit = {
    glfwDestroyWindow(window)
    keyCallback.release()

    glfwTerminate()
    errorCallback.release()
  }

  def main(args: Array[String]): Unit = {
    var madeWindow: Long = -1

    try {
      val window = init()
      madeWindow = window
      playGame(window)
    } finally {
      dispose(madeWindow)
    }
  }
}
