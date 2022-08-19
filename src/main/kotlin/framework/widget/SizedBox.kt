package framework.widget

import framework.geometrics.BoxConstraints
import framework.render.RenderConstrainedBox

class SizedBox(
    child: Widget?,
    val width: Double? = null,
    val height: Double? = null,
) : SingleChildRenderObjectWidget<RenderConstrainedBox>(child) {
    override fun createRenderObject(): RenderConstrainedBox =
        RenderConstrainedBox(additionalConstraints = BoxConstraints.tightFor(width, height))

    override fun updateRenderObject(renderObject: RenderConstrainedBox) {
        renderObject.additionalConstraints = BoxConstraints.tightFor(width, height)
    }
}