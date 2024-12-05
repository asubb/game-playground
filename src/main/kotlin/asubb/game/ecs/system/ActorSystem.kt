package asubb.game.ecs.system

import asubb.game.ecs.*
import asubb.game.ecs.component.Camera
import asubb.game.ecs.component.Motion
import asubb.game.ecs.component.Transform
import asubb.game.ecs.system.ActorMoveDirection.*
import asubb.game.ecs.types.*

enum class ActorMoveDirection { Forward, Backward, Left, Right }
data class ActorMoveStart(val direction: ActorMoveDirection) : Action
data class ActorMoveEnd(val direction: ActorMoveDirection) : Action
data class ActorViewMove(val dViewHoriz: Float, val dViewVert: Float) : Action

/**
 * System to define the behavior of the main Actor of the game.
 *
 * Moves [Camera] around as by external events from keyboard and mouse.
 * An actor is physical object hence defined with [Transform] and [Motion] components
 */
class ActorSystem(
    private val world: World,
    private val speed: Float = 1f,
    private val vertSensitivity: Float = 0.3f,
    private val horizSensitivity: Float = 0.5f,
) : ActionableSystem {

    private var actor = UndefinedEntity

    override fun init() {
        actor = world.createEntity {
            addComponents(Transform())
        }
    }

    override fun update(timeSpan: TimeSpan) {
        world.withEntity<Transform>(actor) { transform ->
            world.map<Camera>(actor) { _ ->
                Camera(
                    position = transform.position,
                    // camera is looking in the opposite direction to the object rotation
                    viewHorizontalAngle = -transform.rotation.y.fromRadians(),
                    viewVerticalAngle = transform.rotation.x.fromRadians(),
                )
            }
        }
    }

    override fun destroy() {
    }

    override fun newAction(action: Action) {
        when (action) {
            is ActorMoveStart -> move(action.direction, speed)
            is ActorMoveEnd -> move(action.direction, 0f)
            is ActorViewMove -> moveCamera(action)
        }
    }

    private val maxCameraV = 89
    private val minCameraV = -89
    private fun moveCamera(action: ActorViewMove) {
        world.update<Transform>(actor) { transform ->
            val newH = (transform.rotation.y.fromRadians() - (action.dViewHoriz * horizSensitivity).degrees).normalize()
            var newV = ((transform.rotation.x).fromRadians()+ (action.dViewVert * vertSensitivity).degrees)
            if (newV.degrees > maxCameraV) newV = maxCameraV.degrees
            else if (newV.degrees < minCameraV) newV = minCameraV.degrees
            transform.copy(
                rotation = transform.rotation.copy(x = newV.radians, y = newH.radians)
            )
        }
    }

    private fun move(direction: ActorMoveDirection, speed: Float) {
        world.withEntity<Transform>(actor) { transform ->
            world.map<Motion>(actor) { motionOrNull ->
                val motion = motionOrNull ?: Motion()

                val horizontalAngle = transform.rotation.y.fromRadians()
                val dx = when (direction) {
                    Forward -> 1
                    Backward -> -1
                    else -> 0
                }
                val dz = when (direction) {
                    Left -> -1
                    Right -> 1
                    else -> 0
                }
                val newDirection = vector(dx * speed, motion.direction.y, dz * speed)
                    .rotateY(horizontalAngle)
                    .normalize()

                motion.copy(direction = newDirection)
                    .takeIf { it.hasMotion() }
            }
        }
    }

}

