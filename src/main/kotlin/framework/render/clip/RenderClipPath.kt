package framework.render.clip

import common.Clip
import common.Offset
import framework.PaintingContext
import framework.render.RenderBox
import org.jetbrains.skia.Path

class RenderClipPath(
    clipper: CustomClipper<Path>? = null, clipBehavior: Clip = Clip.AntiAlias,
    child: RenderBox? = null,
) : RenderCustomClip<Path>(clipper, clipBehavior, child) {

    override val defaultClip: Path
        get() = Path().apply {
            addRect(size.and(Offset.zero))
        }

    override fun paint(context: PaintingContext, offset: Offset) {
        if (child != null) {
            context.pushClipPath(offset,
                size.and(Offset.zero),
                clip!!,
                { c, o -> super.paint(c, o) },
                clipBehavior)
        }
    }
}