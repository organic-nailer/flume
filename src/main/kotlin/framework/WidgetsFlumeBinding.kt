package framework

import framework.element.Element
import framework.render.RenderView
import framework.widget.RenderObjectToWidgetAdapter
import framework.widget.Widget

object WidgetsFlumeBinding : WidgetsBinding {
    lateinit var pipeline: RenderPipeline
    lateinit var engine: Engine
    var renderViewElement: Element? = null
    var initialized = false
    var engineConnected = false
    override fun connectToEngine(engine: Engine) {
        this.engine = engine
        engineConnected = true
    }

    fun ensureInitialized() {
        if (!engineConnected) {
            throw Exception("tried to initialize before connecting to engine.")
        }
        if (initialized) return
        initialized = true
        val configuration = engine.viewConfiguration
        pipeline = RenderPipeline().apply {
            renderView = RenderView(configuration.size.width, configuration.size.height)
        }
    }

    fun attachRootWidget(rootWidget: Widget) {
        renderViewElement =
            RenderObjectToWidgetAdapter(rootWidget, pipeline.renderView!!).attachToRenderTree()
    }

    override fun beginFrame() {
        if (!initialized) return
        drawFrame()
    }


    fun drawFrame() {
        pipeline.flushLayout()
        pipeline.flushPaint()
        engine.render(pipeline.renderView!!.layer)
    }
}