import common.KeyEvent
import common.Offset
import common.Size
import engine.runFlume
import framework.WidgetsFlumeBinding
import framework.element.BuildContext
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
import framework.widget.State
import framework.widget.StatefulWidget
import framework.widget.StatelessWidget
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
    val propagator = Propagator<KeyEvent>()
    runApp(MyPage(propagator))
    WidgetsFlumeBinding.setOnKeyEventCallback {
        propagator.fire(it)
    }
}

class Propagator<T> {
    var listener: ((T) -> Unit)? = null

    fun fire(data: T) {
        listener?.invoke(data)
    }
}

class MyPage(private val propagator: Propagator<KeyEvent>): StatelessWidget() {
    override fun build(context: BuildContext): Widget {
        return TicTakToeView(propagator)
    }
}

class TicTakToeView(val propagator: Propagator<KeyEvent>): StatefulWidget() {
    override fun createState(): State<*> = TicTakToeState()
}

enum class Player {
    X, O
}

class TicTakToeState: State<TicTakToeView>() {
    val lines = listOf(
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

    override fun didChangeDependencies() {
        super.didChangeDependencies()
        widget.propagator.listener = {
            if(it.phase == KeyEvent.KeyEventPhase.KeyUp) {
                var row: Int? = null
                var column: Int? = null
                when(it.character) {
                    "q" -> { row = 0; column = 0 }
                    "w" -> { row = 0; column = 1 }
                    "e" -> { row = 0; column = 2 }
                    "a" -> { row = 1; column = 0 }
                    "s" -> { row = 1; column = 1 }
                    "d" -> { row = 1; column = 2 }
                    "z" -> { row = 2; column = 0 }
                    "x" -> { row = 2; column = 1 }
                    "c" -> { row = 2; column = 2 }
                }
                if (row != null && column != null) {
                    updateField(currentPlayer, row, column)
                }
            }
        }
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
        var message = if(finished) {
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
                    *field.map { row ->
                        return@map Flex(
                            direction = Axis.Horizontal,
                            mainAxisSize = MainAxisSize.Min,
                            children = row.map { cell ->
                                CellView(cell)
                            }
                        )
                    }.toTypedArray()
                )
            )
        )
    }
}

class CellView(private val state: CellState): StatelessWidget() {
    override fun build(context: BuildContext): Widget {
        return SizedBox(
            width = 100.0, height = 100.0,
            child = Align(
                child = RichText(text = TextSpan(
                    when(state) {
                        CellState.Xs -> "X"
                        CellState.Os -> "O"
                        CellState.Empty -> "-"
                    }
                ))
            )
        )
    }

    enum class CellState {
        Xs, Os, Empty
    }
}
