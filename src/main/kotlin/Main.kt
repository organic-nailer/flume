import engine.runFlume
import framework.element.BuildContext
import framework.geometrics.Axis
import framework.render.TextSpan
import framework.runApp
import framework.widget.ColoredBox
import framework.widget.Flex
import framework.widget.InheritedWidget
import framework.widget.Listener
import framework.widget.RichText
import framework.widget.SizedBox
import framework.widget.State
import framework.widget.StatefulWidget
import framework.widget.StatelessWidget
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
    runApp(HomePage())
}

class HomePage : StatefulWidget() {
    override fun createState(): State<*> = HomePageState()
}

class HomePageState : State<HomePage>() {
    private var count = 1

    override fun build(context: BuildContext): Widget {
        return Inherited(
            message = createMessage(),
            child = Flex(
                children = listOf(
                    Message(),
                    SizedBox(
                        width = 100.0, height = 100.0,
                        child = Listener(
                            child = ColoredBox(
                                child = null, color = 0xFF00FF00.toInt()
                            ),
                            onPointerUp = {
                                setState {
                                    count++
                                }
                            }
                        ),
                    )
                ),
                direction = Axis.Vertical
            )
        )
    }

    private fun createMessage(): String {
        val result = when {
            count % 15 == 0 -> "FizzBuzz"
            count % 3 == 0 -> "Fizz"
            count % 5 == 0 -> "Buzz"
            else -> count.toString()
        }
        return result
    }
}

class Message : StatelessWidget() {
    override fun build(context: BuildContext): Widget {
        return RichText(
            text = TextSpan(
                "Message: ${Inherited.of(context, listen = true).message}"
            )
        )
    }
}

class Inherited(
    val message: String, child: Widget
) : InheritedWidget(child) {
    companion object {
        fun of(context: BuildContext, listen: Boolean): Inherited {
            return if(listen) {
                context.dependOnInheritedWidgetOfExactType(Inherited::class)!!
            } else {
                context.getElementForInheritedWidgetOfExactType(Inherited::class)!!.widget as Inherited
            }
        }
    }

    override fun updateShouldNotify(oldWidget: InheritedWidget): Boolean = message != (oldWidget as Inherited).message
}