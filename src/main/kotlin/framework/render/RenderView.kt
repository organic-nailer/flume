package framework.render

import common.ContainerLayer
import common.Offset
import common.Size
import common.TransformLayer
import framework.PaintingContext
import framework.geometrics.BoxConstraints

class RenderView(width: Double, height: Double) : RenderObject() {
    override var size: Size = Size(width, height)
    var child: RenderBox? = null
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