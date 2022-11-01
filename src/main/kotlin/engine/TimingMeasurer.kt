package engine

import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class TimingMeasurer {
    private val initTime = System.nanoTime()

    /**
     * インスタンス生成時から経過した時間を返す
     */
    fun getElapsedTime(): Duration {
        return (System.nanoTime() - initTime).toDuration(DurationUnit.NANOSECONDS)
    }
}