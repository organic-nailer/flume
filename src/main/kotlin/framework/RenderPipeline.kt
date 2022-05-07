package framework

import framework.render.RenderObject
import framework.render.RenderView

class RenderPipeline(
    private val onNeedVisualUpdate: () -> Unit,
) {
    var renderView: RenderView? = null
        set(value) {
            value?.attach(this)
            field = value
        }

    /**
     * flushPaint()でpaintする必要のあるRenderObjectたち
     *
     * [RenderObject.markNeedsPaint]の通り、[RenderObject.isRepaintBoundary] == trueの
     * もの([RenderView], [RenderRepaintBoundary] ...)のみが追加される
     */
    val nodesNeedingPaint: MutableList<RenderObject> = mutableListOf()

    fun flushLayout() {
        renderView!!.performLayout()
    }

    fun flushPaint() {
        val dirtyNodes = nodesNeedingPaint.toList()
        nodesNeedingPaint.clear()
        for (node in dirtyNodes) {
            if (node.needsPaint && node.owner == this) {
                PaintingContext.repaintCompositedChild(node)
            }
        }
    }

    fun requestVisualUpdate() {
        onNeedVisualUpdate()
    }
}