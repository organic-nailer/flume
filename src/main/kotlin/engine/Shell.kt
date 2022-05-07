package engine

import common.Layer
import common.LayerTree
import common.Size
import framework.Engine
import framework.ViewConfiguration

class Shell(
    val taskRunners: TaskRunners,
    var glView: GLView,
    var rasterizer: Rasterizer?,
    val width: Int, val height: Int,
) : Engine {
    fun initRasterThread() {
        taskRunners.rasterTaskRunner.postTask {
            println("in rasterThread")
            val context = glView.createContext()
            rasterizer = Rasterizer(width, height, context)
        }
    }

    override val viewConfiguration: ViewConfiguration =
        ViewConfiguration(Size(width.toDouble(), height.toDouble()))

    override fun render(rootLayer: Layer) {
        val layerTree = LayerTree().apply {
            this.rootLayer = rootLayer
        }
        taskRunners.rasterTaskRunner.postTask {
            rasterizer!!.drawToSurface(layerTree)
            glView.swapBuffers()
        }
    }
}