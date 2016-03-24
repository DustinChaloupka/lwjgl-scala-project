package org.chaloupka.lwjgl

object Updater {
  @annotation.tailrec
  final def tryUpdating(fixedTimeStep: FixedTimeStep, frameRates: FrameRates, renderer: Renderer, delta: Float): (FixedTimeStep, FrameRates, Renderer) = {
    if (fixedTimeStep.canUpdate) {
      val (updatedUpdateFrameRates, updatedRenderer) = update(frameRates, delta, renderer)
      val updatedFixedTimeStep = fixedTimeStep.subtractIntervalFromAccumulator()
      tryUpdating(updatedFixedTimeStep, updatedUpdateFrameRates, updatedRenderer, delta)
    } else {
      val updatedFixedTimeStep = fixedTimeStep.updateAlpha()
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
