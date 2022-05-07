package common

/**
 * キーイベント
 *
 * @param physicalKeyboardKey 物理的なキー位置の返すscancode
 * @param logicalKeyboardKey LWJGLの論理キーポイント
 */
data class KeyEvent(
    val physicalKeyboardKey: Int, val logicalKeyboardKey: Int, val character: String? = null, val phase: KeyEventPhase
) {
    enum class KeyEventPhase {
        KeyDown, KeyUp, KeyRepeat
    }
}