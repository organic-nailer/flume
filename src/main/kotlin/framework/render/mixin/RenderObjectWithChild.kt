package framework.render.mixin

import framework.RenderPipeline
import framework.render.RenderObject
import kotlin.reflect.KProperty

interface RenderObjectWithChild<ChildType : RenderObject> {
    var child: ChildType?

    fun attachChild(owner: RenderPipeline) {
        child?.attach(owner)
    }

    class ChildDelegate<ChildType : RenderObject> {
        var child: ChildType? = null
        operator fun getValue(thisRef: RenderObject, property: KProperty<*>): ChildType? {
            return child
        }

        operator fun setValue(thisRef: RenderObject, property: KProperty<*>, value: ChildType?) {
            child = value
            child?.let {
                thisRef.adoptChild(it)
            }
        }
    }
}