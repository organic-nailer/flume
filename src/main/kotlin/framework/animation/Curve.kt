package framework.animation


interface Curve {
    fun transform(t: Double): Double
}

class LinearCurve: Curve {
    override fun transform(t: Double): Double {
        return t
    }
}