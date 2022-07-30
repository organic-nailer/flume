package framework.widget

import framework.element.Element
import framework.element.MultiChildRenderObjectElement

abstract class MultiChildRenderObjectWidget(
    val children: List<Widget>,
) : RenderObjectWidget() {
    override fun createElement(): Element = MultiChildRenderObjectElement(this)
}