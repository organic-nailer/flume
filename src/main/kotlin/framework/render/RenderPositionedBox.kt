package framework.render

import common.Offset
import common.Size
import framework.PaintingContext
import framework.geometrics.Alignment
import framework.geometrics.BoxConstraints

class RenderPositionedBox(
    val child: RenderObject? = null,
    val widthFactor: Double? = null,
    val heightFactor: Double? = null,
    val alignment: Alignment = Alignment.center,
) : RenderBox() {
    init {
        child?.parentData = BoxParentData()
    }

    override fun layout(constraints: BoxConstraints) {
        val shrinkWrapWidth =
            widthFactor != null || constraints.maxWidth == Double.POSITIVE_INFINITY
        val shrinkWrapHeight =
            heightFactor != null || constraints.maxHeight == Double.POSITIVE_INFINITY

        if (child != null) {
            child.layout(constraints.loosen())
            size = constraints.constrain(Size(
                if (shrinkWrapWidth) child.size.width * (widthFactor
                    ?: 0.0) else Double.POSITIVE_INFINITY,
                if (shrinkWrapHeight) child.size.height * (heightFactor
                    ?: 0.0) else Double.POSITIVE_INFINITY,
            ))
            alignChild()
        } else {
            size =
                constraints.constrain(Size(if (shrinkWrapWidth) 0.0 else Double.POSITIVE_INFINITY,
                    if (shrinkWrapHeight) 0.0 else Double.POSITIVE_INFINITY))
        }
    }

    /// alignmentに沿うように子のoffsetを決定する
    private fun alignChild() {
        val childParentData = child!!.parentData as BoxParentData
        childParentData.offset = alignment.computeOffset(size, child.size)
    }

    override fun paint(context: PaintingContext, offset: Offset) {
        if (child != null) {
            val childParentData = child.parentData as BoxParentData
            child.paint(context, childParentData.offset + offset)
        }
    }
}