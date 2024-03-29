package framework.render

import common.ContainerLayer
import common.Offset
import common.PointerEvent
import common.Size
import common.TransformLayer
import framework.PaintingContext
import framework.RenderPipeline
import framework.ViewConfiguration
import framework.geometrics.BoxConstraints
import framework.gesture.HitTestEntry
import framework.gesture.HitTestResult
import framework.render.mixin.RenderObjectWithChild

class RenderView(configuration: ViewConfiguration) : RenderObject(),
        RenderObjectWithChild<RenderBox> {
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

    override fun detach() {
        super.detach()
        detachChild()
    }

    override fun visitChildren(visitor: RenderObjectVisitor) {
        super<RenderObjectWithChild>.visitChildren(visitor)
    }

    override fun redepthChildren() {
        super<RenderObjectWithChild>.redepthChildren { redepthChild(it) }
    }

    fun prepareInitialFrame() {
        scheduleInitialLayout()
        scheduleInitialPaint(TransformLayer())
    }

    private fun scheduleInitialLayout() {
        relayoutBoundary = this
        owner!!.nodesNeedingLayout.add(this)
    }

    private fun scheduleInitialPaint(rootLayer: ContainerLayer) {
        layer = rootLayer
        owner!!.nodesNeedingPaint.add(this)
    }

    fun hitTest(result: HitTestResult, position: Offset): Boolean {
        child?.hitTest(result, position)
        result.add(HitTestEntry(this))
        return true
    }

    override fun handleEvent(event: PointerEvent, entry: HitTestEntry) {
        // Do Nothing
    }
}