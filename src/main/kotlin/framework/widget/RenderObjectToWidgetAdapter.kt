package framework.widget

import framework.element.Element
import framework.element.RenderObjectToWidgetElement
import framework.render.RenderView

class RenderObjectToWidgetAdapter(
    val child: Widget?, val container: RenderView,
) : RenderObjectWidget() {
    override fun createElement(): Element = RenderObjectToWidgetElement(this)

    override fun createRenderObject(): RenderView = container

    fun attachToRenderTree(): RenderObjectToWidgetElement {
        val element = createElement() as RenderObjectToWidgetElement
        element.mount(null)
        return element
    }
}