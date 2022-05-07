package engine

import common.KeyEvent
import org.lwjgl.glfw.GLFW

class KeyboardController(
    windowHandle: Long, private val onEventCallback: (KeyEvent) -> Unit,
) {
    init {
        GLFW.glfwSetKeyCallback(windowHandle) { _, key: Int, code: Int, action: Int, mods: Int ->
            onKey(key, code, action, mods)
        }
    }

    private fun onKey(key: Int, code: Int, action: Int, mods: Int) {
        val event = KeyEvent(physicalKeyboardKey = code,
            logicalKeyboardKey = key,
            character = GLFW.glfwGetKeyName(key, code),
            phase = when (action) {
                GLFW.GLFW_PRESS -> KeyEvent.KeyEventPhase.KeyDown
                GLFW.GLFW_RELEASE -> KeyEvent.KeyEventPhase.KeyUp
                GLFW.GLFW_REPEAT -> KeyEvent.KeyEventPhase.KeyRepeat
                else -> throw Exception("Unknown key action: $action")
            })
        onEventCallback(event)
    }
}