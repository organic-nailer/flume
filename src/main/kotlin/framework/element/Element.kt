package framework.element

import framework.render.RenderObject
import framework.widget.Widget

abstract class Element(
    open var widget: Widget?,
) {
    var parent: Element? = null

    /**
     * 自分と子を探索して一番近い[RenderObjectElement]の持つ[RenderObject]を返す
     */
    open var renderObject: RenderObject? = null
        get() {
            var result: RenderObject? = null
            fun visit(element: Element) {
                if(element is RenderObjectElement<*>) {
                    result = element.renderObject
                } else {
                    element.visitChildren { visit(it) }
                }
            }
            visit(this)
            return result
        }
        protected set

    /**
     * 子へのアクセス。子を持つElementはoverrideすること
     */
    open fun visitChildren(visitor: ElementVisitor) {

    }

    /**
     * 自身をElementツリーに追加する
     */
    open fun mount(parent: Element?) {
        this.parent = parent
    }

    /**
     * 子となるWidget/Elementをとりそれらをツリー下部として展開する
     */
    protected fun updateChild(child: Element?, newWidget: Widget?): Element? {
        // とりあえずchildが来る場合は考えない
        assert(child == null)
        if (newWidget == null) return null
        return inflateWidget(newWidget)
    }

    /**
     * 与えられたWidgetから子Elementを作成しツリーに追加する
     */
    protected fun inflateWidget(newWidget: Widget): Element {
        val newChild = newWidget.createElement()
        newChild.mount(this)
        return newChild
    }

    /**
     * 自身がRenderObjectを持つ場合はRenderツリーにそれを追加する
     */
    protected open fun attachRenderObject() {

    }
}

typealias ElementVisitor = (child: Element) -> Unit
