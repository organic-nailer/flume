package framework.render

import framework.geometrics.BoxConstraints

class RenderConstrainedBox(
    private val additionalConstraints: BoxConstraints,
) : RenderProxyBox() {

    override fun performLayout() {
        if (child != null) {
            child!!.layout(additionalConstraints.enforce(constraints))
            size = child!!.size
        } else {
            size = additionalConstraints.enforce(constraints).constrain(size)
        }
    }
}