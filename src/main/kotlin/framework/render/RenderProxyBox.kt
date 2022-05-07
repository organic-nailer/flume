package framework.render

import common.Offset
import framework.PaintingContext
import framework.geometrics.BoxConstraints
import framework.render.mixin.RenderObjectWithChild

abstract class RenderProxyBox : RenderBox(), RenderObjectWithChild<RenderBox> {
    override var child: RenderBox? = null
    override fun layout(constraints: BoxConstraints) {
        if (child != null) {
            child!!.layout(constraints)
            size = child!!.size
        } else {
            size = constraints.smallest
        }
    }

    override fun paint(context: PaintingContext, offset: Offset) {
        child?.paint(context, offset)
    }
}