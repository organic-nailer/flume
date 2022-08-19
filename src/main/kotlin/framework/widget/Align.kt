package framework.widget

import framework.geometrics.Alignment
import framework.render.RenderPositionedBox

class Align(
    child: Widget?,
    val alignment: Alignment = Alignment.center,
    val widthFactor: Double? = null,
    val heightFactor: Double? = null,
) : SingleChildRenderObjectWidget<RenderPositionedBox>(child) {
    override fun createRenderObject(): RenderPositionedBox {
        return RenderPositionedBox(alignment = alignment,
            widthFactor = widthFactor,
            heightFactor = heightFactor)
    }

    override fun updateRenderObject(renderObject: RenderPositionedBox) {
        renderObject.let {
            it.alignment = alignment
            it.widthFactor = widthFactor
            it.heightFactor = heightFactor
        }
    }
}