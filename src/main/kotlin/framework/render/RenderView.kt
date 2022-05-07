package framework.render

import common.ContainerLayer
import common.Offset
import common.Size
import common.TransformLayer
import framework.PaintingContext
import framework.geometrics.BoxConstraints
import framework.render.mixin.RenderObjectWithChild

class RenderView(width: Double, height: Double) : RenderObject(), RenderObjectWithChild<RenderBox> {
    override var size: Size = Size(width, height)
    override var child: RenderBox? = null
    val layer: ContainerLayer = TransformLayer()
    override fun layout(constraints: BoxConstraints) {
        throw NotImplementedError()
    }

    fun performLayout() {
        child?.layout(BoxConstraints.tight(size))
    }

    override fun paint(context: PaintingContext, offset: Offset) {
        if (child != null) {
            child!!.paint(context, offset)
        }
    }
}