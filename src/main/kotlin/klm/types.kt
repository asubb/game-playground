package klm

typealias vec4f = FloatArray
typealias mat4x4 = FloatArray

fun vector(x: Float, y: Float, z: Float, w: Float): vec4f = floatArrayOf(x, y, z, w)

fun identityMatrix(): mat4x4 = floatArrayOf(
    1.0f, 0.0f, 0.0f, 0.0f,
    0.0f, 1.0f, 0.0f, 0.0f,
    0.0f, 0.0f, 1.0f, 0.0f,
    0.0f, 0.0f, 0.0f, 1.0f,
)

operator fun vec4f.times(matrix: mat4x4): vec4f = TODO()