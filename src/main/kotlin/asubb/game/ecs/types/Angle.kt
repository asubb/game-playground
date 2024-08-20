package asubb.game.ecs.types

import kotlin.math.PI

/**
 * Angle measurement in degrees.
 */
@JvmInline
value class Angle(val degrees: Float) : Comparable<Angle> {

    fun normalize(): Angle {
        var a = degrees
        if (a >= 0 && a < 360) {
            return this
        }
        while (a < 0) {
            a += 360
        }
        while (a >= 360) {
            a -= 360
        }
        return Angle(a)
    }

    override fun compareTo(other: Angle): Int {
        val a = normalize().degrees
        val b = other.normalize().degrees
        return when {
            a > b -> 1
            b > a -> -1
            else -> 0
        }
    }
}

operator fun Angle.plus(a: Angle) = Angle(this.degrees + a.degrees)
operator fun Angle.minus(a: Angle) = Angle(this.degrees - a.degrees)
operator fun Angle.unaryMinus() = Angle(-degrees)

/**
 * Create an [Angle] from the [Number] with values as degrees.
 */
inline fun angle(a: Number) = Angle(a.toFloat())

/**
 * Create and [Angle] from the [Number] which is radians value.
 */
inline fun Number.fromRadians() = Angle(this.toFloat() * 180f / PI.toFloat())

/**
 * Converts [Number] to [Angle] assuming the value in degrees
 */
inline val Number.degrees get() = angle(this)

/**
 * Converts [Angle] value to radians
 */
inline val Angle.radians get(): Float = this.degrees * PI.toFloat() / 180f

