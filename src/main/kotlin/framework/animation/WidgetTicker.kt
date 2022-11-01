package framework.animation

import framework.WidgetsFlumeBinding
import kotlin.time.Duration

typealias TickerCallback = (Duration) -> Unit

class WidgetTicker(
    private val onTick: TickerCallback,
    private val creator: TickerProvider
) {
    private var animationId: Int? = null
    private val scheduled
        get() = animationId != null
    var isActive: Boolean = false
    var muted: Boolean = false
        set(value) {
            if(field == value) return
            field = value
            if (value) unscheduleTick()
            else if(shouldScheduleTick) scheduleTick()
        }
    private val shouldScheduleTick: Boolean
        get() = !muted && isActive && !scheduled
    private var startTime: Duration? = null

    fun dispose() {
        creator.removeTicker(this)
        isActive = false
        unscheduleTick()
    }

    fun start() {
        isActive = true
        if(shouldScheduleTick) {
            scheduleTick()
        }
    }

    fun stop() {
        if(!isActive) return
        isActive = false
        startTime = null
        unscheduleTick()
    }

    private fun tick(timeStamp: Duration) {
        animationId = null
        if(startTime == null) {
            startTime = timeStamp
        }
        onTick(timeStamp - startTime!!)

        if(shouldScheduleTick) {
            scheduleTick()
        }
    }

    private fun scheduleTick() {
        animationId = WidgetsFlumeBinding.scheduleFrameCallback { tick(it) }
    }

    private fun unscheduleTick() {
        if(scheduled) {
            WidgetsFlumeBinding.cancelFrameCallback(animationId!!)
            animationId = null
        }
    }
}

/**
 * Tickerを供給する。
 *
 * Stateが実装するのが一般的
 */
interface TickerProvider {
    fun createTicker(onTick: TickerCallback): WidgetTicker

    fun removeTicker(ticker: WidgetTicker)
}

// Flutterではサブツリー全体のTickerのon/off制御のためにTickerModeというのが存在していて、それをlistenする
// tickerModeNotifierを持つが、簡略化のため省略する
class TickerProviderImpl: TickerProvider {
    private val tickers = mutableSetOf<WidgetTicker>()

    override fun createTicker(onTick: TickerCallback): WidgetTicker {
        tickers.clear()
        val result = WidgetTicker(onTick, this).apply {
            muted = false
        }
        tickers.add(result)
        return result
    }

    override fun removeTicker(ticker: WidgetTicker) {
        tickers.remove(ticker)
    }
}