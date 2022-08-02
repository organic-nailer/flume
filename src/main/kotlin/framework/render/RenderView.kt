package framework.render

import common.ContainerLayer
import common.Offset
import common.Size
import common.TransformLayer
import framework.PaintingContext
import framework.RenderPipeline
import framework.ViewConfiguration
import framework.geometrics.BoxConstraints
import framework.render.mixin.RenderObjectWithChild

class RenderView(configuration: ViewConfiguration) : RenderObject(), RenderObjectWithChild<RenderBox> {
    override var size: Size = configuration.size
    override var child: RenderBox? by RenderObjectWithChild.ChildDelegate()
    override val isRepaintBoundary: Boolean = true

    override fun performLayout() {
        child?.layout(BoxConstraints.tight(size))
    }

    override fun paint(context: PaintingContext, offset: Offset) {
        if (child != null) {
            context.paintChild(child!!, offset)
        }
    }

    override fun attach(owner: RenderPipeline) {
        super.attach(owner)
        attachChild(owner)
    }

    fun prepareInitialFrame() {
        scheduleInitialPaint(TransformLayer())
    }

    private fun scheduleInitialPaint(rootLayer: ContainerLayer) {
        layer = rootLayer
        owner!!.nodesNeedingPaint.add(this)
    }
}