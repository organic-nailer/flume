package framework.render.clip

import common.Clip
import common.ClipRRectLayer
import common.Offset
import framework.PaintingContext
import framework.painting.BorderRadius
import org.jetbrains.skia.RRect

class RenderClipRRect(
    val borderRadius: BorderRadius,
    clipper: CustomClipper<RRect>? = null,
    clipBehavior: Clip = Clip.AntiAlias,
) : RenderCustomClip<RRect>(clipper, clipBehavior) {

    override val defaultClip: RRect
        get() = borderRadius.toRRect(size.and(Offset.zero))

    override fun paint(context: PaintingContext, offset: Offset) {
        if (child != null) {
            layer = context.pushClipRRect(
                offset, clip!!, clip!!, { c, o -> super.paint(c, o) }, clipBehavior, layer as ClipRRectLayer?
            )
        } else {
            layer = null
        }
    }
}