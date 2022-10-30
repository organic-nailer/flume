package framework.render.mixin

import common.Offset
import framework.RenderPipeline
import framework.gesture.HitTestResult
import framework.render.BoxParentData
import framework.render.RenderBox
import framework.render.RenderObject
import framework.render.RenderObjectVisitor

interface ContainerRenderObject<ChildType : RenderObject> {
    val children: MutableList<ChildType>
    val thisRef: RenderObject

    fun insert(child: ChildType) {
        thisRef.adoptChild(child)
        children.add(child)
    }

    fun remove(child: ChildType) {
        children.remove(child)
        thisRef.dropChild(child)
    }

    /**
     * Implement先の[RenderObject.attach]で必ず呼ぶ
     */
    fun attachChildren(owner: RenderPipeline) {
        for (child in children) {
            child.attach(owner)
        }
    }

    /**
     * Implement先の[RenderObject.detach]で必ず呼ぶ
     */
    fun detachChildren() {
        for (child in children) {
            child.detach()
        }
    }

    /**
     * Implement先の[RenderObject.visitChildren]で必ず呼ぶ
     */
    fun visitChildren(visitor: RenderObjectVisitor) {
        for (child in children) {
            visitor(child)
        }
    }

    /**
     * Implement先の[RenderObject.redepthChildren]で必ず呼ぶ
     */
    fun redepthChildren(callback: (child: RenderObject) -> Unit) {
        for (child in children) {
            child.let(callback)
        }
    }

    /**
     * 複数の子を持つ場合の標準のHitTest
     *
     * [ChildType] is [RenderBox] のときのみ呼び出し可
     *
     * どれかの子に判定があれば即終了する
     */
    fun defaultHitTestChildren(result: HitTestResult, position: Offset): Boolean {
        for(child in children) {
            val childParentData = child.parentData as BoxParentData
            val isHit = result.addWithPaintOffset(
                offset = childParentData.offset,
                position = position,
                hitTest = { testResult, transformed ->
                    (child as RenderBox).hitTest(testResult, transformed)
                }
            )
            if(isHit) {
                return true
            }
        }
        return false
    }
}
