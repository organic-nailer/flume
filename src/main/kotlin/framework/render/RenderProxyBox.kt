package framework.render

import common.Offset
import framework.PaintingContext
import framework.RenderPipeline
import framework.render.mixin.RenderObjectWithChild

abstract class RenderProxyBox : RenderBox(), RenderObjectWithChild<RenderBox> {
    override var child: RenderBox? by RenderObjectWithChild.ChildDelegate()
    override fun performLayout() {
        if (child != null) {
            child!!.layout(constraints)
            size = child!!.size
        } else {
            size = constraints.smallest
        }
    }

    override fun paint(context: PaintingContext, offset: Offset) {
        child?.let {
            context.paintChild(it, offset)
        }
    }

    override fun attach(owner: RenderPipeline) {
        super.attach(owner)
        attachChild(owner)
    }
}