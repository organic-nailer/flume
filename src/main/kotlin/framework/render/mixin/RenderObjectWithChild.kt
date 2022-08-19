package framework.render.mixin

import framework.RenderPipeline
import framework.render.RenderObject
import framework.render.RenderObjectVisitor
import kotlin.reflect.KProperty

interface RenderObjectWithChild<ChildType : RenderObject> {
    var child: ChildType?

    /**
     * Implement先の[RenderObject.attach]で必ず呼ぶ
     */
    fun attachChild(owner: RenderPipeline) {
        child?.attach(owner)
    }

    /**
     * Implement先の[RenderObject.detach]で必ず呼ぶ
     */
    fun detachChild() {
        child?.detach()
    }

    /**
     * Implement先の[RenderObject.visitChildren]で必ず呼ぶ
     */
    fun visitChildren(visitor: RenderObjectVisitor) {
        child?.let(visitor)
    }

    /**
     * Implement先の[RenderObject.redepthChildren]で必ず呼ぶ
     */
    fun redepthChildren(callback: (child: RenderObject) -> Unit) {
        child?.let(callback)
    }

    class ChildDelegate<ChildType : RenderObject> {
        var child: ChildType? = null
        operator fun getValue(thisRef: RenderObject, property: KProperty<*>): ChildType? {
            return child
        }

        operator fun setValue(thisRef: RenderObject, property: KProperty<*>, value: ChildType?) {
            if (child != null) {
                thisRef.dropChild(child!!)
            }
            child = value
            child?.let {
                thisRef.adoptChild(it)
            }
        }
    }
}

typealias RenderObjectVisitor = (child: RenderObject) -> Unit