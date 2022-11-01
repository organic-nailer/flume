package framework.render

import common.Offset
import common.OpacityLayer
import framework.PaintingContext
import framework.RenderPipeline
import framework.animation.AnimationController
import kotlin.math.roundToInt

class RenderAnimatedOpacity(
    opacity: AnimationController
): RenderProxyBox() {
    private var alpha: Int? = null

    var opacity: AnimationController = opacity
        set(value) {
            if(opacity == value) return
            if(attached) {
                opacity.removeListener(onUpdateOpacity)
            }
            field = value
            if(attached) {
                opacity.addListener(onUpdateOpacity)
            }
            updateOpacity()
        }

    override fun attach(owner: RenderPipeline) {
        super.attach(owner)
        opacity.addListener(onUpdateOpacity)
        updateOpacity()
    }

    override fun detach() {
        opacity.removeListener(onUpdateOpacity)
        super.detach()
    }

    private val onUpdateOpacity = {
        updateOpacity()
    }

    private fun updateOpacity() {
        val oldAlpha = alpha
        alpha = (opacity.value * 255).roundToInt()
        if(oldAlpha != alpha) {
            markNeedsPaint()
        }
    }

    override fun paint(context: PaintingContext, offset: Offset) {
        child?.let {
            if(alpha == 0) {
                layer = null
                return
            }
            layer = context.pushOpacity(offset, alpha!!, { c, o -> super.paint(c, o) }, layer as OpacityLayer?)
        }
    }
}