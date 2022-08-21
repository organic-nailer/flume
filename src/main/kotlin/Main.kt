import common.Offset
import common.Size
import engine.runFlume
import framework.WidgetsFlumeBinding
import framework.geometrics.Axis
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
import org.jetbrains.skia.Path
import org.jetbrains.skia.paragraph.TextStyle

fun main() {
    runFlume(appMain = { appMain() })
}

fun appMain() {
    runApp(createWidgetTree())
    WidgetsFlumeBinding.setOnKeyEventCallback {
        when(it.character) {
            "r" -> runApp(createWidgetTree(LightPhase.Red))
            "y" -> runApp(createWidgetTree(LightPhase.Yellow))
            "g" -> runApp(createWidgetTree(LightPhase.Green))
            "a" -> runApp(createWidgetTree(LightPhase.All))
        }
    }
}

enum class LightPhase {
    Red, Green, Yellow, All
}

fun createWidgetTree(phase: LightPhase = LightPhase.All): Widget {
    val darken = 0xFF9E9E9E.toInt()
    return Align(
        child = SizedBox(
            width = 300.0, height = 300.0,
            child = ColoredBox(
                color = if(phase in listOf(LightPhase.Red, LightPhase.All)) 0xFFF44336.toInt() else darken
            )
        )
    )
}
