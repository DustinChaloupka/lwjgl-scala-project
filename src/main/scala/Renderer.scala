package org.chaloupka.lwjgl
import java.nio.Buffer
import org.lwjgl.opengl.GL15._
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL20._
import org.lwjgl.BufferUtils

case class Renderer(shaderProgram: ShaderProgram,
                    models: List[Model]) {
  def dispose(): Unit = {
    shaderProgram.delete()
    models.foreach(_.delete())
  }
}

object Renderer {
  def initializeRenderer(): Renderer = {
    val shaderProgram = new DefaultShaderProgram()
    shaderProgram.init()
    shaderProgram.use()

    val uniformModelLocation = shaderProgram.getUniformLocation("model")
    val xAxisModel = XAxisHexahedronModel(uniformModelLocation)
    // val yAxisModel = YAxisHexahedronModel(uniformModelLocation)
    // val zAxisModel = ZAxisHexahedronModel(uniformModelLocation)

    val model = Matrix4.identity()
    shaderProgram.setUniformMatrix4(uniformModelLocation, model)

    val uniformViewLocation = shaderProgram.getUniformLocation("view")
    val view = Matrix4.identity()
    shaderProgram.setUniformMatrix4(uniformViewLocation, view)

    val uniformProjectionLocation = shaderProgram.getUniformLocation("projection")
    val ratio = 640f / 480f
    val projection = Matrix4.orthographic(-ratio, ratio, -1f, 1f, -1f, 1f)
    shaderProgram.setUniformMatrix4(uniformProjectionLocation, projection)

    xAxisModel.uploadModelData()
    // yAxisModel.uploadModelData()
    // zAxisModel.uploadModelData()

    shaderProgram.bindFragDataLocation(0, "fragColor")
    shaderProgram.specifyVertexAttributes()

    Renderer(shaderProgram, List(xAxisModel))
  }

  def render(frameRates: FrameRates, fixedTimeStep: FixedTimeStep, renderer: Renderer): FrameRates = {
    glClear(GL_COLOR_BUFFER_BIT)

    renderer.models.map { model =>
      val lerpAngle = model.getLerpAngle(fixedTimeStep.alpha)
      val rotationMatrix = Matrix4.rotation(lerpAngle, 0f, 1f, 0f)
      renderer.shaderProgram.setUniformMatrix4(model.id, rotationMatrix)
      model.draw()
    }

    // Last thing probably
    val updatedFrameRates = frameRates.incrementFPSCount()
    updatedFrameRates
  }
}
