package framework.element

import framework.render.RenderObject
import framework.render.mixin.ContainerRenderObject
import framework.widget.MultiChildRenderObjectWidget

class MultiChildRenderObjectElement<T: RenderObject>(widget: MultiChildRenderObjectWidget<T>) :
        RenderObjectElement<T>(widget) {
    val widgetCasted: MultiChildRenderObjectWidget<T> = widget
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