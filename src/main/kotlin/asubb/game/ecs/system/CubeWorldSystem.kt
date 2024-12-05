package asubb.game.ecs.system

import asubb.game.ecs.*
import asubb.game.ecs.component.Motion
import asubb.game.ecs.component.Transform
import asubb.game.ecs.component.render.CubeRender
import asubb.game.ecs.types.Vector
import asubb.game.ecs.types.vector

/**
 * System that rules the World! Creates, moves, rotates and destroys cubes.
 */
class CubeWorldSystem(
    private val world: World,
    private val cubesPosSize: List<Pair<Vector, Vector>> = listOf(
        vector(0.0f, 0.0f, 0.0f) to vector(1f, 1f, 1f),
        vector(2.0f, 5.0f, -15.0f) to vector(2f, 2f, 2f),
        vector(-1.5f, -2.2f, -2.5f) to vector(3f, 3f, 3f),
        vector(-3.8f, -2.0f, -12.3f) to vector(4f, 4f, 4f),
        vector(2.4f, -0.4f, -3.5f) to vector(1f, 1f, 1f),
        vector(-1.7f, 3.0f, -7.5f) to vector(2f, 2f, 2f),
        vector(1.3f, -2.0f, -2.5f) to vector(3f, 3f, 3f),
        vector(1.5f, 2.0f, -2.5f) to vector(4f, 4f, 4f),
        vector(1.5f, 0.2f, -1.5f) to vector(.5f, .5f, .5f),
        vector(-1.3f, 1.0f, -1.5f) to vector(1f, 2f, .2f),
    ),
    private val random: GameRandom = GameRandom.default
) : System {

    private val cubes = mutableListOf<Entity>()

    override fun init() {
        cubesPosSize.forEach { (pos, scale) ->
            addCube(pos, scale)
        }
    }

    val removeCubeProbability = 0.01
    override fun update(timeSpan: TimeSpan) {
        if (cubes.size < 20) {
            random.withProbability(0.05) {
                addCube(
                    position = random.nextVector(-9.0..9.0),
                    scale = random.nextVector(0.0..3.0),
                )
            }
        }
        var activeCubes = cubes.subList(1, cubes.size)
        if (activeCubes.isNotEmpty()) {
            if (cubes.size > 5) {
                random.withProbability(removeCubeProbability) {
                    random.nextElement(activeCubes)
                        .let { (idx, _) -> removeCube(idx) }
                    // list got changed hence update the view
                    activeCubes = cubes.subList(1, cubes.size)
                }
            }
            random.withProbability(0.5) {
                val (_, cube) = random.nextElement(activeCubes)
                world.withEntity<Motion>(cube) { motion ->
                    val newVector = random.nextVector(-1.0..1.0)
                    random.withProbability(0.3) {
                        world.updateComponent(cube, motion.copy(rotation = newVector))
                    }
                }
            }
        }
    }

    override fun destroy() {
    }

    private fun removeCube(at: Int) {
        val cube = cubes.removeAt(at)
        world.removeEntity(cube)
    }

    private fun addCube(position: Vector, scale: Vector) {
        val entity = world.createEntity {
            addComponent(CubeRender())
            addComponent(Transform(position = position, scale = scale))
            addComponent(Motion(rotation = vector(10f, 0f, 0f)))
        }
        cubes.add(entity)
    }

}