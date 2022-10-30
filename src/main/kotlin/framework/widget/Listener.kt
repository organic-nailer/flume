package framework.widget

import framework.render.PointerEventListener
import framework.render.RenderPointerListener

class Listener(
    child: Widget? = null,
    private val onPointerDown: PointerEventListener? = null,
    private val onPointerMove: PointerEventListener? = null,
    private val onPointerUp: PointerEventListener? = null
): SingleChildRenderObjectWidget<RenderPointerListener>(child) {
    override fun createRenderObject(): RenderPointerListener {
        return RenderPointerListener(
            onPointerDown,
            onPointerMove,
            onPointerUp
        )
    }

    override fun updateRenderObject(renderObject: RenderPointerListener) {
        renderObject.let {
            it.onPointerDown = onPointerDown
            it.onPointerMove = onPointerMove
            it.onPointerUp = onPointerUp
        }
    }
}
