package engine

import common.LayerTree
import framework.RenderPipeline

class Shell(
    val taskRunners: TaskRunners,
    var glView: GLView,
    var rasterizer: Rasterizer?,
    val renderPipeline: RenderPipeline,
    val width: Int, val height: Int,
) {
    fun initRasterThread() {
        taskRunners.rasterTaskRunner.postTask {
            println("in rasterThread")
            val context = glView.createContext()
            rasterizer = Rasterizer(width, height, context)
        }
    }

    fun drawFrame() {
        renderPipeline.flushLayout()
        renderPipeline.flushPaint()
        render()
    }

    fun render() {
        val layerTree = LayerTree().apply {
            rootLayer = renderPipeline.renderView!!.layer
        }
        taskRunners.rasterTaskRunner.postTask {
            rasterizer!!.drawToSurface(layerTree)
            glView.swapBuffers()
        }
    }
}