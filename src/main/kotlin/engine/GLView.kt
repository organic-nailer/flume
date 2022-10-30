package engine

import common.KeyEvent
import common.PointerEvent
import org.jetbrains.skia.DirectContext
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL

class GLView(width: Int, height: Int, private val delegate: GLViewDelegate) {
    private var windowHandle: Long = -1
    private val keyboardController: KeyboardController
    private val pointerController: PointerController

    init {
        GLFW.glfwInit()
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE)
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE)
        windowHandle = GLFW.glfwCreateWindow(width, height, "Flume", 0, 0)
        GLFW.glfwSwapInterval(1)
        GLFW.glfwShowWindow(windowHandle)
        keyboardController = KeyboardController(windowHandle) {
            if (!checkWindowClose(it)) delegate.onKeyEvent(it)
        }
        pointerController = PointerController(windowHandle) {
            delegate.onPointerEvent(it)
        }
    }

    private fun checkWindowClose(event: KeyEvent): Boolean {
        if (event.logicalKeyboardKey == GLFW.GLFW_KEY_ESCAPE && event.phase == KeyEvent.KeyEventPhase.KeyDown) {
            GLFW.glfwSetWindowShouldClose(windowHandle, true)
            return true
        }
        return false
    }

    fun windowShouldClose(): Boolean {
        return GLFW.glfwWindowShouldClose(windowHandle)
    }

    fun swapBuffers() {
        GLFW.glfwSwapBuffers(windowHandle)
    }

    fun pollEvents() {
        GLFW.glfwPollEvents()
    }

    fun createContext(): DirectContext {
        GLFW.glfwMakeContextCurrent(windowHandle)
        GL.createCapabilities()
        return DirectContext.makeGL()
    }

    fun setKeyCallback(callback: (window: Long, key: Int, code: Int, action: Int, mods: Int) -> Unit) {
        GLFW.glfwSetKeyCallback(windowHandle, callback)
    }

    interface GLViewDelegate {
        fun onKeyEvent(event: KeyEvent)
        fun onPointerEvent(event: PointerEvent)
    }
}