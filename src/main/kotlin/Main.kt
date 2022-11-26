import engine.runFlume
import framework.animation.AnimationController
import framework.animation.TickerProvider
import framework.animation.TickerProviderImpl
import framework.element.BuildContext
import framework.geometrics.Axis
import framework.geometrics.MainAxisSize
import framework.render.TextSpan
import framework.runApp
import framework.widget.Align
import framework.widget.ColoredBox
import framework.widget.FadeTransition
import framework.widget.Flex
import framework.widget.Listener
import framework.widget.RichText
import framework.widget.SizedBox
import framework.widget.State
import framework.widget.StatefulWidget
import framework.widget.StatelessWidget
import framework.widget.Widget
import org.jetbrains.skia.paragraph.TextStyle
import kotlin.time.Duration.Companion.milliseconds

fun main() {
    runFlume(appMain = { appMain() })
}

fun appMain() {
    runApp(MyPage())
}

class MyPage: StatelessWidget() {
    override fun build(context: BuildContext): Widget {
        return TicTakToeView()
    }
}

class TicTakToeView: StatefulWidget() {
    override fun createState(): State<*> = TicTakToeState()
}

enum class Player {
    X, O
}

class TicTakToeState: State<TicTakToeView>() {
    private val lines = listOf(
        listOf(listOf(0, 0), listOf(0, 1), listOf(0, 2)), // 横向き
        listOf(listOf(1, 0), listOf(1, 1), listOf(1, 2)),
        listOf(listOf(2, 0), listOf(2, 1), listOf(2, 2)),
        listOf(listOf(0, 0), listOf(1, 0), listOf(2, 0)), // 縦向き
        listOf(listOf(0, 1), listOf(1, 1), listOf(2, 1)),
        listOf(listOf(0, 2), listOf(1, 2), listOf(2, 2)),
        listOf(listOf(0, 0), listOf(1, 1), listOf(2, 2)), // 斜め
        listOf(listOf(0, 2), listOf(1, 1), listOf(2, 0)),
    )

    private val field: List<MutableList<CellView.CellState>> = listOf(
        mutableListOf(CellView.CellState.Empty, CellView.CellState.Empty, CellView.CellState.Empty),
        mutableListOf(CellView.CellState.Empty, CellView.CellState.Empty, CellView.CellState.Empty),
        mutableListOf(CellView.CellState.Empty, CellView.CellState.Empty, CellView.CellState.Empty),
    )
    private var currentPlayer: Player = Player.X
    private var winner: Player? = null
    private var finished: Boolean = false

    private fun resetGame() {
        field[0].clear()
        field[1].clear()
        field[2].clear()
        field[0].addAll(listOf(CellView.CellState.Empty, CellView.CellState.Empty, CellView.CellState.Empty))
        field[1].addAll(listOf(CellView.CellState.Empty, CellView.CellState.Empty, CellView.CellState.Empty))
        field[2].addAll(listOf(CellView.CellState.Empty, CellView.CellState.Empty, CellView.CellState.Empty))
        currentPlayer = Player.X
        winner = null
        finished = false
    }

    private fun onFieldClicked(row: Int, column: Int) {
        updateField(currentPlayer, row, column)
    }

    private fun updateField(player: Player, row: Int, column: Int) {
        if (finished) return
        if (field[row][column] != CellView.CellState.Empty) return
        setState {
            when(player) {
                Player.X -> {
                    field[row][column] = CellView.CellState.Xs
                    currentPlayer = Player.O
                }
                Player.O -> {
                    field[row][column] = CellView.CellState.Os
                    currentPlayer = Player.X
                }
            }
            checkIfGameFinished()
        }
    }

    private fun checkIfGameFinished() {
        // 揃っている直線がないか探索する
        for (line in lines) {
            val first = field[line[0][0]][line[0][1]]
            val second = field[line[1][0]][line[1][1]]
            val third = field[line[2][0]][line[2][1]]
            if (first != CellView.CellState.Empty && first == second && second == third) {
                finished = true
                winner = if (first == CellView.CellState.Os) Player.O else Player.X
                return
            }
        }

        // マスが全て埋まっているかどうか確認する
        var isFilled = true
        for (row in 0..2) {
            for (column in 0..2) {
                if (field[row][column] == CellView.CellState.Empty) {
                    isFilled = false
                }
            }
        }
        if (isFilled) {
            finished = true
            winner = null
        }
    }

    override fun build(context: BuildContext): Widget {
        val message = if(finished) {
            if(winner != null) "${winner!!.name}の勝利" else "引き分け"
        } else {
            "${currentPlayer.name}の手番"
        }
        return Align(
            child = Flex(
                direction = Axis.Vertical,
                mainAxisSize = MainAxisSize.Min,
                children = listOf(
                    RichText(TextSpan(message)),
                    *field.mapIndexed { rowIndex, row ->
                        return@mapIndexed Flex(
                            direction = Axis.Horizontal,
                            mainAxisSize = MainAxisSize.Min,
                            children = row.mapIndexed { columnIndex, cell ->
                                val color = if((rowIndex * 3 + columnIndex) % 2 == 0) 0xFF81D4FA.toInt() else 0xFFB3E5FC.toInt()
                                CellView(cell, color) { onFieldClicked(rowIndex, columnIndex) }
                            }
                        )
                    }.toTypedArray(),
                    SizedBox(
                        width = 300.0, height = 148.0,
                        child = Align(
                            child = Listener(
                                child = SizedBox(
                                    height = 100.0,
                                    child = RichText(
                                        text = TextSpan("もう一度遊ぶ", textStyle = TextStyle().apply { fontSize = 20f; color = 0xFF000000.toInt() }),
                                    )
                                ),
                                onPointerUp = {
                                    setState {
                                        resetGame()
                                    }
                                }
                            )
                        )
                    )
                )
            )
        )
    }
}

class CellView(
    val state: CellState,
    val color: Int,
    val onClicked: () -> Unit,
): StatefulWidget() {
    override fun createState(): State<*> = CellViewState()

    enum class CellState {
        Xs, Os, Empty
    }
}

class CellViewState: State<CellView>(), TickerProvider by TickerProviderImpl() {
    private val controller = AnimationController(
        vsync = this,
        initialValue = 0.0,
        duration = 500.milliseconds
    )

    override fun didUpdateWidget(oldWidget: CellView) {
        super.didUpdateWidget(oldWidget)
        if (oldWidget.state != widget.state) {
            if (widget.state == CellView.CellState.Empty) {
                controller.animateTo(0.0)
            }
            else {
                controller.animateTo(1.0)
            }
        }
    }

    override fun build(context: BuildContext): Widget {
        return Listener(
            child = ColoredBox(
                color = widget.color,
                child = SizedBox(
                    width = 100.0, height = 100.0,
                    child = Align(
                        child = FadeTransition(
                            opacity = controller,
                            child = RichText(text = TextSpan(
                                when(widget.state) {
                                    CellView.CellState.Xs -> "X"
                                    CellView.CellState.Os -> "O"
                                    CellView.CellState.Empty -> ""
                                }
                            ))
                        )
                    )
                ),
            ),
            onPointerUp = {
                widget.onClicked()
            }
        )
    }
}