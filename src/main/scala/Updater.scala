package org.chaloupka.lwjgl

object Updater {
  @annotation.tailrec
  final def tryUpdating(fixedTimeStep: FixedTimeStep, frameRates: FrameRates, renderer: Renderer, timer: Timer): (FixedTimeStep, FrameRates, Renderer) = {
    if (fixedTimeStep.canUpdate) {
      val (updatedUpdateFrameRates, updatedRenderer) = update(frameRates, timer, renderer)
      val updatedFixedTimeStep = fixedTimeStep.subtractIntervalFromAccumulator()
      tryUpdating(updatedFixedTimeStep, updatedUpdateFrameRates, updatedRenderer, timer)
    } else {
      val updatedFixedTimeStep = fixedTimeStep.updateAlpha()
      (updatedFixedTimeStep, frameRates, renderer)
    }
  }

  def update(frameRates: FrameRates, timer: Timer, renderer: Renderer): (FrameRates, Renderer) = {
    val updatedModel = renderer.model.updateAngles(timer.delta)
    val updatedRenderer = renderer.updateModel(updatedModel)

    // Last thing probably
    val updatedFrameRates = frameRates.incrementUPSCount()
    (updatedFrameRates, updatedRenderer)
  }
}
