package framework.widget.paint

import common.Clip
import framework.render.RenderObject
import framework.render.clip.CustomClipper
import framework.render.clip.RenderClipRect
import framework.widget.SingleChildRenderObjectWidget
import framework.widget.Widget
import org.jetbrains.skia.Rect

class ClipRect(
    val clipper: CustomClipper<Rect>? = null,
    val clipBehavior: Clip = Clip.AntiAlias,
    child: Widget? = null,
) : SingleChildRenderObjectWidget(child) {
    override fun createRenderObject(): RenderObject {
        return RenderClipRect(clipper = clipper, clipBehavior = clipBehavior)
    }
}