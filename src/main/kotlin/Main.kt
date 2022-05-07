import common.Offset
import common.Size
import engine.runFlume
import framework.WidgetsFlumeBinding
import framework.geometrics.Alignment
import framework.geometrics.Axis
import framework.geometrics.CrossAxisAlignment
import framework.geometrics.MainAxisSize
import framework.painting.BorderRadius
import framework.render.TextSpan
import framework.render.clip.CustomClipper
import framework.runApp
import framework.widget.Align
import framework.widget.ColoredBox
import framework.widget.Flex
import framework.widget.RichText
import framework.widget.SizedBox
import framework.widget.Widget
import framework.widget.paint.ClipOval
import framework.widget.paint.ClipPath
import framework.widget.paint.ClipRRect
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.skia.Path
import org.jetbrains.skia.paragraph.TextStyle
import org.w3c.dom.Text

fun main() {
    runFlume(appMain = { appMain() })
}

fun appMain() {
    runApp(createProgress(0.0, 640))
    GlobalScope.launch {
        for(p in 0..100) {
            delay(300)
            runApp(createProgress(p / 100.0, 640))
            WidgetsFlumeBinding.pipeline.renderView!!.needsLayout = true
            WidgetsFlumeBinding.pipeline.nodesNeedingLayout.add(WidgetsFlumeBinding.pipeline.renderView!!)
            WidgetsFlumeBinding.ensureVisualUpdate()
        }
    }
}

enum class LightPhase {
    Red, Green, Yellow, All
}

fun createProgress(progress: Double, maxWidth: Int): Widget {
    return Flex(
        direction = Axis.Vertical,
        crossAxisAlignment = CrossAxisAlignment.Start,
        mainAxisSize = MainAxisSize.Min,
        children = listOf(
            SizedBox(
                width = (maxWidth * progress).coerceIn(0.0..maxWidth.toDouble()),
                height = 50.0,
                child = ColoredBox(color = 0xFF4CAF50.toInt())
            ),
            RichText(
                text = TextSpan("${(progress * 100).toInt()}%")
            )
        )
    )
}