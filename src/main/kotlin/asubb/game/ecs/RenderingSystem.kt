package asubb.game.ecs

import asubb.game.TextureIO
import com.danielgergely.kgl.GL_DEPTH_TEST
import com.danielgergely.kgl.Kgl
import com.jogamp.opengl.GL
import glm_.glm
import glm_.mat4x4.Mat4
import glm_.vec3.Vec3
import kotlin.math.cos
import kotlin.math.sin

/**
 * Rendering System uses components that can be [Render]ed and be [Transform]ed
 */
class RenderingSystem(
    private val state: State,
    private val gl: Kgl,
    private val textureIO: TextureIO,
) : System {

    override fun init() {
        state.get<Render>().forEach { (_, it) ->
            it.init(gl, textureIO)
        }
    }

    override fun update(timeSpan: TimeSpan) {
        gl.clearColor(1.0f, 1.0f, 1.0f, 0.0f)
        gl.clear(GL.GL_COLOR_BUFFER_BIT or GL.GL_DEPTH_BUFFER_BIT);
        gl.enable(GL_DEPTH_TEST);
        state.get<Render>().forEach { (entity, renderer) ->
            if (!renderer.initialized) renderer.init(gl, textureIO)
            val transform = state.get<Transform>(entity)

            val model = transform?.getModelMatrix() ?: Mat4(1.0f)

            val radius = 10.0f;
            val camX = 0//sin(globalTime) * radius;
            val camZ = 4//cos(globalTime) * radius;
            val view = glm.lookAt(
                Vec3(camX, 0.0, camZ),
                Vec3(0.0, 0.0, 0.0),
                Vec3(0.0, 1.0, 0.0)
            );

            var fov = 45.0f
            val projection = glm.perspective(glm.radians(fov), 800.0f / 600.0f, 0.1f, 100.0f);

            renderer.draw(gl, model, view, projection)
        }
    }

    override fun destroy() {
        state.get<Render>().forEach { (_, it) ->
            it.end(gl)
        }
    }
}