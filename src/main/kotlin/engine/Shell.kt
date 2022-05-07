package engine

import common.KeyEvent
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
) : Engine, GLView.GLViewDelegate {
    var glView: GLView = GLView(width, height, this)
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

    override fun onKeyEvent(event: KeyEvent) {
        binding.handleKeyEvent(event)
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