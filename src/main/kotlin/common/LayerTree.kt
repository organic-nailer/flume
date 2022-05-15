package common

import org.jetbrains.skia.Canvas
import org.jetbrains.skia.DirectContext
import org.jetbrains.skia.Matrix33
import org.jetbrains.skia.Paint
import org.jetbrains.skia.Path
import org.jetbrains.skia.Picture
import org.jetbrains.skia.RRect
import org.jetbrains.skia.Rect

class LayerTree {
    var rootLayer: Layer? = null

    /**
     * LayerツリーのそれぞれのLayerのpaintBoundsを決定する
     */
    fun preroll() {
        assert(rootLayer != null)

        val context = PrerollContext(kGiantRect)
        rootLayer!!.preroll(context, Matrix33.IDENTITY)
    }

    /**
     * PaintContextを用いてLayerツリーを描画する
     */
    fun paint(context: PaintContext) {
        rootLayer?.paint(context)
    }
}

abstract class Layer {
    var paintBounds: Rect = Rect.makeWH(0f, 0f)

    abstract fun paint(context: PaintContext)
    abstract fun preroll(context: PrerollContext, matrix: Matrix33)
}

open class ContainerLayer : Layer() {
    val children: MutableList<Layer> = mutableListOf()

    override fun preroll(context: PrerollContext, matrix: Matrix33) {
        paintBounds = prerollChildren(context, matrix)
    }

    /**
     * 子の矩形を計算しその和を返す
     */
    protected fun prerollChildren(context: PrerollContext, childMatrix: Matrix33): Rect {
        var bounds = kEmptyRect
        for (child in children) {
            child.preroll(context, childMatrix)
            bounds = bounds.join(child.paintBounds)
        }
        return bounds
    }

    override fun paint(context: PaintContext) {
        for (child in children) {
            child.paint(context)
        }
    }
}

class PictureLayer() : Layer() {
    var picture: Picture? = null

    override fun preroll(context: PrerollContext, matrix: Matrix33) {
        paintBounds = picture!!.cullRect
    }

    override fun paint(context: PaintContext) {
        picture?.playback(context.canvas)
    }
}

class TransformLayer(
    val transform: Matrix33 = Matrix33.IDENTITY,
) : ContainerLayer() {
    companion object {
        fun withOffset(
            transform: Matrix33 = Matrix33.IDENTITY,
            offset: Offset = Offset.zero,
        ): TransformLayer {
            val move = Matrix33.makeTranslate(offset.dx.toFloat(), offset.dy.toFloat())
            return TransformLayer(transform.makeConcat(move))
        }
    }

    override fun preroll(context: PrerollContext, matrix: Matrix33) {
        val childMatrix = matrix.makeConcat(transform)
        val previousCullRect = context.cullRect

        val inverseTransform = transform.invert()
        if (inverseTransform != null) {
            context.cullRect = inverseTransform.mapRect(context.cullRect)
        } else {
            context.cullRect = kGiantRect
        }

        val childPaintBounds = prerollChildren(context, childMatrix)
        paintBounds = transform.mapRect(childPaintBounds)

        context.cullRect = previousCullRect
    }
}

class OpacityLayer(
    var alpha: Int? = null, val offset: Offset = Offset.zero,
) : ContainerLayer() {
    override fun preroll(context: PrerollContext, matrix: Matrix33) {
        val childMatrix = matrix.transform(offset)

        context.cullRect = context.cullRect.makeOffset(-offset)

        super.preroll(context, matrix)

        paintBounds = paintBounds.makeOffset(offset)

        context.cullRect = context.cullRect.makeOffset(offset)
    }

    override fun paint(context: PaintContext) {
        val paint = Paint()
        if (alpha != null) {
            paint.alpha = alpha!!
        }
        context.canvas.save()
        context.canvas.translate(offset.dx.toFloat(), offset.dy.toFloat())
        val saveLayerBounds =
            paintBounds.makeOffset(-offset.dx.toFloat(), -offset.dy.toFloat()).roundOut()
        context.canvas.saveLayer(saveLayerBounds, paint)
        super.paint(context)
        context.canvas.restore()
        context.canvas.restore()
    }
}

class ClipPathLayer(
    var clipPath: Path, var clipBehavior: Clip = Clip.AntiAlias,
) : ContainerLayer() {
    override fun preroll(context: PrerollContext, matrix: Matrix33) {
        val previousCullRect = context.cullRect
        val clipPathBounds = clipPath.bounds
        if (context.cullRect.intersect(clipPathBounds) == null) {
            context.cullRect = Rect.makeWH(0f, 0f)
        }
        val childPaintBounds = prerollChildren(context, matrix)
        if (childPaintBounds.intersect(clipPathBounds) != null) {
            paintBounds = childPaintBounds
        }

        context.cullRect = previousCullRect
    }

    override fun paint(context: PaintContext) {
        context.canvas.save()
        context.canvas.clipPath(clipPath, clipBehavior != Clip.HardEdge)

        super.paint(context)
        context.canvas.restore()
    }
}

class ClipRectLayer(
    var clipRect: Rect, var clipBehavior: Clip = Clip.AntiAlias,
) : ContainerLayer() {
    override fun preroll(context: PrerollContext, matrix: Matrix33) {
        val previousCullRect = context.cullRect
        if (context.cullRect.intersect(clipRect) == null) {
            context.cullRect = Rect.makeWH(0f, 0f)
        }
        val childPaintBounds = prerollChildren(context, matrix)
        if (childPaintBounds.intersect(clipRect) != null) {
            paintBounds = childPaintBounds
        }

        context.cullRect = previousCullRect
    }

    override fun paint(context: PaintContext) {
        context.canvas.save()
        context.canvas.clipRect(clipRect, clipBehavior != Clip.HardEdge)

        super.paint(context)
        context.canvas.restore()
    }
}

class ClipRRectLayer(
    var clipRRect: RRect, var clipBehavior: Clip = Clip.AntiAlias,
) : ContainerLayer() {
    override fun preroll(context: PrerollContext, matrix: Matrix33) {
        val previousCullRect = context.cullRect
        if (context.cullRect.intersect(clipRRect) == null) {
            context.cullRect = Rect.makeWH(0f, 0f)
        }
        val childPaintBounds = prerollChildren(context, matrix)
        if (childPaintBounds.intersect(clipRRect) != null) {
            paintBounds = childPaintBounds
        }

        context.cullRect = previousCullRect
    }

    override fun paint(context: PaintContext) {
        context.canvas.save()
        context.canvas.clipRRect(clipRRect, clipBehavior != Clip.HardEdge)

        super.paint(context)
        context.canvas.restore()
    }
}

enum class Clip {
    None, HardEdge, AntiAlias
}

data class PaintContext(
    val canvas: Canvas,
    val context: DirectContext,
)

data class PrerollContext(
    var cullRect: Rect,
)