package asubb.game.ecs

import asubb.game.Scene
import asubb.game.TextureIO
import asubb.game.Time
import asubb.game.ecs.system.ActorSystem
import asubb.game.ecs.system.CubeWorldSystem
import asubb.game.ecs.system.PhysicsSystem
import asubb.game.ecs.system.RenderingSystem
import com.danielgergely.kgl.*

class EcsTest(
    private val time: Time,
) : Scene {

    private val world = World(EntityManager())
    private lateinit var renderingSystem: RenderingSystem
    private lateinit var cubeWorldSystem: CubeWorldSystem
    private lateinit var actorSystem: ActorSystem
    private lateinit var physicsSystem: PhysicsSystem
    private var lastFrame = time.getCurrentTime()

    override fun init(gl: Kgl, textureIO: TextureIO) {
        renderingSystem = RenderingSystem(world, gl, textureIO)
        cubeWorldSystem = CubeWorldSystem(world)
        actorSystem = ActorSystem(world)
        physicsSystem = PhysicsSystem(world)
        actorSystem.init()
        renderingSystem.init()
        cubeWorldSystem.init()
        physicsSystem.init()
    }

    override fun display(gl: Kgl) {
        val now = time.getCurrentTime()
        val delta = now - lastFrame
        lastFrame = now
        val timeSpan = TimeSpan(now, delta)
        cubeWorldSystem.update(timeSpan)
        physicsSystem.update(timeSpan)
        actorSystem.update(timeSpan)
        renderingSystem.update(timeSpan)
    }

    override fun reshape(gl: Kgl, width: Int, height: Int) = with(gl) {
        viewport(0, 0, width, height)
    }

    override fun end(gl: Kgl) {
        cubeWorldSystem.destroy()
        renderingSystem.destroy()
        actorSystem.destroy()
        physicsSystem.destroy()
    }

    override fun keyPressed(keyCode: Short) {
    }

    override fun mouseMoved(x: Int, y: Int) {
    }

    override fun mouseWheelMoved(x: Float, y: Float) {
    }
}