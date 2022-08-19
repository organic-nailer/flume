package framework.widget.paint

import common.Clip
import framework.render.clip.CustomClipper
import framework.render.clip.RenderClipOval
import framework.widget.SingleChildRenderObjectWidget
import framework.widget.Widget
import org.jetbrains.skia.Rect

class ClipOval(
    val clipper: CustomClipper<Rect>? = null,
    val clipBehavior: Clip = Clip.AntiAlias,
    child: Widget? = null,
) : SingleChildRenderObjectWidget<RenderClipOval>(child) {
    override fun createRenderObject(): RenderClipOval {
        return RenderClipOval(clipper = clipper, clipBehavior = clipBehavior)
    }
}