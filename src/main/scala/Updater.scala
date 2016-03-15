package games.fantasy.lwjgl

trait Updater {
  def update(frameRates: FrameRates): FrameRates = {

    // Last thing probably
    val updatedFrameRates = frameRates.incrementUPSCount()
    updatedFrameRates
  }
}