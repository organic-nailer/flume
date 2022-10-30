package engine

import common.PointerEvent
import common.PointerEventPhase
import org.lwjgl.glfw.GLFW

class PointerController(
    private val windowHandle: Long,
    private val onEventCallback: (PointerEvent) -> Unit
) {
    private var pointerCurrentlyAdded = false
    private var pointerCurrentlyDown = false
    private var buttonPressed = false
    private var pointerId = 0

    init {
        GLFW.glfwSetCursorEnterCallback(windowHandle) { _, enter ->
            onCursorEnter(enter)
        }
        GLFW.glfwSetCursorPosCallback(windowHandle) { _, x, y ->
            onCursorPosition(x,y)
        }
        GLFW.glfwSetMouseButtonCallback(windowHandle) { _, key, action, _ ->
            onMouseButton(key, action)
        }
    }

    private fun onCursorEnter(entered: Boolean) {
        val (x,y) = getCurrentCursorPosition() ?: return
        val phase = if(entered) PointerEventPhase.Add else PointerEventPhase.Remove
        sendPointerEvent(phase, x, y)
    }

    private fun onCursorPosition(x: Double, y: Double) {
        val phase = if(buttonPressed) {
            if(pointerCurrentlyDown) PointerEventPhase.Move else PointerEventPhase.Down
        } else {
            if(pointerCurrentlyDown) PointerEventPhase.Up else return
        }
        sendPointerEvent(phase, x, y)
    }


    private fun onMouseButton(key: Int, action: Int) {
        if(key != GLFW.GLFW_MOUSE_BUTTON_LEFT) return
        buttonPressed = action == GLFW.GLFW_PRESS

        val phase = if(buttonPressed) {
            if(pointerCurrentlyDown) PointerEventPhase.Move else PointerEventPhase.Down
        } else {
            if(pointerCurrentlyDown) PointerEventPhase.Up else return
        }
        val (x,y) = getCurrentCursorPosition() ?: return
        sendPointerEvent(phase, x, y)
    }

    private fun getCurrentCursorPosition(): Pair<Double, Double>? {
        val cursorX = DoubleArray(1)
        val cursorY = DoubleArray(1)
        GLFW.glfwGetCursorPos(windowHandle, cursorX, cursorY)
        val x = cursorX.getOrNull(0) ?: return null
        val y = cursorY.getOrNull(0) ?: return null
        return x to y
    }

    private fun sendPointerEvent(phase: PointerEventPhase, x: Double, y: Double) {
        if(!pointerCurrentlyAdded && phase != PointerEventPhase.Add) { // pointerの開始は必ずAddから
            sendPointerEvent(PointerEventPhase.Add, x, y)
        } // Addの重複は許さない
        if(pointerCurrentlyAdded && phase == PointerEventPhase.Add) {
            return
        }

        if(phase == PointerEventPhase.Add) {
            pointerId++
        }

        onEventCallback(
            PointerEvent(
                pointerId, phase, x, y
            )
        )

        when(phase) {
            PointerEventPhase.Add -> {
                pointerCurrentlyAdded = true
            }
            PointerEventPhase.Remove -> {
                pointerCurrentlyAdded = false
            }
            PointerEventPhase.Down -> {
                pointerCurrentlyDown = true
            }
            PointerEventPhase.Up -> {
                pointerCurrentlyDown = false
            }
            else -> {}
        }
    }
}