package framework.widget

import framework.geometrics.Axis
import framework.geometrics.CrossAxisAlignment
import framework.geometrics.MainAxisAlignment
import framework.geometrics.MainAxisSize
import framework.geometrics.VerticalDirection
import framework.render.RenderFlex

class Flex(
    children: List<Widget> = listOf(),
    val direction: Axis,
    val mainAxisAlignment: MainAxisAlignment = MainAxisAlignment.Start,
    val mainAxisSize: MainAxisSize = MainAxisSize.Max,
    val crossAxisAlignment: CrossAxisAlignment = CrossAxisAlignment.Center,
    val verticalDirection: VerticalDirection = VerticalDirection.Down,
) : MultiChildRenderObjectWidget<RenderFlex>(children) {
    override fun createRenderObject(): RenderFlex {
        return RenderFlex(direction,
            mainAxisAlignment,
            mainAxisSize,
            crossAxisAlignment,
            verticalDirection)
    }
}