package framework

import common.ContainerLayer
import common.KeyEvent
import common.Offset
import common.PointerEvent
import common.PointerEventPhase
import framework.element.BuildOwner
import framework.element.Element
import framework.element.RenderObjectToWidgetElement
import framework.gesture.HitTestEntry
import framework.gesture.HitTestResult
import framework.gesture.HitTestTarget
import framework.render.RenderView
import framework.widget.RenderObjectToWidgetAdapter
import framework.widget.Widget

object WidgetsFlumeBinding : WidgetsBinding, HitTestTarget {
    lateinit var pipeline: RenderPipeline
    lateinit var engine: Engine
    var renderViewElement: Element? = null
    var initialized = false
    var engineConnected = false
    var buildOwner: BuildOwner = BuildOwner { handleBuildScheduled() }
    private val hitTests = mutableMapOf<Int, HitTestResult>()

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
        pipeline = RenderPipeline(onNeedVisualUpdate = {
            ensureVisualUpdate()
        }).apply {
            renderView = RenderView(configuration)
            renderView!!.prepareInitialFrame()
        }
    }

    fun ensureVisualUpdate() {
        engine.scheduleFrame()
    }

    private fun handleBuildScheduled() {
        ensureVisualUpdate()
    }

    fun attachRootWidget(rootWidget: Widget) {
        val isBootstrapFrame = renderViewElement == null
        renderViewElement =
            RenderObjectToWidgetAdapter(rootWidget, pipeline.renderView!!).attachToRenderTree(
                buildOwner,
                renderViewElement as RenderObjectToWidgetElement<*>?)
        if (isBootstrapFrame) {
            ensureVisualUpdate()
        }
    }

    override fun beginFrame() {
        if (!initialized) return
        drawFrame()
    }

    /**
     * 次フレームを描画する
     *
     * WidgetsBinding.drawFrame() -> RendererBinding.drawFrame()
     */
    fun drawFrame() {
        // WidgetsBinding.drawFrame
        if (renderViewElement != null) {
            buildOwner.buildScope()
        }

        // RendererBinding.drawFrame
        pipeline.flushLayout()
        pipeline.flushPaint()
        engine.render(pipeline.renderView!!.layer as ContainerLayer)
    }


    var keyEventListener: ((KeyEvent) -> Unit)? = null
    fun setOnKeyEventCallback(listener: (KeyEvent) -> Unit) {
        keyEventListener = listener
    }

    override fun handleKeyEvent(event: KeyEvent) {
        keyEventListener?.invoke(event)
    }

    override fun handlePointerEvent(event: PointerEvent) {
        // GestureBinding._handlePointerEventImmediately()
        var hitTestResult: HitTestResult? = null
        if(event.phase == PointerEventPhase.Down) {
            hitTestResult = HitTestResult()
            hitTest(hitTestResult, event.position)
            hitTests[event.pointerId] = hitTestResult
        } else if(event.phase == PointerEventPhase.Up) {
            hitTestResult = hitTests.remove(event.pointerId)
        } else if(event.phase == PointerEventPhase.Move) {
            hitTestResult = hitTests[event.pointerId]
        }
        if(hitTestResult != null || event.phase == PointerEventPhase.Add || event.phase == PointerEventPhase.Remove) {
            dispatchEvent(event, hitTestResult)
        }
    }

    private fun hitTest(hitTestResult: HitTestResult, position: Offset) {
        // RendererBinding.hitTest()
        pipeline.renderView!!.hitTest(hitTestResult, position)
        // GestureBinding.hitTest()
        hitTestResult.add(HitTestEntry(this))
    }

    override fun handleEvent(event: PointerEvent, entry: HitTestEntry) {
        // None
    }

    private fun dispatchEvent(event: PointerEvent, hitTestResult: HitTestResult?) {
        if(hitTestResult == null) {
            assert(event.phase == PointerEventPhase.Add || event.phase == PointerEventPhase.Remove)
            return
        }
        for(entry in hitTestResult.path) {
            entry.target.handleEvent(event.apply { transform = entry.transform }, entry)
        }
    }
}