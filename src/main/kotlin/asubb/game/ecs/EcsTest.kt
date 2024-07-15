package asubb.game.ecs

import asubb.game.Scene
import asubb.game.TextureIO
import asubb.game.Time
import com.danielgergely.kgl.*

class EcsTest(
    private val time: Time,
) : Scene {

    private val state = State(EntityManager())
    private lateinit var renderingSystem: RenderingSystem
    private lateinit var cubeWorldSystem: CubeWorldSystem
    private var lastFrame = time.getCurrentTime()

    override fun init(gl: Kgl, textureIO: TextureIO) {
        renderingSystem = RenderingSystem(state, gl, textureIO)
        cubeWorldSystem = CubeWorldSystem(state)
        renderingSystem.init()
        cubeWorldSystem.init()
    }

    override fun display(gl: Kgl) {
        val now = time.getCurrentTime()
        val delta = now - lastFrame
        lastFrame = now
        val timeSpan = TimeSpan(now, delta)
        cubeWorldSystem.update(timeSpan)
        renderingSystem.update(timeSpan)
    }

    override fun reshape(gl: Kgl, width: Int, height: Int) = with(gl) {
        viewport(0, 0, width, height)
    }

    override fun end(gl: Kgl) {
        cubeWorldSystem.destroy()
        renderingSystem.destroy()
    }
}