package framework.geometrics

import framework.element.Element
import framework.element.RenderObjectElement
import framework.render.RenderObject
import framework.render.mixin.ContainerRenderObject
import framework.widget.MultiChildRenderObjectWidget

class MultiChildRenderObjectElement(widget: MultiChildRenderObjectWidget) :
        RenderObjectElement(widget) {
    val widgetCasted: MultiChildRenderObjectWidget = widget
    private var children: List<Element> = listOf()

    override fun mount(parent: Element?) {
        super.mount(parent)
        children = widgetCasted.children.map {
            inflateWidget(it)
        }
    }

    override fun insertRenderObjectChild(child: RenderObject) {
        (renderObject as ContainerRenderObject<RenderObject>).insert(child)
    }
}