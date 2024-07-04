package klm

class Matrix4x4(private val array: FloatArray) {
    companion object {
        fun identity() = Matrix4x4(
            floatArrayOf(
                1.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 1.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f,
            )
        )
    }

    operator fun set(i: Int, j: Int, value: Float) {
        array[i * 4 + j] = value
    }

    operator fun get(i: Int, j: Int) = array[j * 4 + i]

    fun copy() = Matrix4x4(array.copyOf())

    override fun toString(): String {
        val values = array.map { it.toString() }
        val colWidths = IntArray(4)
        repeat(4) { i ->
            repeat(4) { j ->
                val v = values[j * 4 + i]
                if (v.length > colWidths[j]) {
                    colWidths[j] = v.length
                }
            }
        }
        return values.windowed(4, 4).joinToString("\n") {
            val sb = StringBuilder();
            sb.append("|")
            it.forEachIndexed { j, value ->
                sb.append(value.padStart(colWidths[j] + 1, ' '))
                if (j < 3) sb.append(',')
            }
            sb.append(" |")
            sb.toString()
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Matrix4x4

        return array.contentEquals(other.array)
    }

    override fun hashCode(): Int {
        return array.contentHashCode()
    }

}

typealias vec4f = FloatArray

fun vec4f(x: Float, y: Float, z: Float, w: Float): vec4f = floatArrayOf(x, y, z, w)

typealias vec3f = FloatArray

fun vec3f(x: Float, y: Float, z: Float): vec3f = floatArrayOf(x, y, z)
val vec3f.x get() = this[0]
val vec3f.y get() = this[1]
val vec3f.z get() = this[2]


operator fun Matrix4x4.times(vec: vec4f): Matrix4x4 {
    TODO()
}

/**
 *  Builds a translation 4 * 4 matrix created from a vector of 3 components.
 *
 *  @param m Input matrix multiplied by this translation matrix.
 *  @param vX X Coordinate of a translation vector.
 *  @param vY Y Coordinate of a translation vector.
 *  @param vZ Z Coordinate of a translation vector.
 *
 *  val m = glm.translate(Mat4(1f), Vec3(1f))
 *
 *  where m is
 *
 *      1  0  0  0
 *      0  1  0  0
 *      0  0  1  0
 *      1  1  1  1
 *
 *  @see gtc_matrix_transform
 *  @see - translate(m: Mat4, x: Float, y: Float, z: Float)
 *  @see - translate(v: Vec3)
 *  @see <a href="https://www.khronos.org/registry/OpenGL-Refpages/gl2.1/xhtml/glTranslate.xml">glTranslate man page</a>
 */
fun translate(m: Matrix4x4, v: vec3f): Matrix4x4 {
    val res = m.copy()
    val x = m[0, 0] * v.x + m[1, 0] * v.y + m[2, 0] * v.z + m[3, 0]
    val y = m[0, 1] * v.x + m[1, 1] * v.y + m[2, 1] * v.z + m[3, 1]
    val z = m[0, 2] * v.x + m[1, 2] * v.y + m[2, 2] * v.z + m[3, 2]
    val w = m[0, 3] * v.x + m[1, 3] * v.y + m[2, 3] * v.z + m[3, 3]
    res[3, 0] = x
    res[3, 1] = y
    res[3, 2] = z
    res[3, 3] = w
    return res
}