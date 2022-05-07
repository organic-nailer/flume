import common.Offset
import common.Size
import engine.runFlume
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
}

fun createWidgetTree(): Widget {
    return Align(
        child = Flex(
            mainAxisSize = MainAxisSize.Min,
            direction = Axis.Vertical,
            children = listOf(
                ClipPath(
                    clipper = ArcClipper(),
                    child = SizedBox(
                        width = 100.0, height = 100.0,
                        child = ColoredBox(color = 0xFFF44336.toInt())
                    )
                ),
                ClipRRect(
                    borderRadius = BorderRadius.circular(20.0),
                    child = SizedBox(
                        width = 100.0, height = 100.0,
                        child = ColoredBox(color = 0xFFFFEB3B.toInt())
                    )
                ),
                ClipOval(
                    child = SizedBox(
                        width = 100.0, height = 100.0,
                        child = ColoredBox(color = 0xFF4CAF50.toInt())
                    )
                ),
                RichText(
                    TextSpan(
                        "信号機だよ",
                        textStyle = TextStyle().apply {
                            color = 0xFFFF0000.toInt()
                            fontSize = 50f
                        }
                    )
                )
            )
        )
    )
}

class ArcClipper: CustomClipper<Path>() {
    override fun getClip(size: Size): Path {
        return Path().apply {
            lineTo(0f, size.height.toFloat() - 30f)

            val firstControlPoint = Offset(size.width / 4, size.height)
            val firstPoint = Offset(size.width / 2, size.height)
            quadTo(firstControlPoint.dx.toFloat(),
                firstControlPoint.dy.toFloat(),
                firstPoint.dx.toFloat(),
                firstPoint.dy.toFloat())

            val secondControlPoint = Offset(size.width - size.width / 4, size.height)
            val secondPoint = Offset(size.width, size.height - 30)
            quadTo(secondControlPoint.dx.toFloat(),
                secondControlPoint.dy.toFloat(),
                secondPoint.dx.toFloat(),
                secondPoint.dy.toFloat())
            lineTo(size.width.toFloat(), 0f)
            closePath()
        }
    }
    override fun shouldReclip(oldClipper: CustomClipper<Path>): Boolean = false
}