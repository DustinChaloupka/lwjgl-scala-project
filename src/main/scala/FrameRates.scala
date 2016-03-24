package org.chaloupka.lwjgl

case class FrameRates(fps: Int, fpsCount: Int, ups: Int, upsCount: Int) {
  def incrementFPSCount(): FrameRates = this.copy(fps, fpsCount + 1, ups, upsCount)
  def incrementUPSCount(): FrameRates = this.copy(fps, fpsCount, ups, upsCount + 1)
  def resetPerSecondCounts(): FrameRates = this.copy(fpsCount, 0, upsCount, 0)
}
