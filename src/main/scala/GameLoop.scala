package games.fantasy.lwjgl
import org.lwjgl.glfw.GLFW._
import GameAttributes.targetUPS

trait GameLoop {
  def playGame(window: Long, timer: Timer, renderer: Renderer): Unit = {
    runLoop(window, timer, renderer, FrameRates(0, 0, 0, 0))
  }

  @annotation.tailrec
  final def runLoop(window: Long, timer: Timer, renderer: Renderer, frameRates: FrameRates, fixedTimeStep: FixedTimeStep = FixedTimeStep(), interval: Float = 1f / targetUPS): Unit = {
    // Where does this go exactly?
    val (delta, updatedTimer) = Timer.getDeltaAndUpdatedTimer(timer)
    val updatedFixedTimeStep = fixedTimeStep.addDeltaToAccumulator(delta)

    // Goes in input?
    glfwSwapBuffers(window)
    glfwPollEvents()

    Inputer.input()

    val (fullyUpdatedFixedTimeStep, updatedUpdateFrameRates, updatedUpdateRenderer) = Updater.tryUpdating(updatedFixedTimeStep, frameRates, renderer, interval, delta)

    val updatedRenderFrameRates = Renderer.render(updatedUpdateFrameRates, fullyUpdatedFixedTimeStep, renderer)

    val (fullyUpdatedFrameRates, fullyUpdatedTimer) = Timer.updateFrameRatesAndTimer(updatedRenderFrameRates, updatedTimer)

    if (glfwWindowShouldClose(window) != GLFW_TRUE) {
      runLoop(window, fullyUpdatedTimer, updatedUpdateRenderer, fullyUpdatedFrameRates, fullyUpdatedFixedTimeStep)
    }
  }
}
