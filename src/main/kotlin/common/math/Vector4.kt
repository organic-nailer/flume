package common.math

class Vector4(
    val vector: List<Float>
) {
    companion object {
        val zero = Vector4(listOf(0f,0f,0f,0f))
    }

    init {
        assert(vector.size == 4)
    }
}