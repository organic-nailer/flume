package framework.element

import framework.widget.InheritedWidget
import framework.widget.Widget
import kotlin.reflect.KClass

interface BuildContext {
    val widget: Widget
    var owner: BuildOwner?

    fun <T: InheritedWidget> dependOnInheritedWidgetOfExactType(type: KClass<T>): T?

    fun <T: InheritedWidget> getElementForInheritedWidgetOfExactType(type: KClass<T>): InheritedElement?
}