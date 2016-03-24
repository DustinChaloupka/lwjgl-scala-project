package org.chaloupka.lwjgl
import org.lwjgl.glfw.GLFW._
import GameAttributes.targetUPS

trait GameLoop {
  @annotation.tailrec
  final def runLoop(window: Window, timer: Timer, renderer: Renderer, frameRates: FrameRates, fixedTimeStep: FixedTimeStep = FixedTimeStep()): Unit = {
    // Where does this go exactly?
    val updatedFixedTimeStep = fixedTimeStep.addDeltaToAccumulator(timer.delta)

    // Goes in input?
    glfwSwapBuffers(window.id)
    glfwPollEvents()

    Inputer.input()

    val (fullyUpdatedFixedTimeStep, updatedUpdateFrameRates, updatedUpdateRenderer) = Updater.tryUpdating(updatedFixedTimeStep, frameRates, renderer, timer)

    val updatedRenderFrameRates = Renderer.render(updatedUpdateFrameRates, fullyUpdatedFixedTimeStep, renderer)

    val oneSecondHasPassed = timer.timeCount > 1f
    val fullyUpdatedFrameRates = frameRates.maybeResetPerSecondCounts(oneSecondHasPassed)
    val updatedTimer = timer.update(oneSecondHasPassed)

    if (glfwWindowShouldClose(window.id) != GLFW_TRUE) {
      runLoop(window, updatedTimer, updatedUpdateRenderer, fullyUpdatedFrameRates, fullyUpdatedFixedTimeStep)
    }
  }
}
