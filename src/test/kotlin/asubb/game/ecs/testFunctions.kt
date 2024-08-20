package asubb.game.ecs

import assertk.Assert
import assertk.all
import assertk.assertThat
import assertk.assertions.isCloseTo
import assertk.assertions.prop
import assertk.assertions.support.fail
import asubb.game.ecs.component.Camera
import asubb.game.ecs.component.Motion
import asubb.game.ecs.component.Transform
import asubb.game.ecs.types.*

private const val precision = 1.0E-4f
fun Assert<Vector>.isVectorWith(x: Number? = null, y: Number? = null, z: Number? = null) {
    all {
        x?.let { prop(Vector::x).isCloseTo(it.toFloat(), precision) }
        y?.let { prop(Vector::y).isCloseTo(it.toFloat(), precision) }
        z?.let { prop(Vector::z).isCloseTo(it.toFloat(), precision) }
    }
}

fun Assert<Vector>.isVector(v: Vector) {
    all {
        prop(Vector::x).isCloseTo(v.x, precision)
        prop(Vector::y).isCloseTo(v.y, precision)
        prop(Vector::z).isCloseTo(v.z, precision)
    }
}

fun World.assert(body: Assert<World>.() -> Unit) = assertThat(this, "world").all(body)
inline fun <reified T : Component> Assert<World>.get() = prop("get<${T::class.simpleName}>()") { it.get<T>() }
inline fun <reified T : Component> Assert<World>.get(entity: Entity) =
    prop("get<${T::class.simpleName}>(entity=$entity)") { it.get<T>(entity) }

inline fun <reified T : Component> Assert<Pair<Entity, T>>.component() = prop("component<${T::class.simpleName}>") { it.second }
fun <T : Component> Assert<Pair<Entity, T>>.entity() = prop("entity") { it.first }

fun <T : Any> Assert<T>.takeValue(): T {
    lateinit var v: T
    transform { v = it }
    return v
}

fun Assert<Angle>.isAngle(expected: Angle) = given {
    if ((it.normalize() - expected.normalize()).normalize().degrees < precision) return@given
    fail(expected, it)
}

@JvmName("cameraPosition")
fun Assert<Camera>.position() = prop(Camera::position)
fun Assert<Camera>.viewHorizontalAngle() = prop(Camera::viewHorizontalAngle)
fun Assert<Camera>.viewVerticalAngle() = prop(Camera::viewVerticalAngle)

@JvmName("transformPosition")
fun Assert<Transform>.position() = prop(Transform::position)

@JvmName("transformScale")
fun Assert<Transform>.scale() = prop(Transform::scale)

@JvmName("transformRotation")
fun Assert<Transform>.rotation() = prop(Transform::rotation)

@JvmName("motionDirection")
fun Assert<Motion>.direction() = prop(Motion::direction)

@JvmName("motionRotation")
fun Assert<Motion>.rotation() = prop(Motion::rotation)

