package framework.widget.paint

import common.Clip
import framework.render.RenderObject
import framework.render.clip.CustomClipper
import framework.render.clip.RenderClipPath
import framework.widget.SingleChildRenderObjectWidget
import framework.widget.Widget
import org.jetbrains.skia.Path

class ClipPath(
    val clipper: CustomClipper<Path>? = null,
    val clipBehavior: Clip = Clip.AntiAlias,
    child: Widget? = null,
) : SingleChildRenderObjectWidget(child) {
    override fun createRenderObject(): RenderObject {
        return RenderClipPath(clipper = clipper, clipBehavior = clipBehavior)
    }
}