package framework.element

import framework.render.RenderObject
import framework.widget.RenderObjectWidget
import framework.widget.Widget

abstract class RenderObjectElement<T: RenderObject>(
    widget: RenderObjectWidget<T>
) : Element(widget) {
    private val widgetCasted: RenderObjectWidget<T>
        get() = widget as RenderObjectWidget<T>

    override var renderObject: RenderObject? = null

    private var ancestorRenderObjectElement: RenderObjectElement<*>? = null


    override fun mount(parent: Element?) {
        super.mount(parent)
        renderObject = (widget as RenderObjectWidget<*>).createRenderObject()
        attachRenderObject()
        dirty = false
    }

    override fun update(newWidget: Widget) {
        super.update(newWidget)
        performRebuild()
    }

    override fun performRebuild() {
        widgetCasted.updateRenderObject(renderObject as T)
        dirty = false
    }

    override fun attachRenderObject() {
        ancestorRenderObjectElement = findAncestorRenderObjectElement()
        ancestorRenderObjectElement?.insertRenderObjectChild(renderObject!!)
    }

    override fun detachRenderObject() {
        if (ancestorRenderObjectElement != null) {
            ancestorRenderObjectElement!!.removeRenderObjectChild(renderObject!!)
            ancestorRenderObjectElement = null
        }
    }

    /**
     * Elementツリーで一番直近のRenderObjectElementを探す
     */
    fun findAncestorRenderObjectElement(): RenderObjectElement<*>? {
        var ancestor = parent
        while(ancestor != null && ancestor !is RenderObjectElement<*>) {
            ancestor = ancestor.parent
        }
        return ancestor as RenderObjectElement<*>?
    }

    /**
     * 渡されたRenderObjectを子に追加する
     * 実装はサブクラスで行う(子を持たないクラスは実装しない)
     */
    open fun insertRenderObjectChild(child: RenderObject) {

    }

    open fun removeRenderObjectChild(child: RenderObject) {

    }
}