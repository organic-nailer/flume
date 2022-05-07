package framework.widget.paint

import common.Clip
import framework.painting.BorderRadius
import framework.render.RenderObject
import framework.render.clip.CustomClipper
import framework.render.clip.RenderClipRRect
import framework.widget.SingleChildRenderObjectWidget
import framework.widget.Widget
import org.jetbrains.skia.RRect

class ClipRRect(
    val borderRadius: BorderRadius = BorderRadius.zero,
    val clipper: CustomClipper<RRect>? = null,
    val clipBehavior: Clip = Clip.AntiAlias,
    child: Widget? = null,
) : SingleChildRenderObjectWidget(child) {
    override fun createRenderObject(): RenderObject {
        return RenderClipRRect(borderRadius = borderRadius,
            clipper = clipper,
            clipBehavior = clipBehavior)
    }
}