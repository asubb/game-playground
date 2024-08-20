package asubb.game.ecs

import asubb.game.Scene
import asubb.game.TextureIO
import asubb.game.Time
import asubb.game.ecs.system.*
import asubb.game.ecs.system.ActorMoveDirection.*
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
        val timeSpan = timeSpan()
        cubeWorldSystem.update(timeSpan)
        physicsSystem.update(timeSpan)
        actorSystem.update(timeSpan)
        renderingSystem.update(timeSpan)
    }

    private fun timeSpan(): TimeSpan {
        val now = time.getCurrentTime()
        val delta = now - lastFrame
        lastFrame = now
        val timeSpan = TimeSpan(now, delta)
        return timeSpan
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
        when (keyCode.toInt()) {
            'W'.code -> actorSystem.newAction(ActorMoveStart(Forward))
            'A'.code -> actorSystem.newAction(ActorMoveStart(Left))
            'S'.code -> actorSystem.newAction(ActorMoveStart(Backward))
            'D'.code -> actorSystem.newAction(ActorMoveStart(Right))
        }
    }

    override fun keyReleased(keyCode: Short) {
        when (keyCode.toInt()) {
            'W'.code -> actorSystem.newAction(ActorMoveEnd(Forward))
            'A'.code -> actorSystem.newAction(ActorMoveEnd(Left))
            'S'.code -> actorSystem.newAction(ActorMoveEnd(Backward))
            'D'.code -> actorSystem.newAction(ActorMoveEnd(Right))
        }
    }

    private var lastX = 0f
    private var lastY = 0f
    override fun mouseMoved(x: Int, y: Int) {
        val xoffset = x.toFloat() - lastX
        val yoffset = lastY - y.toFloat() // reversed since y-coordinates range from bottom to top
        lastX = x.toFloat()
        lastY = y.toFloat()
        actorSystem.newAction(ActorViewMove(xoffset, yoffset))
    }

    override fun mouseWheelMoved(x: Float, y: Float) {
    }
}