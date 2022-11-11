package framework.widget

import framework.element.Element
import framework.element.InheritedElement

abstract class InheritedWidget(
    val child: Widget
): Widget() {
    override fun createElement(): Element = InheritedElement(this)

    abstract fun updateShouldNotify(oldWidget: InheritedWidget): Boolean
}