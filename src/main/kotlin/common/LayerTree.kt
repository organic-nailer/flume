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
    var parent: ContainerLayer? = null

    abstract fun paint(context: PaintContext)
    abstract fun preroll(context: PrerollContext, matrix: Matrix33)

    abstract fun clone(): Layer

    fun remove() {
        parent?.removeChild(this)
    }

    // AbstractNode
    fun dropChild(child: Layer) {
        child.parent = null
    }

    fun adoptChild(child: Layer) {
        child.parent = this as ContainerLayer
    }
}

open class ContainerLayer : Layer() {
    protected val childrenInternal: MutableList<Layer> = mutableListOf()
    val children: List<Layer>
        get() = childrenInternal

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

    override fun clone(): Layer {
        val cloned = ContainerLayer()
        for (child in children) {
            cloned.append(child.clone())
        }
        return cloned
    }

    fun append(child: Layer) {
        adoptChild(child)
        childrenInternal.add(child)
    }

    fun removeChild(child: Layer) {
        childrenInternal.remove(child)
        child.parent = null
    }

    fun removeAllChildren() {
        for (layer in childrenInternal) {
            dropChild(layer)
        }
        childrenInternal.clear()
    }
}

class PictureLayer : Layer() {
    var picture: Picture? = null

    override fun preroll(context: PrerollContext, matrix: Matrix33) {
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

open class OffsetLayer(
    open var offset: Offset = Offset.zero,
) : ContainerLayer() {
    override fun clone(): Layer {
        val cloned = OffsetLayer(offset)
        for (child in children) {
            cloned.append(child.clone())
        }
        return cloned
    }
}

class TransformLayer(
    val transform: Matrix33 = Matrix33.IDENTITY, offset: Offset = Offset.zero,
) : OffsetLayer(offset) {

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

    override fun clone(): Layer {
        val cloned = TransformLayer(transform, offset)
        for (child in children) {
            cloned.append(child.clone())
        }
        return cloned
    }
}

class OpacityLayer(
    var alpha: Int? = null, offset: Offset = Offset.zero,
) : OffsetLayer(offset) {
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

    override fun clone(): Layer {
        val cloned = OpacityLayer(alpha, offset)
        for (child in children) {
            cloned.append(child.clone())
        }
        return cloned
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

    override fun clone(): Layer {
        val cloned = ClipPathLayer(clipPath, clipBehavior)
        for (child in children) {
            cloned.append(child.clone())
        }
        return cloned
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

    override fun clone(): Layer {
        val cloned = ClipRectLayer(clipRect, clipBehavior)
        for (child in children) {
            cloned.append(child.clone())
        }
        return cloned
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

    override fun clone(): Layer {
        val cloned = ClipRRectLayer(clipRRect, clipBehavior)
        for (child in children) {
            cloned.append(child.clone())
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

data class PrerollContext(
    var cullRect: Rect,
)