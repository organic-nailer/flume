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

        rootLayer!!.preroll()
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
    abstract fun preroll()

    abstract fun clone(): Layer
}

open class ContainerLayer : Layer() {
    val children: MutableList<Layer> = mutableListOf()

    override fun preroll() {
        paintBounds = prerollChildren()
    }

    /**
     * 子の矩形を計算しその和を返す
     */
    protected fun prerollChildren(): Rect {
        var bounds = kEmptyRect
        for (child in children) {
            child.preroll()
            bounds = bounds.join(child.paintBounds)
        }
        return bounds
    }

    override fun paint(context: PaintContext) {
        for (child in children) {
            child.paint(context)
        }
    }

    override fun clone(): Layer {
        val cloned = ContainerLayer()
        for (child in children) {
            cloned.children.add(child.clone())
        }
        return cloned
    }
}

class PictureLayer : Layer() {
    var picture: Picture? = null

    override fun preroll() {
        paintBounds = picture!!.cullRect
    }

    override fun paint(context: PaintContext) {
        picture?.playback(context.canvas)
    }

    override fun clone(): Layer {
        val cloned = PictureLayer()
        cloned.picture = picture
        return cloned
    }
}

class TransformLayer(
    var transform: Matrix33 = Matrix33.IDENTITY,
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

    override fun preroll() {
        val childPaintBounds = prerollChildren()
        paintBounds = transform.mapRect(childPaintBounds)
    }

    override fun paint(context: PaintContext) {
        context.canvas.save()
        context.canvas.concat(transform)

        super.paint(context)

        context.canvas.restore()
    }

    override fun clone(): Layer {
        val cloned = TransformLayer(transform)
        for (child in children) {
            cloned.children.add(child.clone())
        }
        return cloned
    }
}

class OpacityLayer(
    var alpha: Int? = null
) : ContainerLayer() {
    override fun paint(context: PaintContext) {
        val paint = Paint()
        if (alpha != null) {
            paint.alpha = alpha!!
        }
        context.canvas.saveLayer(paintBounds.roundOut(), paint)
        super.paint(context)
        context.canvas.restore()
    }

    override fun clone(): Layer {
        val cloned = OpacityLayer(alpha)
        for (child in children) {
            cloned.children.add(child.clone())
        }
        return cloned
    }
}

class ClipPathLayer(
    var clipPath: Path, var clipBehavior: Clip = Clip.AntiAlias,
) : ContainerLayer() {
    override fun preroll() {
        val clipPathBounds = clipPath.bounds
        val childPaintBounds = prerollChildren()
        if (childPaintBounds.intersect(clipPathBounds) != null) {
            paintBounds = childPaintBounds
        }
    }

    override fun paint(context: PaintContext) {
        context.canvas.save()
        context.canvas.clipPath(clipPath, clipBehavior != Clip.HardEdge)

        super.paint(context)
        context.canvas.restore()
    }

    override fun clone(): Layer {
        val cloned = ClipPathLayer(clipPath, clipBehavior)
        for (child in children) {
            cloned.children.add(child.clone())
        }
        return cloned
    }
}

class ClipRectLayer(
    var clipRect: Rect, var clipBehavior: Clip = Clip.AntiAlias,
) : ContainerLayer() {
    override fun preroll() {
        val childPaintBounds = prerollChildren()
        if (childPaintBounds.intersect(clipRect) != null) {
            paintBounds = childPaintBounds
        }
    }

    override fun paint(context: PaintContext) {
        context.canvas.save()
        context.canvas.clipRect(clipRect, clipBehavior != Clip.HardEdge)

        super.paint(context)
        context.canvas.restore()
    }

    override fun clone(): Layer {
        val cloned = ClipRectLayer(clipRect, clipBehavior)
        for (child in children) {
            cloned.children.add(child.clone())
        }
        return cloned
    }
}

class ClipRRectLayer(
    var clipRRect: RRect, var clipBehavior: Clip = Clip.AntiAlias,
) : ContainerLayer() {
    override fun preroll() {
        val childPaintBounds = prerollChildren()
        if (childPaintBounds.intersect(clipRRect) != null) {
            paintBounds = childPaintBounds
        }
    }

    override fun paint(context: PaintContext) {
        context.canvas.save()
        context.canvas.clipRRect(clipRRect, clipBehavior != Clip.HardEdge)

        super.paint(context)
        context.canvas.restore()
    }

    override fun clone(): Layer {
        val cloned = ClipRRectLayer(clipRRect, clipBehavior)
        for (child in children) {
            cloned.children.add(child.clone())
        }
        return cloned
    }
}

enum class Clip {
    None, HardEdge, AntiAlias
}

data class PaintContext(
    val canvas: Canvas,
    val context: DirectContext,
)
