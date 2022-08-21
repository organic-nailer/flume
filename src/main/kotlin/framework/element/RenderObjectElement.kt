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

    protected fun updateChildren(
        oldChildren: List<Element>,
        newWidgets: List<Widget>,
        forgottenChildren: Set<Element>? = null,
    ): List<Element> {
        fun replaceWithNullIfForgotten(child: Element): Element? {
            return if (forgottenChildren?.contains(child) == true) null else child
        }

        var newChildrenTop = 0
        var oldChildrenTop = 0
        var newChildrenBottom = newWidgets.size - 1
        var oldChildrenBottom = oldChildren.size - 1

        val newChildren: MutableList<Element?> =
            if (oldChildren.size == newWidgets.size) oldChildren.toMutableList() else (1..newWidgets.size).map { null }
                .toMutableList()

        while ((oldChildrenTop <= oldChildrenBottom) && (newChildrenTop <= newChildrenBottom)) {
            val oldChild = replaceWithNullIfForgotten(oldChildren[oldChildrenTop])
            val newWidget = newWidgets[newChildrenTop]
            if (oldChild == null || !Widget.canUpdate(oldChild.widget!!, newWidget)) {
                break
            }
            val newChild = updateChild(oldChild, newWidget)
            newChildren[newChildrenTop] = newChild
            newChildrenTop++
            oldChildrenTop++
        }

        while ((oldChildrenTop <= oldChildrenBottom) && (newChildrenTop <= newChildrenBottom)) {
            val oldChild = replaceWithNullIfForgotten(oldChildren[oldChildrenBottom])
            val newWidget = newWidgets[newChildrenBottom]
            if (oldChild == null || !Widget.canUpdate(oldChild.widget!!, newWidget)) {
                break
            }
            newChildrenTop--
            oldChildrenTop--
        }

        val haveOldChildren = oldChildrenTop <= oldChildrenBottom
        if (haveOldChildren) {
            while (oldChildrenTop <= oldChildrenBottom) {
                val oldChild = replaceWithNullIfForgotten(oldChildren[oldChildrenTop])
                if (oldChild != null) {
                    deactivateChild(oldChild)
                }
                oldChildrenTop++
            }
        }

        while (newChildrenTop <= newChildrenBottom) {
            val oldChild: Element? = null
            val newWidget = newWidgets[newChildrenTop]
            val newChild = updateChild(oldChild, newWidget)
            newChildren[newChildrenTop] = newChild
            newChildrenTop++
        }

        newChildrenBottom = newWidgets.size - 1
        oldChildrenBottom = oldChildren.size - 1

        while ((oldChildrenTop <= oldChildrenBottom) && (newChildrenTop <= newChildrenBottom)) {
            val oldChild = oldChildren[oldChildrenTop]
            val newWidget = newWidgets[newChildrenTop]
            val newChild = updateChild(oldChild, newWidget)
            newChildren[newChildrenTop] = newChild
            newChildrenTop++
            oldChildrenTop++
        }

        return newChildren.mapNotNull { it }
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