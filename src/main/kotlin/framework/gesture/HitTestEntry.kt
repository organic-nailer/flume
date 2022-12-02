package framework.gesture

import common.Offset

data class HitTestEntry(
    val target: HitTestTarget,
    var transform: Offset? = null
)