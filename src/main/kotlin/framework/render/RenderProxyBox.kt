package framework.render

import framework.geometrics.BoxConstraints

abstract class RenderProxyBox : RenderBox() {
    var child: RenderBox? = null
    override fun layout(constraints: BoxConstraints) {
        if (child != null) {
            child!!.layout(constraints)
            size = child!!.size
        } else {
            size = constraints.smallest
        }
    }
}