package framework

import common.KeyEvent
import common.Layer
import common.PointerEvent
import framework.widget.Widget
import kotlin.time.Duration

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

    fun beginFrame(elapsedTime: Duration)

    fun handleKeyEvent(event: KeyEvent)

    fun handlePointerEvent(event: PointerEvent)
}