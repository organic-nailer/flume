package framework.widget

import framework.element.Element
import framework.element.MultiChildRenderObjectElement
import framework.render.RenderObject

abstract class MultiChildRenderObjectWidget<RenderObjectType : RenderObject>(
    val children: List<Widget>,
) : RenderObjectWidget<RenderObjectType>() {
    override fun createElement(): Element = MultiChildRenderObjectElement(this)
}