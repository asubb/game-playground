package asubb.game.ecs.system

import asubb.game.TextureIO
import asubb.game.ecs.*
import asubb.game.ecs.component.Transform
import asubb.game.ecs.component.Camera
import asubb.game.ecs.component.Render
import com.danielgergely.kgl.GL_DEPTH_TEST
import com.danielgergely.kgl.Kgl
import com.jogamp.opengl.GL
import glm_.mat4x4.Mat4

/**
 * Rendering System uses components that can be [Render]ed establising project and view matrix
 * based on singular [Camera] component, and making sure all [Transform] objects get there model matrix.
 *
 * Relies on OpenGL subsystem.
 */
class RenderingSystem(
    private val world: World,
    private val gl: Kgl,
    private val textureIO: TextureIO,
) : System {

    override fun init() {
        world.get<Render>().forEach { (_, it) ->
            it.init(gl, textureIO)
        }
    }

    override fun update(timeSpan: TimeSpan) {
        gl.clearColor(1.0f, 1.0f, 1.0f, 0.0f)
        gl.clear(GL.GL_COLOR_BUFFER_BIT or GL.GL_DEPTH_BUFFER_BIT);
        gl.enable(GL_DEPTH_TEST);

        // expect exactly one camera
        val camera = requireNotNull(world.get<Camera>().singleOrNull()?.second) {
            "Camera component is not created for any entity, or is not single"
        }
        val view = camera.getViewMatrix()
        val projection = camera.getProjectionMatrix()

        world.get<Render>().forEach { (entity, renderer) ->
            if (!renderer.initialized) renderer.init(gl, textureIO)
            val transform = world.get<Transform>(entity)
            val model = transform?.getModelMatrix() ?: Mat4(1.0f)

            renderer.draw(gl, model, view, projection)
        }
    }

    override fun destroy() {
        world.get<Render>().forEach { (_, it) ->
            it.end(gl)
        }
    }
}