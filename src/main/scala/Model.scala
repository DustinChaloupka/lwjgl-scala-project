package org.chaloupka.lwjgl
import java.nio.{FloatBuffer, IntBuffer}
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11.glDrawElements

sealed trait Model {
  def id: Int
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

  private[this] var previousAngle: Float = 0f
  private[this] var angle: Float = 0f
  def anglesPerSecond: Float
  def updateAngles(delta: Float): Unit = {
    previousAngle = angle
    angle = angle + delta * anglesPerSecond
  }

  def getLerpAngle(alpha: Float): Float = {
    (1f - alpha) * previousAngle + alpha * angle
  }
}

trait ColoredModel extends Model {
  def verticesWithColors: List[(Coordinates.Position, Palette.Color)]
  lazy val vertices = verticesWithColors.map(_._1)

  lazy val vertexBufferValues = {
    val modelBuffer = BufferUtils.createFloatBuffer(verticesWithColors.size * BufferObject.AttributeSize)
    verticesWithColors.foreach { case (vertex, color) =>
      modelBuffer.put(vertex.x).put(vertex.y).put(vertex.z)

      val (red, green, blue, alpha) = color.floatValues
      modelBuffer.put(red).put(green).put(blue).put(alpha)
    }

    modelBuffer.flip()
    modelBuffer
  }
}

trait HexahedronModel extends Model {
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

case class XAxisHexahedronModel(id: Int) extends ColoredModel with HexahedronModel {
  lazy val color = Palette.Red(255)
  lazy val verticesWithColors = List((Coordinates.XYZ(0.5f, 0.1f, 0.1f), color),
                                     (Coordinates.XYZ(-0.5f, 0.1f, 0.1f), color),
                                     (Coordinates.XYZ(0.5f, -0.1f, 0.1f), color),
                                     (Coordinates.XYZ(0.5f, 0.1f, -0.1f), color),
                                     (Coordinates.XYZ(-0.5f, -0.1f, 0.1f), color),
                                     (Coordinates.XYZ(-0.5f, 0.1f, -0.1f), color),
                                     (Coordinates.XYZ(0.5f, -0.1f, -0.1f), color),
                                     (Coordinates.XYZ(-0.5f, -0.1f, -0.1f), color))
  lazy val vertexUsage = VertexUsage.Static
  lazy val anglesPerSecond = 50f
}

case class YAxisHexahedronModel(id: Int) extends ColoredModel with HexahedronModel {
  lazy val color = Palette.Green(255)
  lazy val verticesWithColors = List((Coordinates.XYZ(0.1f, 0f, 0.1f), color),
                                     (Coordinates.XYZ(-0.1f, 0f, 0.1f), color),
                                     (Coordinates.XYZ(0.1f, -0.5f, 0.1f), color),
                                     (Coordinates.XYZ(0.1f, 0f, -0.1f), color),
                                     (Coordinates.XYZ(-0.1f, -0.5f, 0.1f), color),
                                     (Coordinates.XYZ(-0.1f, 0f, -0.1f), color),
                                     (Coordinates.XYZ(0.1f, -0.5f, -0.1f), color),
                                     (Coordinates.XYZ(-0.1f, -0.5f, -0.1f), color))
  lazy val vertexUsage = VertexUsage.Static
  lazy val anglesPerSecond = 50f
}

case class ZAxisHexahedronModel(id: Int) extends ColoredModel with HexahedronModel {
  lazy val color = Palette.Blue(255)
  lazy val verticesWithColors = List((Coordinates.XYZ(0.1f, 0.1f, 0f), color),
                                     (Coordinates.XYZ(-0.1f, 0.1f, 0f), color),
                                     (Coordinates.XYZ(0.1f, -0.1f, 0f), color),
                                     (Coordinates.XYZ(0.1f, 0.1f, -0.5f), color),
                                     (Coordinates.XYZ(-0.1f, -0.1f, 0f), color),
                                     (Coordinates.XYZ(-0.1f, 0.1f, -0.5f), color),
                                     (Coordinates.XYZ(0.1f, -0.1f, -0.5f), color),
                                     (Coordinates.XYZ(-0.1f, -0.1f, -0.5f), color))
  lazy val vertexUsage = VertexUsage.Static
  lazy val anglesPerSecond = 50f
}

case class MultiColoredCubeModel(id: Int) extends ColoredModel with HexahedronModel {
  lazy val center = Coordinates.XYZ(0f, 0f, 0f)
  lazy val faceDistance = 0.25f
  lazy val verticesWithColors = List((Coordinates.XYZ(faceDistance, faceDistance, faceDistance), Palette.Red(255)),
                                     (Coordinates.XYZ(-faceDistance, faceDistance, faceDistance), Palette.Red(255)),
                                     (Coordinates.XYZ(faceDistance, -faceDistance, faceDistance), Palette.Red(255)),
                                     (Coordinates.XYZ(faceDistance, faceDistance, -faceDistance), Palette.Blue(255)),
                                     (Coordinates.XYZ(-faceDistance, -faceDistance, faceDistance), Palette.Red(255)),
                                     (Coordinates.XYZ(-faceDistance, faceDistance, -faceDistance), Palette.Blue(255)),
                                     (Coordinates.XYZ(faceDistance, -faceDistance, -faceDistance), Palette.Green(255)),
                                     (Coordinates.XYZ(-faceDistance, -faceDistance, -faceDistance), Palette.Green(255)))
  lazy val vertexUsage = VertexUsage.Static
  lazy val anglesPerSecond = 50f
}
