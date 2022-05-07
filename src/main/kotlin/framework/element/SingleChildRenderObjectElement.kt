package framework.element

import framework.render.RenderObject
import framework.render.mixin.RenderObjectWithChild
import framework.widget.SingleChildRenderObjectWidget

class SingleChildRenderObjectElement(widget: SingleChildRenderObjectWidget) :
        RenderObjectElement(widget) {
    val widgetCasted: SingleChildRenderObjectWidget = widget
    private var child: Element? = null

    override fun mount(parent: Element?) {
        super.mount(parent)
        child = updateChild(child, widgetCasted.child)
    }

    override fun insertRenderObjectChild(child: RenderObject) {
        (renderObject as RenderObjectWithChild<RenderObject>).child = child
    }
}