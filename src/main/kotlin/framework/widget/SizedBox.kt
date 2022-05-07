package framework.widget

import framework.geometrics.BoxConstraints
import framework.render.RenderConstrainedBox
import framework.render.RenderObject

class SizedBox(
    child: Widget?,
    val width: Double? = null,
    val height: Double? = null,
) : SingleChildRenderObjectWidget(child) {
    override fun createRenderObject(): RenderObject =
        RenderConstrainedBox(additionalConstraints = BoxConstraints.tightFor(width, height))
}