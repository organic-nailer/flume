package framework.render

import common.Offset
import framework.PaintingContext
import framework.geometrics.BoxConstraints

class RenderConstrainedBox(
    private val additionalConstraints: BoxConstraints,
    child: RenderBox?,
) : RenderProxyBox(child) {

    override fun layout(constraints: BoxConstraints) {
        if (child != null) {
            child.layout(additionalConstraints.enforce(constraints))
            size = child.size
        } else {
            size = additionalConstraints.enforce(constraints).constrain(size)
        }
    }

    override fun paint(context: PaintingContext, offset: Offset) {
        child?.paint(context, offset)
    }
}