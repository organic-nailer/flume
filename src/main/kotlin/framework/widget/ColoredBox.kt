package framework.widget

import framework.render.RenderColoredBox
import framework.render.RenderObject

class ColoredBox(
    child: Widget? = null,
    val color: Int,
) : SingleChildRenderObjectWidget(child) {
    override fun createRenderObject(): RenderObject = RenderColoredBox(color)
}