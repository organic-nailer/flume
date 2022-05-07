package framework.render.clip

import common.Clip
import common.Offset
import framework.PaintingContext
import framework.painting.BorderRadius
import framework.render.RenderBox
import org.jetbrains.skia.RRect

class RenderClipRRect(
    val borderRadius: BorderRadius,
    clipper: CustomClipper<RRect>? = null,
    clipBehavior: Clip = Clip.AntiAlias,
    child: RenderBox? = null,
) : RenderCustomClip<RRect>(clipper, clipBehavior, child) {

    override val defaultClip: RRect
        get() = borderRadius.toRRect(size.and(Offset.zero))

    override fun paint(context: PaintingContext, offset: Offset) {
        if (child != null) {
            context.pushClipRRect(offset,
                clip!!,
                clip!!,
                { c, o -> super.paint(c, o) },
                clipBehavior)
        }
    }
}