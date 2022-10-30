package framework.gesture

import common.PointerEvent

interface HitTestTarget {
    fun handleEvent(event: PointerEvent, entry: HitTestEntry)
}