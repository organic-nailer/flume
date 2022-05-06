package engine

import common.LayerTree
import common.PaintContext
import org.jetbrains.skia.BackendRenderTarget
import org.jetbrains.skia.ColorSpace
import org.jetbrains.skia.DirectContext
import org.jetbrains.skia.FramebufferFormat
import org.jetbrains.skia.Surface
import org.jetbrains.skia.SurfaceColorFormat
import org.jetbrains.skia.SurfaceOrigin
import org.lwjgl.opengl.GL11

class Rasterizer(
    width: Int,
    height: Int,
    private val context: DirectContext,
) {
    private val surface: Surface
    private val fbId: Int = GL11.glGetInteger(0x8CA6)

    init {
        val renderTarget =
            BackendRenderTarget.makeGL(width, height, 0, 8, fbId, FramebufferFormat.GR_GL_RGBA8)

        surface = Surface.makeFromBackendRenderTarget(context,
            renderTarget,
            SurfaceOrigin.BOTTOM_LEFT,
            SurfaceColorFormat.RGBA_8888,
            ColorSpace.sRGB)!!
    }

    fun drawToSurface(layerTree: LayerTree) {
        println("draw")

        layerTree.preroll()

        surface.canvas.clear(0xFFFFFFFF.toInt())
        layerTree.paint(PaintContext(surface.canvas, context))

        context.flush()
    }
}