import common.Offset
import common.Size
import engine.GLView
import engine.Shell
import engine.TaskRunner
import engine.TaskRunners
import framework.RenderPipeline
import framework.geometrics.BoxConstraints
import framework.geometrics.MainAxisSize
import framework.painting.BorderRadius
import framework.render.RenderColoredBox
import framework.render.RenderConstrainedBox
import framework.render.RenderFlex
import framework.render.RenderPositionedBox
import framework.render.RenderView
import framework.render.clip.CustomClipper
import framework.render.clip.RenderClipOval
import framework.render.clip.RenderClipPath
import framework.render.clip.RenderClipRRect
import framework.widget.Align
import framework.widget.ColoredBox
import framework.widget.RenderObjectToWidgetAdapter
import framework.widget.SizedBox
import framework.widget.Widget
import org.jetbrains.skia.Path
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
            RenderObjectToWidgetAdapter(
                createWidgetTree(),
                renderPipeline.renderView!!
            ).attachToRenderTree()
            shell.drawFrame()
        }
        shell.glView.pollEvents()
    }
    shell.taskRunners.terminateAll()
}

fun createWidgetTree(): Widget {
    return Align(
        child = SizedBox(
            width = 100.0, height = 100.0,
            child = ColoredBox(
                color = 0xFFFF0000.toInt()
            )
        )
    )
}