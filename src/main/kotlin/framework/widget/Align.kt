package framework.widget

import framework.geometrics.Alignment
import framework.render.RenderObject
import framework.render.RenderPositionedBox

class Align(
    child: Widget?,
    val alignment: Alignment = Alignment.center,
    val widthFactor: Double? = null,
    val heightFactor: Double? = null,
) : SingleChildRenderObjectWidget(child) {
    override fun createRenderObject(): RenderObject {
        return RenderPositionedBox(alignment = alignment,
            widthFactor = widthFactor,
            heightFactor = heightFactor)
    }
}