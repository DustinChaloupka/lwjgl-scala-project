package org.chaloupka.lwjgl

case class FrameRates(currentFPS: Int, fpsCount: Int, currentUPS: Int, upsCount: Int) {
  def incrementFPSCount(): FrameRates = this.copy(fpsCount = fpsCount + 1)
  def incrementUPSCount(): FrameRates = this.copy(upsCount = upsCount + 1)
  def maybeResetPerSecondCounts(shouldReset: Boolean): FrameRates = {
    if (shouldReset) {
      this.copy(fpsCount, 0, upsCount, 0)
    } else {
      this
    }
  }
}
