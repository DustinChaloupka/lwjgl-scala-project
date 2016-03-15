package games.fantasy.lwjgl

case class FixedTimeStep(accumulator: Double = 0, alpha: Double = 0) {
  def addDeltaToAccumulator(delta: Double): FixedTimeStep = this.copy(accumulator + delta, alpha)
  def subtractIntervalFromAccumulator(interval: Double): FixedTimeStep = this.copy(accumulator - interval, alpha)
  def updateAlpha(interval: Double): FixedTimeStep = this.copy(accumulator, accumulator / interval)
}