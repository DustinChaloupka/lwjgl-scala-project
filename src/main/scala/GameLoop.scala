package org.chaloupka.lwjgl
import org.lwjgl.glfw.GLFW._
import GameAttributes.targetUPS

trait GameLoop {
  @annotation.tailrec
  final def runLoop(window: Window, timer: Timer, renderer: Renderer, frameRates: FrameRates, fixedTimeStep: FixedTimeStep = FixedTimeStep()): Unit = {
    // Where does this go exactly?
    val (delta, updatedTimer) = Timer.getDeltaAndUpdatedTimer(timer)
    val updatedFixedTimeStep = fixedTimeStep.addDeltaToAccumulator(delta)

    // Goes in input?
    glfwSwapBuffers(window.id)
    glfwPollEvents()

    Inputer.input()

    val (fullyUpdatedFixedTimeStep, updatedUpdateFrameRates, updatedUpdateRenderer) = Updater.tryUpdating(updatedFixedTimeStep, frameRates, renderer, delta)

    val updatedRenderFrameRates = Renderer.render(updatedUpdateFrameRates, fullyUpdatedFixedTimeStep, renderer)

    val (fullyUpdatedFrameRates, fullyUpdatedTimer) = Timer.updateFrameRatesAndTimer(updatedRenderFrameRates, updatedTimer)

    if (glfwWindowShouldClose(window.id) != GLFW_TRUE) {
      runLoop(window, fullyUpdatedTimer, updatedUpdateRenderer, fullyUpdatedFrameRates, fullyUpdatedFixedTimeStep)
    }
  }
}
