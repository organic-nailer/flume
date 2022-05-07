package framework.widget

import framework.element.Element
import framework.element.SingleChildRenderObjectElement

abstract class SingleChildRenderObjectWidget(
    val child: Widget?,
) : RenderObjectWidget() {
    override fun createElement(): Element = SingleChildRenderObjectElement(this)
}