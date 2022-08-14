package framework.render

import common.ContainerLayer
import common.Offset
import common.Size
import framework.PaintingContext
import framework.RenderPipeline
import framework.geometrics.BoxConstraints

abstract class RenderObject {
    var parentData: ParentData? = null
    var parent: RenderObject? = null
    var needsPaint = true
    open val isRepaintBoundary: Boolean = false
    var owner: RenderPipeline? = null

    /**
     * 同じ階層のLayerを保持する
     *
     * [isRepaintBoundary] == true のときしか使われない。
     * [RenderView]ならばscheduleInitialPaintで、その他はrepaintCompositedChildで代入される
     */
    var layer: ContainerLayer? = null

    val attached: Boolean
        get() = owner != null

    lateinit var constraints: BoxConstraints
        private set

    abstract var size: Size

    abstract fun performLayout()

    abstract fun paint(context: PaintingContext, offset: Offset)

    fun layout(constraints: BoxConstraints) {
        this.constraints = constraints
        performLayout()

        markNeedsPaint()
    }

    fun markNeedsPaint() {
        if (needsPaint) return
        needsPaint = true
        if (isRepaintBoundary) {
            owner?.let {
                it.nodesNeedingPaint.add(this)
                it.requestVisualUpdate()
            }
        } else {
            parent!!.markNeedsPaint()
        }
    }

    open fun attach(owner: RenderPipeline) {
        this.owner = owner
        if (needsPaint && layer != null) {
            needsPaint = false
            markNeedsPaint()
        }
    }

    open fun setupParentData(child: RenderObject) {

    }

    fun adoptChild(child: RenderObject) {
        setupParentData(child)
        child.parent = this
        if (attached) {
            child.attach(owner!!)
        }
    }
}