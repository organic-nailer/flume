package engine

import common.Layer
import common.LayerTree
import common.Size
import framework.Engine
import framework.ViewConfiguration
import framework.WidgetsBinding
import framework.WidgetsFlumeBinding

class Shell(
    val taskRunners: TaskRunners,
    var rasterizer: Rasterizer?,
    val width: Int, val height: Int,
) : Engine {
    var glView: GLView = GLView(width, height)
    private var binding: WidgetsBinding = WidgetsFlumeBinding

    init {
        binding.connectToEngine(this)
    }

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
            this.rootLayer = rootLayer.clone()
        }
        taskRunners.rasterTaskRunner.postTask {
            rasterizer!!.drawToSurface(layerTree)
            glView.swapBuffers()
        }
    }

    fun run(appMain: () -> Unit) {
        taskRunners.uiTaskRunner.postTask {
            appMain()
        }
    }

    fun onVsync() {
        taskRunners.uiTaskRunner.postTask {
            binding.beginFrame()
        }
    }
}