package asubb.game.ecs.types

typealias Angle = Float

inline fun angle(a: Number) = a.toFloat()

inline val Number.degrees get() = angle(this)