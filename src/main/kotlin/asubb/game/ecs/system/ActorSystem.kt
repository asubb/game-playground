package asubb.game.ecs.system

import asubb.game.ecs.*
import asubb.game.ecs.component.Camera
import asubb.game.ecs.component.Motion
import asubb.game.ecs.component.Transform
import asubb.game.ecs.types.degrees
import asubb.game.ecs.types.vector
import asubb.game.ecs.types.x
import asubb.game.ecs.types.y
import kotlin.math.cos
import kotlin.math.sin

/**
 * System to define the behavior of the main Actor of the game.
 *
 * Moves [Camera] around as by external events from keyboard and mouse.
 * An actor is physical object hence defined with [Transform] and [Motion] components
 */
class ActorSystem(private val world: World) : System {

    private var actor = UndefinedEntity

    override fun init() {
        actor = world.createEntity {
            val position = vector(0, 0, 10)
            addComponents(
                Camera( // that is a derivative component
                    position = position,
                    viewDirectionH = 0.degrees,
                    viewDirectionV = 0.degrees
                ),
                Transform(
                    position = vector(0, 0, 0),
                    rotation = vector(270, 0, 0),
                    scale = vector(0, 0, 0)
                ),
                Motion(
                    direction = vector(0, 0, 0),
                    rotation = vector(0, 10, 0),
                )
            )
        }
    }

    override fun update(timeSpan: TimeSpan) {
        val camera = requireNotNull(world.get<Camera>(actor))
        val transform = requireNotNull(world.get<Transform>(actor))
        val motion = requireNotNull(world.get<Motion>(actor))
//        val x = timeSpan.globalTime.toDouble() / 1000.0
        world.updateComponent(
            actor,
            camera.copy(
                position = transform.position,
                viewDirectionV = transform.rotation.y,
                viewDirectionH = transform.rotation.x
            )
//            camera.copy(
//                position = vector(sin(x) * 2, 0, 10 + cos(x) * 2),
//                viewDirectionV = 0.degrees + 10 * sin(x).toFloat(),
//                viewDirectionH = 270.degrees + 10 * cos(x).toFloat(),
//            )
        )
    }

    override fun destroy() {
    }
}