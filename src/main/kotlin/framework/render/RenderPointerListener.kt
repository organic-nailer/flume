package framework.render

import common.PointerEvent
import common.PointerEventPhase
import framework.gesture.HitTestEntry

typealias PointerEventListener = (PointerEvent) -> Unit

class RenderPointerListener(
    var onPointerDown: PointerEventListener?,
    var onPointerMove: PointerEventListener?,
    var onPointerUp: PointerEventListener?
): RenderProxyBox() {
    override fun handleEvent(event: PointerEvent, entry: HitTestEntry) {
        when(event.phase) {
            PointerEventPhase.Down -> onPointerDown?.invoke(event)
            PointerEventPhase.Move -> onPointerMove?.invoke(event)
            PointerEventPhase.Up -> onPointerUp?.invoke(event)
            else -> {}
        }
    }
}