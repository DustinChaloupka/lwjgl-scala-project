package games.fantasy.lwjgl
import org.lwjgl.glfw.GLFW._
import org.lwjgl.opengl.GL
import org.lwjgl.system.MemoryUtil._
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL11.GL_TRUE
import org.lwjgl.opengl.GL11.GL_FLOAT
import org.lwjgl.opengl.GL30.glBindFragDataLocation

import Callbacks._

object Run extends GameLoop {

  def init(): (Long, Timer, Renderer) = {
    glfwSetErrorCallback(errorCallback)

    if (glfwInit() != GLFW_TRUE) {
      throw new IllegalStateException("Unable to initialize GLFW")
    }

    glfwDefaultWindowHints()
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2)
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
    glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE)
    lazy val window = glfwCreateWindow(640, 480, "Simple example", NULL, NULL)

    if (window == NULL) {
      glfwTerminate()
      throw new RuntimeException("Failed to create the GLFW window")
    }

    glfwMakeContextCurrent(window)
    GL.createCapabilities()

    glfwSetKeyCallback(window, keyCallback)

    val timer = Timer.initializeTimer()

    val renderer = Renderer.initializeRenderer()

    // val state = initStates()

    (window, timer, renderer)
  }

  def dispose(window: Long, renderer: Renderer): Unit = {
    renderer.dispose()
    glfwDestroyWindow(window)
    keyCallback.release()

    glfwTerminate()
    errorCallback.release()
  }

  def main(args: Array[String]): Unit = {
    var madeWindow = -1L
    var madeRenderer: Renderer = null

    try {
      val (window, timer, renderer) = init()
      madeWindow = window
      madeRenderer = renderer
      playGame(window, timer, renderer)
    } finally {
      dispose(madeWindow, madeRenderer)
    }
  }
}
