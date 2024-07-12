package asubb.game

import com.danielgergely.kgl.*
import com.jogamp.common.nio.Buffers
import com.jogamp.opengl.GL
import com.jogamp.opengl.GL2ES2
import com.jogamp.opengl.GL3ES3
import glm_.glm
import glm_.mat4x4.Mat4
import glm_.vec3.Vec3
import kotlin.math.sin

private const val posLocation = 0
private const val colorLocation = 1
private const val texLocation = 2

private const val strVertexShader = """
    #version 330 core
    layout (location = $posLocation) in vec3 aPos;
    layout (location = $colorLocation) in vec3 aColor;
    layout (location = $texLocation) in vec2 aTexCoord;
    
    uniform mat4 transform;

    out vec4 ourColor;
    out vec2 TexCoord;

    void main()
    {
        gl_Position = transform * vec4(aPos, 1.0f);
        ourColor = vec4(aColor, 1.0);
        TexCoord = vec2(aTexCoord.x, aTexCoord.y);
    }
    """

private const val strFragmentShader = """
    #version 330 core
    out vec4 FragColor;

    in vec4 ourColor;
    in vec2 TexCoord;

    // texture samplers
    uniform sampler2D texture1;
    uniform sampler2D texture2;

    void main()
    {
        FragColor = mix(texture(texture1, TexCoord), texture(texture2, TexCoord), 0.5) * ourColor;
    }
    """

class TexRect(
    private val logger: Logger,
    private val time: Time,
) : Scene {
    private var program: Program = 0
    private var texture1: Texture = 0
    private var texture2: Texture = 0
    private var vao: VertexArrayObject = 0
    private var vbo: GlBuffer = 0
    private var ebo: GlBuffer = 0

    override fun init(gl: Kgl, textureIO: TextureIO) = with(gl) {
        clearColor(1.0f, 1.0f, 1.0f, 0.0f)
        initializeProgram()
        val vertices = FloatBuffer(
            floatArrayOf(
                // positions        // colors         // texture coords
                00.5f, 00.5f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, // top right
                00.5f, -0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, // bottom right
                -0.5f, -0.5f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, // bottom left
                -0.5f, 00.5f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f  // top left
            )
        )
        val indices = IntBuffer(
            intArrayOf(
                0, 1, 3, // first triangle
                1, 2, 3  // second triangle
            )
        )

        vao = createVertexArray()
        bindVertexArray(vao)
        vbo = createBuffer()
        bindBuffer(GL_ARRAY_BUFFER, vbo)
        bufferData(GL_ARRAY_BUFFER, vertices, Buffers.SIZEOF_FLOAT * 32, GL_STATIC_DRAW)
        ebo = createBuffer()
        bindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo)
        bufferData(GL_ELEMENT_ARRAY_BUFFER, indices, Buffers.SIZEOF_INT * 6, GL_STATIC_DRAW)

        // position attribute
        vertexAttribPointer(posLocation, 3, GL_FLOAT, false, Buffers.SIZEOF_FLOAT * 8, 0)
        enableVertexAttribArray(posLocation)

        // color attribute
        vertexAttribPointer(colorLocation, 3, GL_FLOAT, false, Buffers.SIZEOF_FLOAT * 8, Buffers.SIZEOF_FLOAT * 3)
        enableVertexAttribArray(colorLocation)

        // texture coordinate
        vertexAttribPointer(texLocation, 2, GL_FLOAT, false, Buffers.SIZEOF_FLOAT * 8, Buffers.SIZEOF_FLOAT * 6)
        enableVertexAttribArray(texLocation)

        // load and create texture
        // texture 1
        texture1 = createTexture()
        bindTexture(GL_TEXTURE_2D, texture1)
        texParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        texParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        texParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        texParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        val data = textureIO.loadTextureData("/img.png")
        texImage2D(
            GL_TEXTURE_2D,
            0,
            data.internalFormat,
            data.width,
            data.height,
            0,
            data.pixelFormat,
            GL_UNSIGNED_BYTE,
            data.buffer
        )
        // texture 2
        texture2 = createTexture()
        bindTexture(GL_TEXTURE_2D, texture2)
        texParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        texParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        texParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        texParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        val data2 = textureIO.loadTextureData("/img2.png")
        texImage2D(
            GL_TEXTURE_2D,
            0,
            GL_RGB,
            data2.width,
            data2.height,
            0,
            GL_RGB,
            GL_UNSIGNED_BYTE,
            data2.buffer
        )

        useProgram(program)
        val tex1location = requireNotNull(getUniformLocation(program, "texture1")) { "texture1" }
        val tex2location = requireNotNull(getUniformLocation(program, "texture2")) { "texture2" }
        uniform1i(tex1location, 0)
        uniform1i(tex2location, 1)
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
        logger.log { "Linker info $strInfoLog" }

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
        logger.log{ "Compiler lnfo log in $strShaderType shader: $strInfoLog" }

        return shader
    }

    override fun display(gl: Kgl) = with(gl) {
        clear(GL.GL_COLOR_BUFFER_BIT);

        activeTexture(GL_TEXTURE0)
        bindTexture(GL_TEXTURE_2D, texture1)
        activeTexture(GL_TEXTURE1)
        bindTexture(GL_TEXTURE_2D, texture2)

        var trans = Mat4(1.0f);
        val angle = sin(time.getCurrentTime().toDouble()/ 10000.0).toFloat() * 360.0f
        trans = glm.rotate(trans, glm.radians(angle), Vec3(0.0, 0.0, 1.0));
        trans = glm.scale(trans, Vec3(0.5, 0.5, 0.5));
        val transformLoc = requireNotNull(getUniformLocation(program, "transform"))
        uniformMatrix4fv(transformLoc, false, trans.array)

        useProgram(program)
        bindVertexArray(vao)
        drawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT)
    }

    override fun reshape(gl: Kgl, width: Int, height: Int) = with(gl) {
        println("Viewport reshape: width=$width height=$height")
        viewport(0, 0, width, height)
    }

    override fun end(gl: Kgl) {
    }

}