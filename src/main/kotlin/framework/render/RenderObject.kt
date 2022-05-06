package framework.render

import common.Offset
import common.Size
import framework.PaintingContext
import framework.geometrics.BoxConstraints

abstract class RenderObject {
    var parentData: ParentData? = null

    abstract var size: Size

    abstract fun layout(constraints: BoxConstraints)

    abstract fun paint(context: PaintingContext, offset: Offset)
}