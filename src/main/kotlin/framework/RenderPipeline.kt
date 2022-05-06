package framework

import common.Offset
import framework.render.RenderView

class RenderPipeline {
    var renderView: RenderView? = null
    fun flushLayout() {
        renderView!!.performLayout()
    }

    fun flushPaint() {
        val rootLayer = renderView!!.layer
        val context = PaintingContext(rootLayer, renderView!!.size.and(Offset.zero))
        renderView!!.paint(context, Offset.zero)
        context.stopRecordingIfNeeded()
    }
}