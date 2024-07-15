package asubb.game.ecs

import asubb.game.TextureIO
import com.danielgergely.kgl.Kgl
import glm_.glm
import glm_.mat4x4.Mat4
import glm_.vec3.Vec3

typealias ComponentKey = String

interface Component {
    val key: ComponentKey
}

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

interface Render : Component {
    override val key get() = requireNotNull(Render::class.simpleName)
    val initialized: Boolean
    fun init(gl: Kgl, textureIO: TextureIO)
    fun draw(gl: Kgl, model: Mat4, view: Mat4, projection: Mat4)
    fun end(gl: Kgl)
}
