import common.ContainerLayer
import common.LayerTree
import common.PictureLayer
import engine.GLView
import engine.Rasterizer
import engine.Shell
import engine.TaskRunner
import engine.TaskRunners
import org.jetbrains.skia.Paint
import org.jetbrains.skia.PictureRecorder
import org.jetbrains.skia.Rect
import org.lwjgl.glfw.GLFW.GLFW_KEY_M
import org.lwjgl.glfw.GLFW.GLFW_PRESS
import kotlin.random.Random

fun main() {
    val width = 640
    val height = 480

    val taskRunners = TaskRunners(rasterTaskRunner = TaskRunner(), uiTaskRunner = TaskRunner())

    val glView = GLView(width, height)

    val shell = Shell(taskRunners, glView, null)

    taskRunners.rasterTaskRunner.postTask {
        println("in rasterThread")
        val context = shell.glView.createContext()
        val rasterizer = Rasterizer(width, height, context)
        shell.rasterizer = rasterizer
    }

    var keyPressed = false

    glView.setKeyCallback { _, key, _, action, _ ->
        if (key == GLFW_KEY_M && action == GLFW_PRESS) {
            keyPressed = true
        }
    }

    while (!shell.glView.windowShouldClose()) {
        if (keyPressed) {
            keyPressed = false
            shell.taskRunners.rasterTaskRunner.postTask {
                shell.rasterizer!!.drawToSurface(
                    createRandomTree(width.toFloat(),height.toFloat())
                )
                shell.glView.swapBuffers()
            }
        }
        shell.glView.pollEvents()
    }
    shell.taskRunners.terminateAll()
}

fun createRandomTree(width: Float, height: Float): LayerTree {
    val root = ContainerLayer()
    val rect = Rect.makeXYWH(0f,0f,width, height)
    val leaf = PictureLayer()
    val recorder = PictureRecorder()
    val canvas = recorder.beginRecording(rect)

    val paint = Paint().apply { color = 0xFFFF0000.toInt() }

    val randomX = Random.nextFloat() * width
    val randomY = Random.nextFloat() * height

    canvas.drawCircle(randomX, randomY, 40f, paint)

    leaf.picture = recorder.finishRecordingAsPicture()
    root.children.add(leaf)

    return LayerTree().apply {
        rootLayer = root
    }
}