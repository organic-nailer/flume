import engine.runFlume
import framework.element.BuildContext
import framework.geometrics.Alignment
import framework.runApp
import framework.widget.Align
import framework.widget.ColoredBox
import framework.widget.Listener
import framework.widget.SizedBox
import framework.widget.State
import framework.widget.StatefulWidget
import framework.widget.Widget

const val windowWidth = 640
const val windowHeight = 480

fun main() {
    runFlume(
        appMain = { appMain() },
        windowWidth = windowWidth,
        windowHeight = windowHeight
    )
}

fun appMain() {
    runApp(DrawView())
}

class DrawView: StatefulWidget() {
    override fun createState(): State<*> = DrawViewState()
}

class DrawViewState: State<DrawView>() {
    var pointerX = 0.0
    var pointerY = 0.0
    var color = 0x00000000
    override fun build(context: BuildContext): Widget {
        return Listener(
            child = ColoredBox(
                color = 0xFFDEDEDE.toInt(),
                child = SizedBox(
                    width = windowWidth.toDouble(),
                    height = windowHeight.toDouble(),
                    child = Align(
                        alignment = Alignment(pointerX,pointerY),
                        child = SizedBox(
                            width = 20.0, height = 20.0,
                            child = ColoredBox(
                                color = color
                            )
                        )
                    ),
                ),
            ),
            onPointerDown = {
                setState {
                    pointerX = (it.x / windowWidth) * 2 - 1
                    pointerY = (it.y / windowHeight) * 2 - 1
                    color = 0xFFFF0000.toInt()
                }
            },
            onPointerMove = {
                setState {
                    pointerX = (it.x / windowWidth) * 2 - 1
                    pointerY = (it.y / windowHeight) * 2 - 1
                    color = 0xFF00FF00.toInt()
                }
            },
            onPointerUp = {
                setState {
                    pointerX = (it.x / windowWidth) * 2 - 1
                    pointerY = (it.y / windowHeight) * 2 - 1
                    color = 0xFF0000FF.toInt()
                }
            }
        )
    }
}
