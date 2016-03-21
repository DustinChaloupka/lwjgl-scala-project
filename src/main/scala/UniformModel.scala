package games.fantasy.lwjgl

sealed trait UniformModel {
  def id: Int
  def updateAngles(delta: Float): UniformModel
}

case class TriangleUniformModel(id: Int, previousAngle: Float = 0f, angle: Float = 0f) extends UniformModel {
  lazy val anglePerSecond = 50f
  def updateAngles(delta: Float): UniformModel = {
    this.copy(previousAngle = angle, angle = angle + delta * anglePerSecond)
  }
}
