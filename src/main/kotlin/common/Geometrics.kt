package common

import org.jetbrains.skia.Matrix33
import org.jetbrains.skia.Rect
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round

abstract class OffsetBase(
    private val dx: Double, private val dy: Double
) {
    val isInfinite: Boolean
        get() = dx >= Double.POSITIVE_INFINITY || dy >= Double.POSITIVE_INFINITY

    val isFinite: Boolean
        get() = dx.isFinite() && dy.isFinite()
}

class Size(
    val width: Double, val height: Double
) : OffsetBase(width, height) {
    companion object {
        val zero = Size(0.0, 0.0)
    }

    val isEmpty = width <= 0.0 || height <= 0.0

    operator fun minus(other: OffsetBase): OffsetBase {
        if(other is Offset) {
            return Size(width - other.dx, height - other.dy)
        }
        if(other is Size) {
            return Offset(width - other.width, height - other.height)
        }
        throw IllegalArgumentException()
    }

    fun and(other: Offset): Rect {
        return Rect.makeXYWH(other.dx.toFloat(), other.dy.toFloat(), width.toFloat(), height.toFloat())
    }

    fun contains(offset: Offset): Boolean {
        return offset.dx >= 0.0 && offset.dx < width && offset.dy >= 0.0 && offset.dy < height
    }
}

class Offset(val dx: Double, val dy: Double) : OffsetBase(dx, dy) {
    companion object {
        val zero = Offset(0.0, 0.0)
    }

    operator fun plus(other: Offset): Offset {
        return Offset(dx + other.dx, dy + other.dy)
    }

    operator fun minus(other: Offset): Offset {
        return Offset(dx - other.dx, dy - other.dy)
    }

    operator fun unaryMinus(): Offset {
        return Offset(-dx, -dy)
    }

    override fun toString(): String {
        return "Offset(dx:$dx, dy:$dy)"
    }
}

// Rect Extensions

/**
 * 矩形の和(Union)を取る
 */
fun Rect.join(other: Rect): Rect {
    return Rect(
        min(this.left, other.left),
        min(this.top, other.top),
        max(this.right, other.right),
        max(this.bottom, other.bottom)
    )
}

/**
 * 矩形をOffsetだけずらす
 *
 * dxが正なら右へ、dyが正なら下へずれる
 */
fun Rect.makeOffset(dx: Float, dy: Float): Rect {
    return Rect(
        this.left + dx, this.top + dy, this.right + dx, this.bottom + dy
    )
}

fun Rect.makeOffset(offset: Offset): Rect {
    return this.makeOffset(offset.dx.toFloat(), offset.dy.toFloat())
}

fun Rect.roundOut(): Rect {
    return Rect(
        round(this.left), round(this.top), round(this.right), round(this.bottom)
    )
}

val kEmptyRect: Rect = Rect.makeWH(0f, 0f)

val kGiantRect: Rect = Rect.makeLTRB(-1e9f, -1e9f, 1e9f, 1e9f)

// Matrix33 Extensions

/**
 * 逆行列があれば逆行列を返す
 */
fun Matrix33.invert(): Matrix33? {
    val mat = this.mat
    val a11 = mat[0]
    val a12 = mat[1]
    val a13 = mat[2]
    val a21 = mat[3]
    val a22 = mat[4]
    val a23 = mat[5]
    val a31 = mat[6]
    val a32 = mat[7]
    val a33 = mat[8]
    val det = a11 * a22 * a33 + a12 * a23 * a31 + a13 * a21 * a32
    -a13 * a22 * a31 - a12 * a21 * a33 - a11 * a23 * a32
    if(abs(det) <= 1e-4) return null
    return Matrix33(
        (a22 * a33 - a23 * a22) / det,
        -(a12 * a33 - a13 * a22) / det,
        (a12 * a23 - a13 * a22) / det,
        -(a21 * a33 - a23 * a31) / det,
        (a11 * a33 - a13 * a31) / det,
        -(a11 * a23 - a13 * a21) / det,
        (a21 * a32 - a22 * a31) / det,
        -(a11 * a32 - a12 * a31) / det,
        (a11 * a22 - a12 * a21) / det
    )
}

/**
 * Rectに変換行列を適用した結果を返す
 */
fun Matrix33.mapRect(rect: Rect): Rect {
    val topLeft = Offset(rect.left.toDouble(), rect.top.toDouble())
    val topRight = Offset(rect.right.toDouble(), rect.top.toDouble())
    val bottomLeft = Offset(rect.left.toDouble(), rect.bottom.toDouble())
    val bottomRight = Offset(rect.right.toDouble(), rect.bottom.toDouble())
    val transformedTopLeft = transform(topLeft)
    val transformedTopRight = transform(topRight)
    val transformedBottomLeft = transform(bottomLeft)
    val transformedBottomRight = transform(bottomRight)
    return Rect(
        minOf(
            transformedTopLeft.dx, transformedTopRight.dx, transformedBottomLeft.dx, transformedBottomRight.dx
        ).toFloat(),
        minOf(
            transformedTopLeft.dy, transformedTopRight.dy, transformedBottomLeft.dy, transformedBottomRight.dy
        ).toFloat(),
        maxOf(
            transformedTopLeft.dx, transformedTopRight.dx, transformedBottomLeft.dx, transformedBottomRight.dx
        ).toFloat(),
        maxOf(
            transformedTopLeft.dy, transformedTopRight.dy, transformedBottomLeft.dy, transformedBottomRight.dy
        ).toFloat(),
    )
}

fun Matrix33.transform(offset: Offset): Offset {
    val mat = this.mat
    val a11 = mat[0]
    val a12 = mat[1]
    val a13 = mat[2]
    val a21 = mat[3]
    val a22 = mat[4]
    val a23 = mat[5]
    val a31 = mat[6]
    val a32 = mat[7]
    val a33 = mat[8]
    val x = a11 * offset.dx + a12 * offset.dy + a13
    val y = a21 * offset.dx + a22 * offset.dy + a23
    val scale = a31 * offset.dx + a32 * offset.dy + a33
    return Offset(x / scale, y / scale)
}
