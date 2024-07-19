package asubb.game.ecs.system

import asubb.game.ecs.*
import asubb.game.ecs.component.Motion
import asubb.game.ecs.component.Transform


/**
 * Physics system moves the object by altering the [asubb.game.ecs.component.Transform] component based
 * on their defined [asubb.game.ecs.component.Motion] component.
 */
class PhysicsSystem(private val world: World) : System {

    override fun init() {
    }

    override fun update(timeSpan: TimeSpan) {
        world.forEach<Motion> { entity, motion ->
            world.withEntity<Transform>(entity) { transform ->
                world.updateComponent(
                    entity,
                    transform.copy(
                        position = motion.newPosition(timeSpan, transform.position),
                        rotation = motion.newRotation(timeSpan, transform.rotation)
                    )
                )
            }
        }
    }

    override fun destroy() {
    }
}