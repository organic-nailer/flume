package framework.widget

import framework.animation.AnimationController
import framework.render.RenderAnimatedOpacity

class FadeTransition(
    private val opacity: AnimationController,
    child: Widget? = null
): SingleChildRenderObjectWidget<RenderAnimatedOpacity>(child) {
    override fun createRenderObject(): RenderAnimatedOpacity {
        return RenderAnimatedOpacity(
            opacity = opacity
        )
    }

    override fun updateRenderObject(renderObject: RenderAnimatedOpacity) {
        renderObject.opacity = opacity
    }
}
