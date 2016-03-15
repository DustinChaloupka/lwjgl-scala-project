package games.fantasy.lwjgl
import org.lwjgl.glfw.GLFW._
import GameAttributes.targetUPS

trait GameLoop extends Timer with Updater with Inputer with Renderer {
  def playGame(window: Long): Unit = {
    runLoop(window, Times(getTime, 0), FrameRates(0, 0, 0, 0))
  }

  @annotation.tailrec
  final def runLoop(window: Long, times: Times, frameRates: FrameRates, fixedTimeStep: FixedTimeStep = FixedTimeStep(), interval: Double = 1.0 / targetUPS): Unit = {
    // Where does this go exactly?
    val (delta, updatedTimes) = getDeltaAndUpdatedTimes(times)
    val updatedFixedTimeStep = fixedTimeStep.addDeltaToAccumulator(delta)

    // Goes in input?
    glfwSwapBuffers(window)
    glfwPollEvents()

    input()

    val (fullyUpdatedFixedTimeStep, updatedUpdateFrameRates) = tryUpdating(updatedFixedTimeStep, frameRates, interval)

    val updatedRenderFrameRates = render(updatedUpdateFrameRates, fullyUpdatedFixedTimeStep)

    // Maybe do this here?
    val (fullyUpdatedFrameRates, fullyUpdatedTimes) = updateFrameRatesAndTimes(updatedRenderFrameRates, updatedTimes)

    if (glfwWindowShouldClose(window) != GLFW_TRUE) {
      runLoop(window, fullyUpdatedTimes, fullyUpdatedFrameRates, fullyUpdatedFixedTimeStep)
    }
  }

  @annotation.tailrec
  final def tryUpdating(fixedTimeStep: FixedTimeStep, frameRates: FrameRates, interval: Double): (FixedTimeStep, FrameRates) = {
    if (fixedTimeStep.accumulator >= interval) {
      val updatedUpdateFrameRates = update(frameRates)
      val updatedFixedTimeStep = fixedTimeStep.subtractIntervalFromAccumulator(interval)
      tryUpdating(updatedFixedTimeStep, updatedUpdateFrameRates, interval)
    } else {
      val updatedFixedTimeStep = fixedTimeStep.updateAlpha(interval)
      (updatedFixedTimeStep, frameRates)
    }
  }
}