package framework.render.clip

import common.Clip
import common.ClipRectLayer
import common.Offset
import framework.PaintingContext
import org.jetbrains.skia.Rect

class RenderClipRect(
    clipper: CustomClipper<Rect>? = null, clipBehavior: Clip = Clip.AntiAlias,
) : RenderCustomClip<Rect>(clipper, clipBehavior) {
    override val defaultClip: Rect
        get() = size.and(Offset.zero)

    override fun paint(context: PaintingContext, offset: Offset) {
        if (child != null) {
            layer = context.pushClipRect(
                offset, clip!!, { c, o -> super.paint(c, o) }, clipBehavior, layer as ClipRectLayer?
            )
        } else {
            layer = null
        }
    }
}