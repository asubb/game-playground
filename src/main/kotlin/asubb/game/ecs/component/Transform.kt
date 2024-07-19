package asubb.game.ecs.component

import asubb.game.ecs.*
import asubb.game.ecs.types.*
import glm_.glm
import glm_.mat4x4.Mat4
import glm_.vec3.Vec3

data class Transform(
    val position: Vector = vector(0f, 0f, 0f),
    val scale: Vector = vector(1f, 1f, 1f),
    val rotation: Vector = vector(0f, 0f, 0f),
) : Component {

    override val key get() = requireNotNull(Transform::class.simpleName)

    fun getModelMatrix(): Mat4 {
        var model = Mat4(1.0f)
        model = glm.scale(model, scale.x, scale.y, scale.z)
        model = glm.translate(model, position.x, position.y, position.z)
        model = glm.rotate(model, glm.radians(rotation.x), Vec3(1.0f, 0.0f, 0.0f));
        model = glm.rotate(model, glm.radians(rotation.y), Vec3(0.0f, 1.0f, 0.0f));
        model = glm.rotate(model, glm.radians(rotation.z), Vec3(0.0f, 0.0f, 1.0f));
        return model
    }

    override fun toString(): String {
        return "Transform(position=${position.asString()}, scale=${scale.asString()}, rotation=${rotation.asString()})"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Transform

        if (!position.contentEquals(other.position)) return false
        if (!scale.contentEquals(other.scale)) return false
        if (!rotation.contentEquals(other.rotation)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = position.contentHashCode()
        result = 31 * result + scale.contentHashCode()
        result = 31 * result + rotation.contentHashCode()
        return result
    }
}