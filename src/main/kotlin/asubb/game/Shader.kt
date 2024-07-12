package asubb.game

import com.danielgergely.kgl.Kgl
import com.danielgergely.kgl.Program
import com.danielgergely.kgl.UniformLocation
import com.jogamp.opengl.GL2ES2
import com.jogamp.opengl.GL3ES3

class ShaderProgram(
    private val gl: Kgl,
    vararg shaders: Shader,
) {
    private val program: Program

    init {
        with(gl) {
            program = requireNotNull(createProgram())

            shaders.forEach { it.attach(program) }

            linkProgram(program)
            val strInfoLog = getProgramInfoLog(program)
            if (!strInfoLog.isNullOrEmpty()) {
                throw IllegalStateException("Can't link program $strInfoLog")
            }

            shaders.forEach {
                it.detach(program)
                it.delete()
            }
        }
    }

    fun use() {
        gl.useProgram(program)
    }

    fun getUniformLocation(name: String): UniformLocation {
        return requireNotNull(gl.getUniformLocation(program, name)) { "Uniform location `$name` is not found" }
    }
}

class Shader(
    private val gl: Kgl,
    shaderType: Int,
    shaderFile: String,
) {

    private val shader: com.danielgergely.kgl.Shader

    init {
        with(gl) {
            shader = requireNotNull(createShader(shaderType)) { "GL ERROR ${getError()}" }
            shaderSource(shader, shaderFile)

            compileShader(shader)

            val strInfoLog = getShaderInfoLog(shader)

            var strShaderType = ""
            when (shaderType) {
                GL2ES2.GL_VERTEX_SHADER -> strShaderType = "vertex"
                GL3ES3.GL_GEOMETRY_SHADER -> strShaderType = "geometry"
                GL2ES2.GL_FRAGMENT_SHADER -> strShaderType = "fragment"
            }
            if (!strInfoLog.isNullOrEmpty()) {
                throw IllegalStateException("Can't compile shader ($this) $strInfoLog")
            }
        }
    }

    fun attach(program: Program) {
        gl.attachShader(program, shader)
    }

    fun detach(program: Program) {
        gl.detachShader(program, shader)
    }

    fun delete() {
        gl.deleteShader(shader)
    }
}
