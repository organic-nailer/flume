package framework.widget

import framework.element.Element

abstract class Widget {
    companion object {
        fun canUpdate(oldWidget: Widget, newWidget: Widget): Boolean {
            return oldWidget::class == newWidget::class
        }
    }

    abstract fun createElement(): Element
}