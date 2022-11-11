package framework.element

import framework.widget.InheritedWidget
import framework.widget.Widget

class InheritedElement(
    widget: InheritedWidget
): ComponentElement(widget) {
    private val dependents: MutableSet<Element> = HashSet()

    override fun build(): Widget = (widget as InheritedWidget).child

    override fun updateInheritance() {
        val incomingWidgets = parent?.inheritedWidgets
        if(incomingWidgets != null) {
            inheritedWidgets = HashMap(incomingWidgets)
        }
        else {
            inheritedWidgets = HashMap()
            inheritedWidgets!![widget::class] = this
        }
    }

    fun updateDependencies(dependent: Element) {
        dependents.add(dependent)
    }

    override fun update(newWidget: Widget) {
        val oldWidget = widget as InheritedWidget
        super.update(newWidget)
        if((widget as InheritedWidget).updateShouldNotify(oldWidget)) {
            notifyClients()
        }
        dirty = true
        rebuild()
    }

    private fun notifyClients() {
        for(dependent in dependents) {
            dependent.didChangeDependencies()
        }
    }
}