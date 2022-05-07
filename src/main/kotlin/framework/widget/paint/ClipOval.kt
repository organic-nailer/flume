package framework.widget.paint

import common.Clip
import framework.render.RenderObject
import framework.render.clip.CustomClipper
import framework.render.clip.RenderClipOval
import framework.widget.SingleChildRenderObjectWidget
import framework.widget.Widget
import org.jetbrains.skia.Rect

class ClipOval(
    val clipper: CustomClipper<Rect>? = null,
    val clipBehavior: Clip = Clip.AntiAlias,
    child: Widget? = null,
) : SingleChildRenderObjectWidget(child) {
    override fun createRenderObject(): RenderObject {
        return RenderClipOval(clipper = clipper, clipBehavior = clipBehavior)
    }
}