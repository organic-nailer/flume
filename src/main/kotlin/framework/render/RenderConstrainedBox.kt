package framework.render

import framework.geometrics.BoxConstraints

class RenderConstrainedBox(
    additionalConstraints: BoxConstraints,
) : RenderProxyBox() {
    var additionalConstraints: BoxConstraints by MarkLayoutProperty(additionalConstraints)
    override fun performLayout() {
        if (child != null) {
            child!!.layout(additionalConstraints.enforce(constraints), parentUsesSize = true)
            size = child!!.size
        } else {
            size = additionalConstraints.enforce(constraints).constrain(size)
        }
    }
}