import engine.runFlume
import framework.element.BuildContext
import framework.runApp
import framework.widget.Align
import framework.widget.ColoredBox
import framework.widget.Listener
import framework.widget.SizedBox
import framework.widget.StatelessWidget
import framework.widget.Widget

fun main() {
    runFlume(appMain = { appMain() })
}

fun appMain() {
    runApp(MyPage())
}

class MyPage: StatelessWidget() {
    override fun build(context: BuildContext): Widget {
        return Align(
            child = SizedBox(
                width = 200.0, height = 200.0,
                child = Listener(
                    child = ColoredBox(
                        color = 0xFFFF0000.toInt()
                    ),
                    onPointerDown = {
                        println(it)
                    },
                    onPointerMove = {
                        println(it)
                    },
                    onPointerUp = {
                        println(it)
                    }
                )
            )
        )
    }
}
