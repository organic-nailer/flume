package framework.element

import framework.render.RenderObject
import framework.render.mixin.RenderObjectWithChild
import framework.widget.RenderObjectToWidgetAdapter
import framework.widget.RenderObjectWidget

class RenderObjectToWidgetElement(widget: RenderObjectWidget) : RenderObjectElement(widget) {
    private var child: Element? = null
    override fun mount(parent: Element?) {
        super.mount(parent)
        rebuild()
    }

    /**
     * ツリーを再構築する
     */
    private fun rebuild() {
        child = updateChild(child, (widget as RenderObjectToWidgetAdapter).child)
    }

    override fun insertRenderObjectChild(child: RenderObject) {
        (renderObject as RenderObjectWithChild<RenderObject>).child = child
    }
}