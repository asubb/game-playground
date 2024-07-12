package asubb.game

import com.danielgergely.kgl.*
import com.jogamp.common.nio.Buffers
import com.jogamp.opengl.GL
import com.jogamp.opengl.GL2ES2
import com.jogamp.opengl.GL3ES3
import glm_.glm
import glm_.mat4x4.Mat4
import glm_.vec3.Vec3
import kotlin.math.cos
import kotlin.math.sin

class RenderingSystem : System {

}

class EcsTest() : Scene {

    private val cube = Cube(Vector(0, 0, 0), Vector(1, 1, 1), Vector(0, 0, 0))

    override fun init(gl: Kgl, textureIO: TextureIO) = with(gl) {
        cube.init(this, textureIO)
    }

    override fun display(gl: Kgl) = with(gl) {
        clearColor(1.0f, 1.0f, 1.0f, 0.0f)
        clear(GL.GL_COLOR_BUFFER_BIT or GL.GL_DEPTH_BUFFER_BIT);
        cube.draw(this)
    }

    override fun reshape(gl: Kgl, width: Int, height: Int) = with(gl) {
        viewport(0, 0, width, height)
    }

    override fun end(gl: Kgl) {
        cube.end(gl)
    }
}