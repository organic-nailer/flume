package framework.render.mixin

import framework.render.BoxParentData
import framework.render.RenderObject

interface RenderObjectWithChild<ChildType : RenderObject> {
    var child: ChildType?

    fun setRenderObjectChild(child: ChildType) {
        this.child = child
        this.child!!.parentData = BoxParentData()
    }
}