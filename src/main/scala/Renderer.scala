package games.fantasy.lwjgl

trait Renderer {
  def render(frameRates: FrameRates, fixedTimeStep: FixedTimeStep): FrameRates = {

    // Last thing probably
    val updatedFrameRates = frameRates.incrementFPSCount()
    updatedFrameRates
  }
}