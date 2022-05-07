package framework.widget

import framework.render.RenderColoredBox

class ColoredBox(
    child: Widget? = null,
    val color: Int,
) : SingleChildRenderObjectWidget<RenderColoredBox>(child) {
    override fun createRenderObject(): RenderColoredBox = RenderColoredBox(color)

    override fun updateRenderObject(renderObject: RenderColoredBox) {
        renderObject.color = color
    }
}