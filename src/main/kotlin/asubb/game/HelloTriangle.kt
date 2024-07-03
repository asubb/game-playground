package asubb.game

import com.danielgergely.kgl.*
import com.jogamp.common.nio.Buffers
import com.jogamp.newt.event.KeyEvent
import com.jogamp.opengl.GL
import com.jogamp.opengl.GL2ES2
import com.jogamp.opengl.GL3
import com.jogamp.opengl.GL3ES3
import kotlin.math.absoluteValue
import kotlin.math.sin

private const val posLocation = 0
private const val colorLocation = 1
private const val strVertexShader = """
    #version 330 core
    layout (location = $posLocation) in vec3 aPos;
    layout (location = $colorLocation) in vec3 aColor;
    
    
    out vec4 vertexColor;

    void main()
    {
        gl_Position = vec4(aPos.x, aPos.y, aPos.z, 1.0);
        vertexColor = vec4(aColor, 1.0);
    }
    """

private const val strFragmentShader = """
    #version 330 core
    in vec4 vertexColor;
    
    uniform vec4 ourColor;

    out vec4 FragColor;

    void main()
    {
        FragColor = (vertexColor + ourColor) / 2;
    }     
    """


class HelloTriangle : Scene {

    var vao: VertexArrayObject = 0
    var vao2: VertexArrayObject = 0
    var vbo: GlBuffer = 0
    var vbo2: GlBuffer = 0
    var program: Program = 0

    /**
     * Called after the window and OpenGL are initialized. Called exactly once, before the main loop.
     * @param gl
     */
    override fun init(gl: Kgl) = with(gl) {
        clearColor(1.0f, 1.0f, 1.0f, 0.0f)
        initializeProgram()

        val vert = FloatBuffer(
            floatArrayOf(
                -0.3f, 00.5f, -1.0f, 1.0f, 0.0f, 0.0f,
                -0.8f, -0.5f, -1.0f, 0.0f, 1.0f, 0.0f,
                00.2f, -0.5f, -1.0f, 0.0f, 0.0f, 1.0f,
            )
        )
        val vert2 = FloatBuffer(
            floatArrayOf(
                -0.2f, 00.5f, -1.0f, 0.5f, 0.0f, 0.7f,
                00.3f, -0.5f, -1.0f, 0.4f, 0.3f, 0.1f,
                00.8f, 00.5f, -1.0f, 0.5f, 0.9f, 0.2f,
            )
        )
        // First VAO setup
        vao = createVertexArray()
        bindVertexArray(vao)
        vbo = createBuffer()
        bindBuffer(GL.GL_ARRAY_BUFFER, vbo)
        bufferData(GL.GL_ARRAY_BUFFER, vert, Buffers.SIZEOF_FLOAT * 18, GL.GL_STATIC_DRAW)
        vertexAttribPointer(posLocation, 3, GL.GL_FLOAT, false, Buffers.SIZEOF_FLOAT * 6, 0)
        enableVertexAttribArray(posLocation)
        vertexAttribPointer(colorLocation, 3, GL.GL_FLOAT, false, Buffers.SIZEOF_FLOAT * 6, Buffers.SIZEOF_FLOAT * 3)
        enableVertexAttribArray(colorLocation)

        // Second VAO setup
        vao2 = createVertexArray()
        bindVertexArray(vao2)
        vbo2 = createBuffer()
        bindBuffer(GL.GL_ARRAY_BUFFER, vbo2)
        bufferData(GL.GL_ARRAY_BUFFER, vert2, Buffers.SIZEOF_FLOAT * 18, GL.GL_STATIC_DRAW)
        vertexAttribPointer(posLocation, 3, GL.GL_FLOAT, false, Buffers.SIZEOF_FLOAT * 6, 0)
        enableVertexAttribArray(posLocation)
        vertexAttribPointer(colorLocation, 3, GL.GL_FLOAT, false, Buffers.SIZEOF_FLOAT * 6, Buffers.SIZEOF_FLOAT * 3)
        enableVertexAttribArray(colorLocation)
    }

    fun Kgl.initializeProgram() {
        program = requireNotNull(createProgram())

        val shaderList = listOf(
            createShader(GL2ES2.GL_VERTEX_SHADER, strVertexShader),
            createShader(GL2ES2.GL_FRAGMENT_SHADER, strFragmentShader)
        )
        shaderList.forEach { attachShader(program, it) }

        linkProgram(program)
        val strInfoLog = getProgramInfoLog(program)
        System.err.println("Linker info $strInfoLog")

        shaderList.forEach { detachShader(program, it) }


//        shaderList.forEach(gl::glDeleteShader)
    }

    fun Kgl.createShader(shaderType: Int, shaderFile: String): Int {
        val shader = requireNotNull(createShader(shaderType)) { "GL ERROR ${getError()}" }
        shaderSource(shader, shaderFile)

        compileShader(shader)

        val strInfoLog = getShaderInfoLog(shader)

        var strShaderType = ""
        when (shaderType) {
            GL2ES2.GL_VERTEX_SHADER -> strShaderType = "vertex"
            GL3ES3.GL_GEOMETRY_SHADER -> strShaderType = "geometry"
            GL2ES2.GL_FRAGMENT_SHADER -> strShaderType = "fragment"
        }
        System.err.println("Compiler lnfo log in $strShaderType shader: $strInfoLog")

        return shader
    }

    /**
     * Called to update the display. You don't need to swap the buffers after all of your rendering to display what you rendered,
     * it is done automatically.
     * @param gl
     */
    override fun display(gl: Kgl) = with(gl) {
        clear(GL.GL_COLOR_BUFFER_BIT);

        val value = sin(System.currentTimeMillis().toDouble() / 1000).absoluteValue.toFloat() * 3.0f
        val vertexColorLocation = requireNotNull(getUniformLocation(program, "ourColor"))
        useProgram(program)

        uniform4f(
            vertexColorLocation,
            if (value < 1.0f) value else value - 1.0f,
            if (value >= 1.0f && value < 2.0f) value - 1.0f else value - 2.0f,
            if (value >= 2.0f && value < 3.0f) value - 2.0f else 0.0f,
            1.0f
        );

        bindVertexArray(vao)
        drawArrays(GL.GL_TRIANGLES, 0, 3);

        uniform4f(
            vertexColorLocation,
            0.5f,
            0.5f,
            0.0f,
            1.0f
        );

        bindVertexArray(vao2)
        drawArrays(GL.GL_TRIANGLES, 0, 3);
    }

    /**
     * Called whenever the window is resized. The new window size is given, in pixels. This is an opportunity to call glViewport or
     * glScissor to keep up with the change in size.
     * @param gl
     * @param w
     * @param h
     */
    override fun reshape(gl: Kgl, width: Int, height: Int) = with(gl) {
        println("Viewport reshape: width=$width height=$height")
        viewport(0, 0, width, height)
    }

    /**
     * Called at the end, here you want to clean all the resources.
     * @param gl
     */
    override fun end(gl: Kgl) = with(gl) {
        deleteProgram(program)
        deleteBuffer(vbo)
        deleteVertexArray(vao)
        deleteBuffer(vbo2)
        deleteVertexArray(vao2)
    }
}