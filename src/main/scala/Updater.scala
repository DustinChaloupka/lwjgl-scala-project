package org.chaloupka.lwjgl

object Updater {
  @annotation.tailrec
  final def tryUpdating(fixedTimeStep: FixedTimeStep, frameRates: FrameRates, renderer: Renderer, interval: Float, delta: Float): (FixedTimeStep, FrameRates, Renderer) = {
    if (fixedTimeStep.accumulator >= interval) {
      val (updatedUpdateFrameRates, updatedRenderer) = update(frameRates, delta, renderer)
      val updatedFixedTimeStep = fixedTimeStep.subtractIntervalFromAccumulator(interval)
      tryUpdating(updatedFixedTimeStep, updatedUpdateFrameRates, updatedRenderer, interval, delta)
    } else {
      val updatedFixedTimeStep = fixedTimeStep.updateAlpha(interval)
      (updatedFixedTimeStep, frameRates, renderer)
    }
  }

  def update(frameRates: FrameRates, delta: Float, renderer: Renderer): (FrameRates, Renderer) = {
    val updatedUniformModel = renderer.uniformModel.updateAngles(delta)
    val updatedRenderer = renderer.updateUniformModel(updatedUniformModel)

    // Last thing probably
    val updatedFrameRates = frameRates.incrementUPSCount()
    (updatedFrameRates, updatedRenderer)
  }
}
