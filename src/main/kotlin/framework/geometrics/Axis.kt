package framework.geometrics

enum class Axis {
    Horizontal {
        override fun flip() = Vertical
    },
    Vertical {
        override fun flip(): Axis = Horizontal
    };

    abstract fun flip(): Axis
}