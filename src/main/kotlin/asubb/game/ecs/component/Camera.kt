package asubb.game.ecs.component

import asubb.game.ecs.*
import asubb.game.ecs.types.*
import glm_.glm
import glm_.mat4x4.Mat4
import glm_.vec3.Vec3
import kotlin.math.cos
import kotlin.math.sin

data class Camera(
    val position: Vector,
    val viewHorizontalAngle: Angle,
    val viewVerticalAngle: Angle,
    val up: Vector = vector(0, 1, 0),
    val fov: Float = 45f,
    val near: Float = 0.1f,
    val far: Float = 100f,
    val aspectRatio: Float = 800f / 600f,
) : Component {

    override val key: ComponentKey
        get() = requireNotNull(Camera::class.simpleName)

    fun getViewMatrix(): Mat4 {
        val yaw = viewHorizontalAngle.radians
        val pitch = viewVerticalAngle.radians
        val direction = Vec3(
            x = cos(yaw) * cos(pitch),
            y = sin(pitch),
            z = sin(yaw) * cos(pitch),
        )
//        println("direction: $direction")
        val cameraFront = glm.normalize(direction)
        val view = glm.lookAt(position.toVec3(), position.toVec3() + cameraFront, up.toVec3());
        return view
    }

    fun getProjectionMatrix(): Mat4 {
        return glm.perspective(glm.radians(fov), aspectRatio, near, far);
    }


}