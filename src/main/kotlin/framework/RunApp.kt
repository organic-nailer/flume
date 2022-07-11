package framework

import common.KeyEvent
import common.Layer
import framework.widget.Widget

fun runApp(app: Widget) {
    WidgetsFlumeBinding.apply {
        ensureInitialized()
        attachRootWidget(app)
        engine.scheduleFrame()
    }
}

interface Engine {
    val viewConfiguration: ViewConfiguration

    fun render(rootLayer: Layer)

    fun scheduleFrame()
}

interface WidgetsBinding {
    fun connectToEngine(engine: Engine)

    fun beginFrame()

    fun handleKeyEvent(event: KeyEvent)
}