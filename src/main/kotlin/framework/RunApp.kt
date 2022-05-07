package framework

import common.Layer
import framework.widget.Widget

fun runApp(engine: Engine, app: Widget) {
    WidgetsFlumeBinding.apply {
        ensureInitialized(engine)
        attachRootWidget(app)
        drawFrame()
    }
}

interface Engine {
    val viewConfiguration: ViewConfiguration

    fun render(rootLayer: Layer)
}