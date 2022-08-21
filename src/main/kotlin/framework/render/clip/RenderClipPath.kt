package framework.render.clip

import common.Clip
import common.ClipPathLayer
import common.Offset
import framework.PaintingContext
import org.jetbrains.skia.Path

class RenderClipPath(
    clipper: CustomClipper<Path>? = null, clipBehavior: Clip = Clip.AntiAlias,
) : RenderCustomClip<Path>(clipper, clipBehavior) {

    override val defaultClip: Path
        get() = Path().apply {
            addRect(size.and(Offset.zero))
        }

    override fun paint(context: PaintingContext, offset: Offset) {
        if (child != null) {
            layer = context.pushClipPath(
                offset,
                size.and(Offset.zero),
                clip!!,
                { c, o -> super.paint(c, o) },
                clipBehavior,
                layer as ClipPathLayer?
            )
        } else {
            layer = null
        }
    }
}