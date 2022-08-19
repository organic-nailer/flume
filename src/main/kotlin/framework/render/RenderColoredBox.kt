package framework.render

import common.Offset
import framework.PaintingContext
import org.jetbrains.skia.Paint

class RenderColoredBox(color: Int) : RenderProxyBox() {
    var color: Int by MarkPaintProperty(color)
    override fun paint(context: PaintingContext, offset: Offset) {
        if (size.width != 0.0 && size.height != 0.0) {
            context.canvas.drawRect(size.and(offset), Paint().also { it.color = color })
        }
    }
}