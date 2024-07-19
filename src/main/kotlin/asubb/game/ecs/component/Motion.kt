package asubb.game.ecs.component

import asubb.game.ecs.*
import asubb.game.ecs.types.*

data class Motion(
    val direction: Vector = vector(0f, 0f, 0f),
    val rotation: Vector = vector(0f, 0f, 0f),
) : Component {

    fun newPosition(timeSpan: TimeSpan, currentPosition: Vector): Vector {
        return currentPosition + direction / 1000.0f * timeSpan.delta.toFloat()
    }

    fun newRotation(timeSpan: TimeSpan, currentRotation: Vector): Vector {
        return currentRotation + rotation / 1000.0f * timeSpan.delta.toFloat()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Motion

        if (!direction.contentEquals(other.direction)) return false
        if (!rotation.contentEquals(other.rotation)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = direction.contentHashCode()
        result = 31 * result + rotation.contentHashCode()
        return result
    }

    override fun toString(): String {
        return "Motion(direction=${direction.asString()}, rotation=${rotation.asString()})"
    }

    override val key: ComponentKey get() = requireNotNull(Motion::class.simpleName)

}