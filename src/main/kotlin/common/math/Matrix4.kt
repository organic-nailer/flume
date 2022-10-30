package common.math


/**
 * 4x4の3次元座標変換行列
 *
 * 右手系(xは右方向、yは下方向、zは画面の奥方向)
 *
 * @param mat
 * 行列を展開したもの。右上がmat\[3\]で左下がmat\[12\]
 */
class Matrix4(
    private val mat: List<Float>
) {
    companion object {
        val identity = Matrix4(
            listOf(
                1f,0f,0f,0f,
                0f,1f,0f,0f,
                0f,0f,1f,0f,
                0f,0f,0f,1f
            )
        )
        val zero = Matrix4(listOf(
            0f,0f,0f,0f,
            0f,0f,0f,0f,
            0f,0f,0f,0f,
            0f,0f,0f,0f
        ))
    }

    init {
        assert(mat.size == 16)
    }

    fun put(data: Map<Int, Float>): Matrix4 {
        val mutable = mat.toMutableList()
        for(datum in data) {
            mutable[datum.key] = datum.value
        }
        return Matrix4(mutable)
    }

    /**
     * 平行移動する
     *
     * <pre>
     * 1 0 0 x
     * 0 1 0 y
     * 0 0 1 z
     * 0 0 0 1
     * </pre>
     */
    fun leftTranslate(x: Float, y: Float = 0f, z: Float = 0f): Matrix4 {
        val translator = identity.put(mapOf(
            3 to x, 7 to y, 11 to z
        ))
        return translator * this
    }

    operator fun times(rightHand: Matrix4): Matrix4 {
        val m00 = mat[0]
        val m01 = mat[1]
        val m02 = mat[2]
        val m03 = mat[3]
        val m10 = mat[4]
        val m11 = mat[5]
        val m12 = mat[6]
        val m13 = mat[7]
        val m20 = mat[8]
        val m21 = mat[9]
        val m22 = mat[10]
        val m23 = mat[11]
        val m30 = mat[12]
        val m31 = mat[13]
        val m32 = mat[14]
        val m33 = mat[15]
        val argStorage = rightHand.mat
        val n00 = argStorage[0]
        val n01 = argStorage[1]
        val n02 = argStorage[2]
        val n03 = argStorage[3]
        val n10 = argStorage[4]
        val n11 = argStorage[5]
        val n12 = argStorage[6]
        val n13 = argStorage[7]
        val n20 = argStorage[8]
        val n21 = argStorage[9]
        val n22 = argStorage[10]
        val n23 = argStorage[11]
        val n30 = argStorage[12]
        val n31 = argStorage[13]
        val n32 = argStorage[14]
        val n33 = argStorage[15]
        return Matrix4(listOf(
            (m00 * n00) + (m01 * n10) + (m02 * n20) + (m03 * n30),
            (m10 * n00) + (m11 * n10) + (m12 * n20) + (m13 * n30),
            (m20 * n00) + (m21 * n10) + (m22 * n20) + (m23 * n30),
            (m30 * n00) + (m31 * n10) + (m32 * n20) + (m33 * n30),
            (m00 * n01) + (m01 * n11) + (m02 * n21) + (m03 * n31),
            (m10 * n01) + (m11 * n11) + (m12 * n21) + (m13 * n31),
            (m20 * n01) + (m21 * n11) + (m22 * n21) + (m23 * n31),
            (m30 * n01) + (m31 * n11) + (m32 * n21) + (m33 * n31),
            (m00 * n02) + (m01 * n12) + (m02 * n22) + (m03 * n32),
            (m10 * n02) + (m11 * n12) + (m12 * n22) + (m13 * n32),
            (m20 * n02) + (m21 * n12) + (m22 * n22) + (m23 * n32),
            (m30 * n02) + (m31 * n12) + (m32 * n22) + (m33 * n32),
            (m00 * n03) + (m01 * n13) + (m02 * n23) + (m03 * n33),
            (m10 * n03) + (m11 * n13) + (m12 * n23) + (m13 * n33),
            (m20 * n03) + (m21 * n13) + (m22 * n23) + (m23 * n33),
            (m30 * n03) + (m31 * n13) + (m32 * n23) + (m33 * n33)
        ))
    }

    operator fun times(rightHand: Vector4): Vector4 {
        val m00 = mat[0]
        val m01 = mat[1]
        val m02 = mat[2]
        val m03 = mat[3]
        val m10 = mat[4]
        val m11 = mat[5]
        val m12 = mat[6]
        val m13 = mat[7]
        val m20 = mat[8]
        val m21 = mat[9]
        val m22 = mat[10]
        val m23 = mat[11]
        val m30 = mat[12]
        val m31 = mat[13]
        val m32 = mat[14]
        val m33 = mat[15]
        val argStorage = rightHand.vector
        val n0 = argStorage[0]
        val n1 = argStorage[1]
        val n2 = argStorage[2]
        val n3 = argStorage[3]
        return Vector4(listOf(
            m00 * n0 + m01 * n1 + m02 * n2 + m03 * n3,
            m10 * n0 + m11 * n1 + m12 * n2 + m13 * n3,
            m20 * n0 + m21 * n1 + m22 * n2 + m23 * n3,
            m30 * n0 + m31 * n1 + m32 * n2 + m33 * n3
        ))
    }
}