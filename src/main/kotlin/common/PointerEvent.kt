package common

enum class PointerEventPhase {
    Up, Down, Move, Add, Remove
}

data class PointerEvent(
    val pointerId: Int,
    val phase: PointerEventPhase, val x: Double, val y: Double
){
    val position = Offset(x, y)

    var transform: Offset? = null
}