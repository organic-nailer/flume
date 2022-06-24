package framework.geometrics

import common.Offset
import common.Size

class Alignment(val x: Double, val y: Double) {
    companion object {
        val topLeft = Alignment(-1.0, -1.0)
        val topCenter = Alignment(0.0, -1.0)
        val topRight = Alignment(1.0, -1.0)
        val centerLeft = Alignment(-1.0, 0.0)
        val center = Alignment(0.0, 0.0)
        val centerRight = Alignment(1.0, 0.0)
        val bottomLeft = Alignment(-1.0, 1.0)
        val bottomCenter = Alignment(0.0, 1.0)
        val bottomRight = Alignment(1.0, 1.0)
    }

    private fun alongOffset(other: Offset): Offset {
        val centerX = other.dx / 2.0
        val centerY = other.dy / 2.0
        return Offset((1 + x) * centerX, (1 + y) * centerY)
    }

    fun computeOffset(parentSize: Size, childSize: Size): Offset {
        val offsetIfRightBottom = (parentSize - childSize) as Offset
        return alongOffset(offsetIfRightBottom)
    }
}