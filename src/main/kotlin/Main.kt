import engine.runFlume
import framework.animation.AnimationController
import framework.animation.TickerProvider
import framework.animation.TickerProviderImpl
import framework.element.BuildContext
import framework.runApp
import framework.widget.ColoredBox
import framework.widget.FadeTransition
import framework.widget.Listener
import framework.widget.SizedBox
import framework.widget.State
import framework.widget.StatefulWidget
import framework.widget.Widget
import kotlin.time.Duration.Companion.seconds

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
    runApp(MyStatefulWidget())
}

class MyStatefulWidget: StatefulWidget() {
    override fun createState(): State<*> = MyStatefulWidgetState()
}

class MyStatefulWidgetState: State<MyStatefulWidget>(), TickerProvider by TickerProviderImpl() {
    private val animationController = AnimationController(
        initialValue = 1.0,
        duration = 1.seconds,
        vsync = this
    )
    private var isForward = false

    private fun animate() {
        if(isForward) {
            animationController.forward(0.0)
            isForward = false
        }
        else {
            animationController.reverse(1.0)
            isForward = true
        }
    }

    override fun build(context: BuildContext): Widget {
        return SizedBox(
            child = Listener(
                child = ColoredBox(
                    color = 0xFF000000.toInt(),
                    child = FadeTransition(
                        opacity = animationController,
                        child = ColoredBox(
                            child = null,
                            color = 0xFF00FF00.toInt()
                        ),
                    )
                ),
                onPointerUp = {
                    animate()
                },
            ),
            width = 100.0,
            height = 100.0
        )
    }
}
