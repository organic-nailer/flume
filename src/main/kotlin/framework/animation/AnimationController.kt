package framework.animation

import kotlin.math.abs
import kotlin.time.Duration
import kotlin.time.DurationUnit

enum class AnimationDirection {
    Forward, Reverse
}

interface AnimationListener {
    fun addListener(listener: () -> Unit)
    fun removeListener(listener: () -> Unit)
    fun clearListeners(listener: () -> Unit)
    fun notifyListeners()
}

class AnimationListenerImpl: AnimationListener {
    private val listeners = mutableListOf<() -> Unit>()

    override fun addListener(listener: () -> Unit) {
        listeners.add(listener)
    }

    override fun removeListener(listener: () -> Unit) {
        listeners.remove(listener)
    }

    override fun clearListeners(listener: () -> Unit) {
        listeners.clear()
    }

    override fun notifyListeners() {
        for(listener in listeners) {
            listener()
        }
    }
}

class AnimationController(
    initialValue: Double? = null,
    private val duration: Duration? = null,
    private val lowerBound: Double = 0.0,
    private val upperBound: Double = 1.0,
    vsync: TickerProvider
): AnimationListener by AnimationListenerImpl() {
    private fun tick(elapsed: Duration) {
        lastElapsedDuration = elapsed
        val elapsedInSeconds = elapsed.toDouble(DurationUnit.SECONDS)
        valueInternal = simulation!!.x(elapsedInSeconds).coerceIn(lowerBound, upperBound)
        if(simulation!!.isDone(elapsedInSeconds)) {
            stop()
        }
        notifyListeners()
    }

    private val ticker: WidgetTicker = vsync.createTicker {
        tick(it)
    }
    private var simulation: Simulation? = null
    var value: Double
        get() = valueInternal
        set(newValue) {
            stop()
            valueInternal = newValue
            notifyListeners()
        }
    private var valueInternal: Double = initialValue ?: lowerBound
        set(newValue) {
            field = newValue.coerceIn(lowerBound, upperBound)
        }

    private var direction: AnimationDirection = AnimationDirection.Forward
    private var lastElapsedDuration: Duration? = null

    val velocity: Double
        get() {
            if(!isAnimating) return 0.0
            return simulation!!.dx(lastElapsedDuration!!.toDouble(DurationUnit.SECONDS))
        }
    val isAnimating: Boolean
        get() = ticker.isActive

    private fun reset() {
        value = lowerBound
    }

    fun forward(from: Double? = null) {
        direction = AnimationDirection.Forward
        if(from != null) {
            value = from
        }
        animateToInternal(upperBound)
    }

    fun reverse(from: Double? = null) {
        direction = AnimationDirection.Reverse
        if(from != null) {
            value = from
        }
        animateToInternal(lowerBound)
    }

    fun animateTo(target: Double, duration: Duration? = null, curve: Curve = LinearCurve()) {
        direction = AnimationDirection.Forward
        animateToInternal(target, duration, curve)
    }

    fun animateBack(target: Double, duration: Duration? = null, curve: Curve = LinearCurve()) {
        direction = AnimationDirection.Reverse
        animateToInternal(target, duration, curve)
    }

    fun animateToInternal(target: Double, duration: Duration? = null, curve: Curve = LinearCurve()) {
        var simulationDuration = duration
        if(simulationDuration == null) {
            val range = upperBound - lowerBound
            val remainingFraction = if(range.isFinite()) abs(target - value) / range else 1.0 //TODO: directionDuration
            val directionDuration = this.duration
            simulationDuration = directionDuration!! * remainingFraction
        } else if(target == value) {
            simulationDuration = Duration.ZERO
        }
        stop()
        if(simulationDuration == Duration.ZERO) {
            if(value != target) {
                valueInternal = target.coerceIn(lowerBound, upperBound)
                notifyListeners()
            }
        }
        startSimulation(InterpolationSimulation(value, target, simulationDuration, curve))
    }

    private fun startSimulation(simulation: Simulation) {
        this.simulation = simulation
        lastElapsedDuration = Duration.ZERO
        valueInternal = simulation.x(0.0).coerceIn(lowerBound, upperBound)
        ticker.start()
    }

    fun stop() {
        simulation = null
        lastElapsedDuration = null
        ticker.stop()
    }

}