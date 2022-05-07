package framework.render.mixin

import framework.render.BoxParentData
import framework.render.RenderObject

interface ContainerRenderObject<ChildType : RenderObject> {
    val children: MutableList<ChildType>

    fun insert(child: ChildType) {
        children.add(child)
        child.parentData = BoxParentData()
    }
}
