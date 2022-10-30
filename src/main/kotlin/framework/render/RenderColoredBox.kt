package framework.render

import common.Offset
import framework.PaintingContext
import framework.gesture.HitTestEntry
import framework.gesture.HitTestResult
import org.jetbrains.skia.Paint

class RenderColoredBox(color: Int) : RenderProxyBox() {
    var color: Int by MarkPaintProperty(color)
    override fun paint(context: PaintingContext, offset: Offset) {
        if (size.width != 0.0 && size.height != 0.0) {
            context.canvas.drawRect(size.and(offset), Paint().also { it.color = color })
        }
    }

    // RenderProxyBoxWithHitTestBehavior
    override fun hitTest(result: HitTestResult, position: Offset): Boolean {
        var hitTarget = false
        if(size.contains(position)) {
            hitTarget = hitTestChildren(result, position) || hitTestSelf(position)
            if(hitTarget) {
                result.add(HitTestEntry(this))
            }
        }
        return hitTarget
    }

    override fun hitTestSelf(position: Offset): Boolean = true
}