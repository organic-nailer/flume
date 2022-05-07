package framework.element

import framework.render.RenderObject
import framework.render.mixin.ContainerRenderObject
import framework.widget.MultiChildRenderObjectWidget
import framework.widget.Widget

class MultiChildRenderObjectElement<T : RenderObject>(widget: MultiChildRenderObjectWidget<T>) :
        RenderObjectElement<T>(widget) {
    val widgetCasted: MultiChildRenderObjectWidget<*> get() = widget as MultiChildRenderObjectWidget<*>
    private var children: List<Element> = listOf()

    override fun visitChildren(visitor: ElementVisitor) {
        children.forEach(visitor)
    }

    override fun update(newWidget: Widget) {
        super.update(newWidget)
        children = updateChildren(children, widgetCasted.children)
    }

    override fun mount(parent: Element?) {
        super.mount(parent)
        children = widgetCasted.children.map {
            inflateWidget(it)
        }
    }

    override fun insertRenderObjectChild(child: RenderObject) {
        (renderObject as ContainerRenderObject<RenderObject>).insert(child)
    }

    override fun removeRenderObjectChild(child: RenderObject) {
        (renderObject as ContainerRenderObject<RenderObject>).remove(child)
    }
}