package framework.gesture

import common.math.Matrix4

data class HitTestEntry(
    val target: HitTestTarget,
    var transform: Matrix4? = null
)