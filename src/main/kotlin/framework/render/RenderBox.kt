package framework.render

import common.Offset
import common.PointerEvent
import common.Size
import framework.gesture.HitTestEntry
import framework.gesture.HitTestResult

abstract class RenderBox : RenderObject() {
    override var size: Size = Size.zero

    override fun setupParentData(child: RenderObject) {
        child.parentData = BoxParentData()
    }

    open fun hitTest(result: HitTestResult, position: Offset): Boolean {
        if(size.contains(position)) {
            if(hitTestChildren(result, position) || hitTestSelf(position)) {
                result.add(HitTestEntry(this))
                return true
            }
        }
        return false
    }

    open fun hitTestChildren(result: HitTestResult, position: Offset): Boolean = false

    open fun hitTestSelf(position: Offset): Boolean = false

    override fun handleEvent(event: PointerEvent, entry: HitTestEntry) {
        // Do Nothing
    }
}