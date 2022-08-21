package framework.element

import framework.render.RenderObject
import framework.render.mixin.RenderObjectWithChild
import framework.widget.RenderObjectToWidgetAdapter
import framework.widget.RenderObjectWidget
import framework.widget.Widget

class RenderObjectToWidgetElement<T: RenderObject>(widget: RenderObjectWidget<T>) : RenderObjectElement<T>(widget) {
    private var child: Element? = null
    var newWidget: Widget? = null

    override fun visitChildren(visitor: ElementVisitor) {
        child?.let(visitor)
    }

    override fun mount(parent: Element?) {
        super.mount(parent)
        _rebuild()
    }

    override fun update(newWidget: Widget) {
        super.update(newWidget)
        _rebuild()
    }

    override fun performRebuild() {
        if (newWidget != null) {
            val tmp = newWidget!!
            newWidget = null
            update(tmp)
        }
        super.performRebuild()
    }

    /**
     * ツリーを再構築する
     */
    private fun _rebuild() {
        child = updateChild(child, (widget as RenderObjectToWidgetAdapter).child)
    }

    override fun insertRenderObjectChild(child: RenderObject) {
        (renderObject as RenderObjectWithChild<RenderObject>).child = child
    }
}