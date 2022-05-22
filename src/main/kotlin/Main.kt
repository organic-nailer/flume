import common.Size
import engine.GLView
import engine.Shell
import engine.TaskRunner
import engine.TaskRunners
import framework.RenderPipeline
import framework.geometrics.BoxConstraints
import framework.render.RenderColoredBox
import framework.render.RenderConstrainedBox
import framework.render.RenderView
import org.lwjgl.glfw.GLFW.GLFW_KEY_M
import org.lwjgl.glfw.GLFW.GLFW_PRESS

fun main() {
    val width = 640
    val height = 480

    val taskRunners = TaskRunners(rasterTaskRunner = TaskRunner(), uiTaskRunner = TaskRunner())

    val glView = GLView(width, height)

    val renderPipeline = RenderPipeline().apply {
        renderView = RenderView(width.toDouble(), height.toDouble())
    }

    val shell = Shell(taskRunners, glView, null, renderPipeline, width, height)

    shell.initRasterThread()

    shell.drawFrame()

    var keyPressed = false

    glView.setKeyCallback { _, key, _, action, _ ->
        if (key == GLFW_KEY_M && action == GLFW_PRESS) {
            keyPressed = true
        }
    }

    while (!shell.glView.windowShouldClose()) {
        if (keyPressed) {
            keyPressed = false
            renderPipeline.renderView!!.child =
                RenderConstrainedBox(additionalConstraints = BoxConstraints.tight(Size(100.0,
                    100.0)), child = RenderColoredBox(0xFFFF0000.toInt()))
            shell.drawFrame()
        }
        shell.glView.pollEvents()
    }
    shell.taskRunners.terminateAll()
}
