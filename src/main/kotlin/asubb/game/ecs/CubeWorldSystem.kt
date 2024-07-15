package asubb.game.ecs

class CubeWorldSystem(
    private val state: State,
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
    )
) : System {

    private val cubes = mutableListOf<Entity>()

    override fun init() {
        cubesPosSize.forEach { (pos, scale) ->
            addCube(pos, scale)
        }
        state.addComponent(
            cubes[0],
            Motion(rotation = vector(10f, 0f, 0f))
        )
    }

    override fun update(timeSpan: TimeSpan) {
        cubes.forEach { cube ->
            state.withEntity<Transform>(cube) { transform ->
                state.withEntity<Motion>(cube) { motion ->
                    state.updateComponent(
                        cube,
                        transform.copy(
                            position = motion.newPosition(timeSpan, transform.position),
                            rotation = motion.newRotation(timeSpan, transform.rotation)
                        )
                    )
                }
            }
        }
    }

    override fun destroy() {
    }

    private fun addCube(position: Vector, scale: Vector) {
        val entity = state.createEntity {
            addComponent(CubeRender())
            addComponent(Transform(position = position))
        }
        cubes.add(entity)
    }

}