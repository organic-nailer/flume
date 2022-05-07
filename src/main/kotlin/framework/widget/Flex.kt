package framework.widget

import framework.geometrics.Axis
import framework.geometrics.CrossAxisAlignment
import framework.geometrics.MainAxisAlignment
import framework.geometrics.MainAxisSize
import framework.geometrics.VerticalDirection
import framework.render.RenderFlex
import framework.render.RenderObject

class Flex(
    children: List<Widget> = listOf(),
    val direction: Axis,
    val mainAxisAlignment: MainAxisAlignment = MainAxisAlignment.Start,
    val mainAxisSize: MainAxisSize = MainAxisSize.Max,
    val crossAxisAlignment: CrossAxisAlignment = CrossAxisAlignment.Center,
    val verticalDirection: VerticalDirection = VerticalDirection.Down,
) : MultiChildRenderObjectWidget(children) {
    override fun createRenderObject(): RenderObject {
        return RenderFlex(direction,
            mainAxisAlignment,
            mainAxisSize,
            crossAxisAlignment,
            verticalDirection)
    }
}