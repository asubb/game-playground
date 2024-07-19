package asubb.game.ecs.types

import glm_.vec3.Vec3

typealias Vector = FloatArray

inline fun vector(x: Number, y: Number, z: Number) = vector(x.toFloat(), y.toFloat(), z.toFloat())

inline fun vector(x: Float, y: Float, z: Float): Vector {
    val v = FloatArray(3)
    v[0] = x
    v[1] = y
    v[2] = z
    return v
}

inline val Vector.x get() = this[0]
inline val Vector.y get() = this[1]
inline val Vector.z get() = this[2]

fun Vector.asString() = "[x=${x}, y=${y}, z=${z}]"

fun Vector.toVec3() = Vec3(x, y, z)

inline operator fun Vector.plus(v: Vector): Vector = vector(
    this[0] + v.x,
    this[1] + v.y,
    this[2] + v.z,
)

inline operator fun Vector.minus(v: Vector): Vector = vector(
    this[0] - v.x,
    this[1] - v.y,
    this[2] - v.z,
)

inline operator fun Vector.times(d: Float): Vector = vector(
    this[0] * d,
    this[1] * d,
    this[2] * d,
)

inline operator fun Vector.div(d: Float): Vector = vector(
    this[0] / d,
    this[1] / d,
    this[2] / d,
)
