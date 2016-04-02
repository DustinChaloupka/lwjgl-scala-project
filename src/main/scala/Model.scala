package org.chaloupka.lwjgl
import java.nio.{FloatBuffer, IntBuffer}
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11.glDrawElements

sealed trait Model {
  def id: Int
  def updateAngles(delta: Float): Model
  def vertexUsage: VertexUsage.Value
  def drawMode: DrawMode.Value
  def indexType: IndexType.Value

  lazy val vertexBufferObject = new VertexBufferObject()
  lazy val elementBufferObject = new ElementBufferObject()
  def vertexBufferValues: FloatBuffer
  def elementBufferValues: IntBuffer
  def uploadModelData(): Unit = {
    vertexBufferObject.uploadData(vertexBufferValues, vertexUsage.id)
    elementBufferObject.uploadData(elementBufferValues, vertexUsage.id)
  }

  def draw(): Unit = {
    glDrawElements(drawMode.id, elementBufferValues.limit, indexType.id, 0)
  }

  def delete(): Unit = {
    vertexBufferObject.delete()
    elementBufferObject.delete()
  }
}

trait SingleColorModel extends Model {
  def vertices: List[Coordinates.Position]
  def color: Palette.Color

  lazy val vertexBufferValues = {
    val modelBuffer = BufferUtils.createFloatBuffer(vertices.size * BufferObject.AttributeSize)
    vertices.foreach { vertex =>
      modelBuffer.put(vertex.x).put(vertex.y).put(vertex.z)

      val (red, green, blue, alpha) = color.floatValues
      modelBuffer.put(red).put(green).put(blue).put(alpha)
    }

    modelBuffer.flip()
    modelBuffer
  }
}

trait MultiColoredModel extends Model {
  def vertices: List[Coordinates.Position]
  def colors: List[Palette.Color]
  def verticesWithColors: List[(Coordinates.Position, Palette.Color)] = {
    vertices zip colors
  }

  lazy val vertexBufferValues = {
    val modelBuffer = BufferUtils.createFloatBuffer(verticesWithColors.size * BufferObject.AttributeSize)
    verticesWithColors.foreach { case (vertex, color) =>
      modelBuffer.put(vertex.x).put(vertex.y).put(vertex.z)

      val (red, blue, green, alpha) = color.floatValues
      modelBuffer.put(red).put(blue).put(green).put(alpha)
    }

    modelBuffer.flip()
    modelBuffer
  }
}

trait CubeModel extends Model {
  def vertices: List[Coordinates.Position]
  lazy val drawMode = DrawMode.Triangles
  lazy val indexType = IndexType.UnsignedInt

  lazy val elementBufferValues = {
    val indices = vertices match {
      case vertex :: rest =>
        getIndicesForTrianglesOnFaces(vertex, rest)

      case Nil => Nil
    }

    val elementBuffer = BufferUtils.createIntBuffer(indices.size)
    indices.foreach(elementBuffer.put(_))
    elementBuffer.flip()
    elementBuffer
  }

  // this can probably be better
  def getIndicesForTrianglesOnFaces(firstVertex: Coordinates.Position, rest: List[Coordinates.Position]): List[Int] = {
    val vertexIndex = 0
    val indicesWithVerticesAlongEdges = findVerticesAlongEdges(firstVertex)
    val firstSetOfTriangles = List(vertexIndex, indicesWithVerticesAlongEdges.apply(0)._1, indicesWithVerticesAlongEdges.apply(1)._1,
                                   vertexIndex, indicesWithVerticesAlongEdges.apply(0)._1, indicesWithVerticesAlongEdges.apply(2)._1,
                                   vertexIndex, indicesWithVerticesAlongEdges.apply(1)._1, indicesWithVerticesAlongEdges.apply(2)._1)

    val oppositePlaneIndicesWithVertices = findOppositeVerticesAlongPlane(firstVertex)
    val secondSetOfTriangles = oppositePlaneIndicesWithVertices.foldLeft(List[Int]()) { (acc, indexWithVertex) =>
      val (index, oppositePlaneVertex) = indexWithVertex
      val matchingIndices = indicesWithVerticesAlongEdges.collect {
        case (otherIndex, vertexToCheck) if vertexOnSameXYOrZAxis(vertexToCheck, oppositePlaneVertex) =>
          otherIndex
      }

      acc ++ matchingIndices :+ index
    }

    val alreadyFoundVertices = indicesWithVerticesAlongEdges.map(_._2) ++ oppositePlaneIndicesWithVertices.map(_._2)
    val oppositeVertex = rest.diff(alreadyFoundVertices).head
    val oppositeVertexIndex = vertices.indexOf(oppositeVertex)
    val thirdSetOfTriangles = List(oppositeVertexIndex, oppositePlaneIndicesWithVertices.apply(0)._1, oppositePlaneIndicesWithVertices.apply(1)._1,
                                  oppositeVertexIndex, oppositePlaneIndicesWithVertices.apply(0)._1, oppositePlaneIndicesWithVertices.apply(2)._1,
                                  oppositeVertexIndex, oppositePlaneIndicesWithVertices.apply(1)._1, oppositePlaneIndicesWithVertices.apply(2)._1)

    val lastSetOfTriangles = indicesWithVerticesAlongEdges.foldLeft(List[Int]()) { (acc, indexWithVertex) =>
      val (index, indexAlongEdge) = indexWithVertex
      val matchingIndices = oppositePlaneIndicesWithVertices.collect {
        case (otherIndex, vertexToCheck) if vertexOnSameXYOrZAxis(vertexToCheck, indexAlongEdge) =>
          otherIndex
      }

      acc ++ matchingIndices :+ index
    }

    firstSetOfTriangles ++ secondSetOfTriangles ++ thirdSetOfTriangles ++ lastSetOfTriangles
  }

  def findVerticesAlongEdges(vertex: Coordinates.Position): List[(Int, Coordinates.Position)] = {
    vertices.zipWithIndex.collect {
      case (vertexToCheck, index) if vertexOnSameXYOrZAxis(vertexToCheck, vertex)  && vertexToCheck != vertex =>
        (index, vertexToCheck)
    }
  }

  def findOppositeVerticesAlongPlane(vertex: Coordinates.Position): List[(Int, Coordinates.Position)] = {
    vertices.zipWithIndex.collect {
      case (vertexToCheck, index) if vertexOppositeOnSamePlane(vertexToCheck, vertex) =>
        (index, vertexToCheck)
    }
  }

  def vertexOnSameXYOrZAxis(vertexToCheck: Coordinates.Position, vertex: Coordinates.Position): Boolean = {
    (vertexToCheck.onSameXAxisAs(vertex) && vertexToCheck.x != vertex.x) ||
    (vertexToCheck.onSameYAxisAs(vertex) && vertexToCheck.y != vertex.y) ||
    (vertexToCheck.onSameZAxisAs(vertex) && vertexToCheck.z != vertex.z)
  }

  def vertexOppositeOnSamePlane(vertexToCheck: Coordinates.Position, vertex: Coordinates.Position): Boolean = {
    (vertexToCheck.onSameXYPlaneAs(vertex) && !vertexToCheck.onSameXAxisAs(vertex) && !vertexToCheck.onSameYAxisAs(vertex)) ||
    (vertexToCheck.onSameXZPlaneAs(vertex) && !vertexToCheck.onSameXAxisAs(vertex) && !vertexToCheck.onSameZAxisAs(vertex)) ||
    (vertexToCheck.onSameYZPlaneAs(vertex) && !vertexToCheck.onSameYAxisAs(vertex) && !vertexToCheck.onSameZAxisAs(vertex))
  }
}

case class SingleColorCubeModel(id: Int, previousAngle: Float= 0f, angle: Float = 0f) extends SingleColorModel with CubeModel {
  lazy val center = Coordinates.XYZ(0f, 0f, 0f)
  lazy val faceDistance = 0.25f
  lazy val vertices = List(Coordinates.XYZ(faceDistance, faceDistance, faceDistance),
                           Coordinates.XYZ(-faceDistance, faceDistance, faceDistance),
                           Coordinates.XYZ(faceDistance, -faceDistance, faceDistance),
                           Coordinates.XYZ(faceDistance, faceDistance, -faceDistance),
                           Coordinates.XYZ(-faceDistance, -faceDistance, faceDistance),
                           Coordinates.XYZ(-faceDistance, faceDistance, -faceDistance),
                           Coordinates.XYZ(faceDistance, -faceDistance, -faceDistance),
                           Coordinates.XYZ(-faceDistance, -faceDistance, -faceDistance))
  lazy val color = Palette.White()
  lazy val vertexUsage = VertexUsage.Static

  lazy val anglePerSecond = 50f
  def updateAngles(delta: Float): Model = {
    this.copy(previousAngle = angle, angle = angle + delta * anglePerSecond)
  }
}

case class MultiColoredCubeModel(id: Int, previousAngle: Float= 0f, angle: Float = 0f) extends MultiColoredModel with CubeModel {
  lazy val center = Coordinates.XYZ(0f, 0f, 0f)
  lazy val faceDistance = 0.25f
  lazy val vertices = List(Coordinates.XYZ(faceDistance, faceDistance, faceDistance),
                           Coordinates.XYZ(-faceDistance, faceDistance, faceDistance),
                           Coordinates.XYZ(faceDistance, -faceDistance, faceDistance),
                           Coordinates.XYZ(faceDistance, faceDistance, -faceDistance),
                           Coordinates.XYZ(-faceDistance, -faceDistance, faceDistance),
                           Coordinates.XYZ(-faceDistance, faceDistance, -faceDistance),
                           Coordinates.XYZ(faceDistance, -faceDistance, -faceDistance),
                           Coordinates.XYZ(-faceDistance, -faceDistance, -faceDistance))
  lazy val colors = List(Palette.Red(255),
                         Palette.Green(255),
                         Palette.Blue(255),
                         Palette.RedBlue(255, 255),
                         Palette.BlueGreen(255, 255),
                         Palette.RedGreen(255, 255),
                         Palette.RedBlueGreen(122, 122, 122),
                         Palette.BlueGreen(100, 200))
  lazy val vertexUsage = VertexUsage.Static

  lazy val anglePerSecond = 50f
  def updateAngles(delta: Float): Model = {
    this.copy(previousAngle = angle, angle = angle + delta * anglePerSecond)
  }
}
