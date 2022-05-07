package framework.widget

import framework.element.Element
import framework.element.SingleChildRenderObjectElement
import framework.render.RenderObject

abstract class SingleChildRenderObjectWidget<RenderObjectType : RenderObject>(
    val child: Widget?,
) : RenderObjectWidget<RenderObjectType>() {
    override fun createElement(): Element = SingleChildRenderObjectElement(this)
}