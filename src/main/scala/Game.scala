package org.chaloupka.lwjgl
import org.chaloupka.lwjgl.Callbacks._
import org.lwjgl.glfw.GLFW._

object Game extends GameLoop {
  def play(window: Window, timer: Timer, renderer: Renderer): Unit = {
    runLoop(window, timer, renderer, FrameRates(0, 0, 0, 0))
  }

  def init(): (Window, Timer, Renderer) = {
    glfwSetErrorCallback(errorCallback)

    if (glfwInit() != GLFW_TRUE) {
      throw new IllegalStateException("Unable to initialize GLFW")
    }

    val window = new Window(640, 480, "Game")

    window.setDefaultWindowHints()
    window.makeContextCurrent()
    window.createCapabilities()
    window.setKeyCallback(keyCallback)

    val timer = Timer()

    val renderer = Renderer.initializeRenderer()

    // val state = initStates()

    (window, timer, renderer)
  }

  def dispose(window: Window, renderer: Renderer): Unit = {
    renderer.dispose()
    keyCallback.release()
    errorCallback.release()
    window.dispose()
    glfwTerminate()
  }
}
