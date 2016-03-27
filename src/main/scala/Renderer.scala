package org.chaloupka.lwjgl
import java.nio.Buffer
import org.lwjgl.opengl.GL15._
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL20._
import org.lwjgl.BufferUtils

case class Renderer(shaderProgram: ShaderProgram,
                    model: Model) {
  def dispose(): Unit = {
    shaderProgram.delete()
    model.delete()
  }

  def updateModel(model: Model): Renderer = {
    this.copy(model = model)
  }
}

object Renderer {
  def initializeRenderer(): Renderer = {
    val vertexArrayObject = new VertexArrayObject()
    vertexArrayObject.bind()

    val vertexShader = new VertexShader()
    val vertexShaderCompiledSuccessfully = vertexShader.init()
    if (!vertexShaderCompiledSuccessfully) {
      println(s"Vertex shader did not compile correctly!")
    }

    val fragmentShader = new FragmentShader()
    val fragmentShaderCompiledSuccessfully = fragmentShader.init()
    if (!fragmentShaderCompiledSuccessfully) {
      println(s"Fragment shader did not compile correctly!")
    }

    val shaderProgram = ShaderProgram(List(vertexShader, fragmentShader), vertexArrayObject)
    shaderProgram.attachShaders()
    shaderProgram.link()
    shaderProgram.use()

    val uniformModelLocation = shaderProgram.getUniformLocation("model")
    val cubeModel = MultiColoredCubeModel(uniformModelLocation)

    val model = Matrix4.identity()
    shaderProgram.setUniformMatrix4(uniformModelLocation, model)

    val uniformViewLocation = shaderProgram.getUniformLocation("view")
    val view = Matrix4.identity()
    shaderProgram.setUniformMatrix4(uniformViewLocation, view)

    val uniformProjectionLocation = shaderProgram.getUniformLocation("projection")
    val ratio = 640f / 480f
    val projection = Matrix4.orthographic(-ratio, ratio, -1f, 1f, -1f, 1f)
    shaderProgram.setUniformMatrix4(uniformProjectionLocation, projection)

    cubeModel.uploadModelData()

    shaderProgram.bindFragDataLocation(0, "fragColor")
    shaderProgram.specifyVertexAttributes()

    Renderer(shaderProgram, cubeModel)
  }

  def render(frameRates: FrameRates, fixedTimeStep: FixedTimeStep, renderer: Renderer): FrameRates = {
    glClear(GL_COLOR_BUFFER_BIT)

    renderer.model match {
      case MultiColoredCubeModel(id, previousAngle, angle) =>
        val triangleLerpAngle = (1f - fixedTimeStep.alpha) * previousAngle + fixedTimeStep.alpha * angle
        val rotateModel = Matrix4.rotate(triangleLerpAngle, 0f, 0.25f, 0.25f)
        renderer.shaderProgram.setUniformMatrix4(id, rotateModel)
    }

    glDrawElements(GL_TRIANGLES, 36, GL_UNSIGNED_INT, 0)

    // Last thing probably
    val updatedFrameRates = frameRates.incrementFPSCount()
    updatedFrameRates
  }
}
