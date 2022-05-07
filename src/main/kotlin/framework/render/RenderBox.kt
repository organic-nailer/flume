package framework.render

import common.Size

abstract class RenderBox : RenderObject() {
    override var size: Size = Size.zero

    override fun setupParentData(child: RenderObject) {
        child.parentData = BoxParentData()
    }
}