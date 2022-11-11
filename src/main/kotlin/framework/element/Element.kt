package framework.element

import framework.render.RenderObject
import framework.widget.InheritedWidget
import framework.widget.Widget
import kotlin.reflect.KClass

abstract class Element(
    widget: Widget,
) : Comparable<Element>, BuildContext {
    var parent: Element? = null
    var depth: Int = 0
    override var owner: BuildOwner? = null
    var dirty: Boolean = true
    var inDirtyList: Boolean = false

    final override var widget: Widget = widget
        private set

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
        depth = if (parent != null) parent.depth + 1 else 1
        parent?.let {
            owner = it.owner
        }
        updateInheritance()
    }

    /**
     * 子となるWidget/Elementをとりそれらをツリー下部として展開する
     */
    fun updateChild(child: Element?, newWidget: Widget?): Element? {
        if (newWidget == null) {
            if (child != null) {
                deactivateChild(child)
            }
            return null
        }
        if (child != null) {
            val newChild: Element
            // Flutterではここで`hasSameSuperclass`ということを確認しているが、
            // HotReloadによるStatefulElementとStatelessElementの置換を検知するものなので
            // 省略する
            if (child.widget == newWidget) {
                newChild = child
            } else if (Widget.canUpdate(child.widget, newWidget)) {
                child.update(newWidget)
                newChild = child
            } else {
                deactivateChild(child)
                newChild = inflateWidget(newWidget)
            }
            return newChild
        } else {
            return inflateWidget(newWidget)
        }
    }

    open fun update(newWidget: Widget) {
        widget = newWidget
    }

    /**
     * 与えられたWidgetから子Elementを作成しツリーに追加する
     */
    protected fun inflateWidget(newWidget: Widget): Element {
        val newChild = newWidget.createElement()
        newChild.mount(this)
        return newChild
    }

    open fun attachRenderObject() {}

    open fun detachRenderObject() {
        visitChildren {
            it.detachRenderObject()
        }
    }

    protected fun deactivateChild(child: Element) {
        child.parent = null
        child.detachRenderObject()
    }

    open fun didChangeDependencies() {
        markNeedsBuild()
    }

    fun markNeedsBuild() {
        if (dirty) return
        dirty = true
        owner!!.scheduleBuildFor(this)
    }

    fun rebuild() {
        performRebuild()
    }

    abstract fun performRebuild()


    var inheritedWidgets: MutableMap<KClass<*>, InheritedElement>? = null

    /**
     * 自分の依存しているInheritedElementを格納する場所
     *
     * [Element.deactivate]で自身を依存先のリストから消すために保持
     */
    private var dependencies = HashSet<InheritedElement>()

    private fun dependOnInheritedElement(ancestor: InheritedElement): InheritedWidget {
        dependencies.add(ancestor)
        ancestor.updateDependencies(this)
        return ancestor.widget as InheritedWidget
    }

    override fun <T: InheritedWidget> dependOnInheritedWidgetOfExactType(type: KClass<T>): T? {
        val ancestor = if(inheritedWidgets == null) null else inheritedWidgets!![type]
        if(ancestor != null) {
            return dependOnInheritedElement(ancestor) as T
        }
        return null
    }

    override fun <T: InheritedWidget> getElementForInheritedWidgetOfExactType(type: KClass<T>): InheritedElement? {
        return if(inheritedWidgets == null) null else inheritedWidgets!![type]
    }

    protected open fun updateInheritance() {
        inheritedWidgets = parent?.inheritedWidgets
    }


    override operator fun compareTo(other: Element): Int {
        when {
            depth < other.depth -> return -1
            other.depth < depth -> return 1
            !dirty && other.dirty -> return -1
            dirty && !other.dirty -> return 1
        }
        return 0
    }
}

typealias ElementVisitor = (child: Element) -> Unit
