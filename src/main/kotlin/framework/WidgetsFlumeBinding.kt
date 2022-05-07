package framework

import framework.element.Element
import framework.render.RenderView
import framework.widget.RenderObjectToWidgetAdapter
import framework.widget.Widget

object WidgetsFlumeBinding {
    lateinit var pipeline: RenderPipeline
    lateinit var engine: Engine
    var renderViewElement: Element? = null
    var initialized = false
    fun ensureInitialized(engine: Engine) {
        if (initialized) return
        initialized = true
        this.engine = engine
        val configuration = engine.viewConfiguration
        pipeline = RenderPipeline().apply {
            renderView = RenderView(configuration.size.width, configuration.size.height)
        }
    }

    fun attachRootWidget(rootWidget: Widget) {
        renderViewElement =
            RenderObjectToWidgetAdapter(rootWidget, pipeline.renderView!!).attachToRenderTree()
    }

    fun drawFrame() {
        pipeline.flushLayout()
        pipeline.flushPaint()
        engine.render(pipeline.renderView!!.layer)
    }
}