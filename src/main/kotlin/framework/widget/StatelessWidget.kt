package framework.widget

import framework.element.BuildContext
import framework.element.Element
import framework.element.StatelessElement

abstract class StatelessWidget: Widget() {
    override fun createElement(): Element = StatelessElement(this)

    abstract fun build(context: BuildContext): Widget
}