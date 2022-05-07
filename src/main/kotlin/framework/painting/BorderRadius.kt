package framework.painting

import org.jetbrains.skia.RRect
import org.jetbrains.skia.Rect

class BorderRadius private constructor(
    val topLeft: Radius = Radius.zero,
    val topRight: Radius = Radius.zero,
    val bottomLeft: Radius = Radius.zero,
    val bottomRight: Radius = Radius.zero,
) {
    companion object {
        fun all(radius: Radius): BorderRadius = BorderRadius(radius, radius, radius, radius)

        fun circular(radius: Double): BorderRadius = all(Radius.circular(radius))

        fun only(
            topLeft: Radius = Radius.zero,
            topRight: Radius = Radius.zero,
            bottomLeft: Radius = Radius.zero,
            bottomRight: Radius = Radius.zero,
        ): BorderRadius = BorderRadius(topLeft, topRight, bottomLeft, bottomRight)

        val zero = all(Radius.zero)
    }

    fun toRRect(rect: Rect): RRect {
        return RRect.makeComplexLTRB(rect.left,
            rect.top,
            rect.right,
            rect.bottom,
            floatArrayOf(topLeft.x.toFloat(),
                topLeft.y.toFloat(),
                topRight.x.toFloat(),
                topRight.y.toFloat(),
                bottomRight.x.toFloat(),
                bottomRight.y.toFloat(),
                bottomLeft.x.toFloat(),
                bottomLeft.y.toFloat()))
    }
}

/**
 * 角丸の設定用オブジェクト
 *
 * x, y軸ごとに半径を指定
 */
class Radius private constructor(
    val x: Double, val y: Double,
) {
    companion object {
        fun circular(radius: Double): Radius = Radius(radius, radius)

        fun elliptical(x: Double, y: Double) = Radius(x, y)

        val zero = Radius(0.0, 0.0)
    }
}