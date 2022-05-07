package framework.render.clip

import common.Clip
import common.Offset
import framework.PaintingContext
import org.jetbrains.skia.Path
import org.jetbrains.skia.Rect

class RenderClipOval(
    clipper: CustomClipper<Rect>? = null, clipBehavior: Clip = Clip.AntiAlias,
) : RenderCustomClip<Rect>(clipper, clipBehavior) {
    override val defaultClip: Rect
        get() = size.and(Offset.zero)

    override fun paint(context: PaintingContext, offset: Offset) {
        if (child != null) {
            context.pushClipPath(offset,
                clip!!,
                Path().apply { addOval(clip!!) },
                { c, o -> super.paint(c, o) },
                clipBehavior)
        }
    }
}