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

    val nodesNeedingLayout: MutableList<RenderObject> = mutableListOf()

    fun flushLayout() {
        while(nodesNeedingLayout.isNotEmpty()) {
            // ツリーの上の方を先にやる
            val dirtyNodes = nodesNeedingLayout.sortedBy { it.depth }
            nodesNeedingLayout.clear()
            for(node in dirtyNodes) {
                if(node.needsLayout && node.owner == this) {
                    node.layoutWithoutResize()
                }
            }
        }
    }

    fun flushPaint() {
        val dirtyNodes = nodesNeedingPaint.sortedBy { it.depth }
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