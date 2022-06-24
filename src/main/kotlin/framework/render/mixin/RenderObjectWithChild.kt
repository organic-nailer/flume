package framework.render.mixin

import framework.render.BoxParentData
import framework.render.RenderObject

interface RenderObjectWithChild<ChildType : RenderObject> {
    var child: ChildType?

    fun setRenderObjectChild(child: ChildType) {
        this.child = child
        this.child!!.parentData = BoxParentData()
    }

    /**
     * Implement先の[RenderObject.visitChildren]で必ず呼ぶ
     */
    fun visitChildren(visitor: RenderObjectVisitor) {
        child?.let(visitor)
    }
}

typealias RenderObjectVisitor = (child: RenderObject) -> Unit