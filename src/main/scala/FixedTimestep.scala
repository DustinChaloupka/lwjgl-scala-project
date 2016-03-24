package org.chaloupka.lwjgl

case class FixedTimeStep(accumulator: Float = 0f, alpha: Float = 0f) {
  def addDeltaToAccumulator(delta: Float): FixedTimeStep = this.copy(accumulator + delta, alpha)
  def subtractIntervalFromAccumulator(interval: Float): FixedTimeStep = this.copy(accumulator - interval, alpha)
  def updateAlpha(interval: Float): FixedTimeStep = this.copy(accumulator, accumulator / interval)
}
