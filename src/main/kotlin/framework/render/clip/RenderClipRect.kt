package framework.render.clip

import common.Clip
import common.Offset
import framework.PaintingContext
import framework.render.RenderBox
import org.jetbrains.skia.Rect

class RenderClipRect(
    clipper: CustomClipper<Rect>? = null, clipBehavior: Clip = Clip.AntiAlias,
    child: RenderBox? = null,
) : RenderCustomClip<Rect>(clipper, clipBehavior, child) {
    override val defaultClip: Rect
        get() = size.and(Offset.zero)

    override fun paint(context: PaintingContext, offset: Offset) {
        if (child != null) {
            context.pushClipRect(offset, clip!!, { c, o -> super.paint(c, o) }, clipBehavior)
        }
    }
}