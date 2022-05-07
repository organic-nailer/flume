package framework.element

import framework.widget.Widget

abstract class ComponentElement(widget: Widget) : Element(widget) {
    private var child: Element? = null

    override fun mount(parent: Element?) {
        super.mount(parent)
        firstBuild()
    }

    open fun firstBuild() {
        rebuild()
    }

    override fun performRebuild() {
        val built = build()
        dirty = false
        child = updateChild(child, built)
    }

    abstract fun build(): Widget

    override fun visitChildren(visitor: ElementVisitor) {
        child?.let(visitor)
    }
}