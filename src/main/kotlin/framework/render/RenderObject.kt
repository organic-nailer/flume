package framework.render

import common.ContainerLayer
import common.Offset
import common.Size
import framework.PaintingContext
import framework.RenderPipeline
import framework.geometrics.BoxConstraints

abstract class RenderObject {
    companion object {
        private val cleanChildRelayoutBoundary: RenderObjectVisitor = {
            it.cleanRelayoutBounary()
        }
    }
    var parentData: ParentData? = null
    var parent: RenderObject? = null
    var needsPaint = true
    open val isRepaintBoundary: Boolean = false
    var owner: RenderPipeline? = null
    var needsLayout = true
    var relayoutBoundary: RenderObject? = null
    open val sizedByParent: Boolean = false
    var depth: Int = 0

    fun redepthChild(child: RenderObject) {
        if(child.depth <= depth) {
            child.depth = depth + 1
            child.redepthChildren()
        }
    }

    open fun redepthChildren() {}

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

    fun layout(constraints: BoxConstraints, parentUsesSize: Boolean = false) {
        // relayoutBoundaryの再計算
        val relayoutBoundary = if(!parentUsesSize || sizedByParent || constraints.isTight || parent == null) {
            this
        }
        else {
            parent!!.relayoutBoundary
        }
        if(!needsLayout && this.constraints == constraints && this.relayoutBoundary == relayoutBoundary) {
            // 制約とrelayoutBoundaryに変化がなく再レイアウト要求も無ければなにもしない
            return
        }
        this.constraints = constraints
        if(this.relayoutBoundary != null && relayoutBoundary != this.relayoutBoundary) {
            // relayoutBoundaryに更新があった場合、子のrelayoutBoundaryを一旦リセットする
            visitChildren(cleanChildRelayoutBoundary)
        }
        this.relayoutBoundary = relayoutBoundary
        // FlutterではsizedByParentでの分岐が存在するが、
        // 簡略化のためperformResizeの機能はperformLayout内に移動する
        performLayout()

        needsLayout = false
        markNeedsPaint()
    }

    /**
     * constraintsをそのままlayoutする
     */
    fun layoutWithoutResize() {
        performLayout()

        needsLayout = false
        markNeedsPaint()
    }

    fun cleanRelayoutBounary() {
        if(relayoutBoundary != this) {
            relayoutBoundary = null
            needsLayout = true
            visitChildren(cleanChildRelayoutBoundary)
        }
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

    fun markNeedsLayout() {
        if(needsLayout) return
        if(relayoutBoundary != this) {
            markParentNeedsLayout()
        }
        else {
            needsLayout = true
            owner?.let {
                it.nodesNeedingLayout.add(this)
                it.requestVisualUpdate()
            }
        }
    }

    fun markParentNeedsLayout() {
        needsLayout = true
        parent!!.markNeedsLayout()
    }


    open fun attach(owner: RenderPipeline) {
        this.owner = owner
        if (needsLayout && relayoutBoundary != null) {
            needsLayout = false
            markNeedsLayout()
        }
        if (needsPaint && layer != null) {
            needsPaint = false
            markNeedsPaint()
        }
    }

    open fun setupParentData(child: RenderObject) {

    }

    /**
     * 子へのアクセスの抽象化。
     *
     * 子が存在する場合、その子の数だけコールバックが呼ばれる
     */
    open fun visitChildren(visitor: RenderObjectVisitor) {

    }

    fun adoptChild(child: RenderObject) {
        setupParentData(child)
        child.parent = this
        if (attached) {
            child.attach(owner!!)
        }
        redepthChild(child)
    }
}

typealias RenderObjectVisitor = (child: RenderObject) -> Unit