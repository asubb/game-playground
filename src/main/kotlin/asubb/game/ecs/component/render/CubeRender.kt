package asubb.game.ecs.component.render

import asubb.game.FragmentShader
import asubb.game.ShaderProgram
import asubb.game.TextureIO
import asubb.game.VertexShader
import asubb.game.ecs.Render
import com.danielgergely.kgl.*
import com.jogamp.common.nio.Buffers
import glm_.mat4x4.Mat4

class CubeRender : Render {

    private val posLocation = 0
    private val texLocation = 1
    private val strVertexShader = """
    #version 330 core
    layout (location = $posLocation) in vec3 aPos;
    layout (location = $texLocation) in vec2 aTexCoord;
    
    uniform mat4 model;
    uniform mat4 view;
    uniform mat4 projection;
    
    out vec2 TexCoord;

    void main()
    {
        gl_Position = projection * view * model * vec4(aPos, 1.0);
        TexCoord = vec2(aTexCoord.x, aTexCoord.y);
    }
    """
    private val strFragmentShader = """
    #version 330 core
    out vec4 FragColor;

    in vec2 TexCoord;

    uniform sampler2D texture1;
    uniform sampler2D texture2;

    void main()
    {
        FragColor = mix(texture(texture1, TexCoord), texture(texture2, TexCoord), 0.4);
    }
    """

    private lateinit var program: ShaderProgram
    private var vao: VertexArrayObject = 0
    private var vbo: GlBuffer = 0
    private var texture1: Texture = 0
    private var texture2: Texture = 0
    private var wasInitialized: Boolean = false

    override val initialized: Boolean
        get() = wasInitialized

    override fun init(gl: Kgl, textureIO: TextureIO) = with(gl) {
        wasInitialized = true
        program = ShaderProgram(
            gl,
            VertexShader(gl, strVertexShader),
            FragmentShader(gl, strFragmentShader)
        )

        val vertices = FloatBuffer(
            floatArrayOf(
                -0.5f, -0.5f, -0.5f, 0.0f, 0.0f,
                0.5f, -0.5f, -0.5f, 1.0f, 0.0f,
                0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
                0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
                -0.5f, 0.5f, -0.5f, 0.0f, 1.0f,
                -0.5f, -0.5f, -0.5f, 0.0f, 0.0f,

                -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
                0.5f, -0.5f, 0.5f, 1.0f, 0.0f,
                0.5f, 0.5f, 0.5f, 1.0f, 1.0f,
                0.5f, 0.5f, 0.5f, 1.0f, 1.0f,
                -0.5f, 0.5f, 0.5f, 0.0f, 1.0f,
                -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,

                -0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
                -0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
                -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
                -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
                -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
                -0.5f, 0.5f, 0.5f, 1.0f, 0.0f,

                0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
                0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
                0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
                0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
                0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
                0.5f, 0.5f, 0.5f, 1.0f, 0.0f,

                -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
                0.5f, -0.5f, -0.5f, 1.0f, 1.0f,
                0.5f, -0.5f, 0.5f, 1.0f, 0.0f,
                0.5f, -0.5f, 0.5f, 1.0f, 0.0f,
                -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
                -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,

                -0.5f, 0.5f, -0.5f, 0.0f, 1.0f,
                0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
                0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
                0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
                -0.5f, 0.5f, 0.5f, 0.0f, 0.0f,
                -0.5f, 0.5f, -0.5f, 0.0f, 1.0f
            )
        )

        vao = createVertexArray()
        bindVertexArray(vao)
        vbo = createBuffer()
        bindBuffer(GL_ARRAY_BUFFER, vbo)
        bufferData(GL_ARRAY_BUFFER, vertices, Buffers.SIZEOF_FLOAT * 5 * 36, GL_STATIC_DRAW)

        // position attribute
        vertexAttribPointer(posLocation, 3, GL_FLOAT, false, Buffers.SIZEOF_FLOAT * 5, 0)
        enableVertexAttribArray(posLocation)

        // texture coordinate
        vertexAttribPointer(texLocation, 2, GL_FLOAT, false, Buffers.SIZEOF_FLOAT * 5, Buffers.SIZEOF_FLOAT * 3)
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
            data.buffer,
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

        program.use()
        val tex1location = program.getUniformLocation("texture1")
        val tex2location = program.getUniformLocation("texture2")
        uniform1i(tex1location, 0)
        uniform1i(tex2location, 1)
    }

    override fun draw(gl: Kgl, model: Mat4, view: Mat4, projection: Mat4) = with(gl) {
        program.use()
        val viewLoc = program.getUniformLocation("view")
        uniformMatrix4fv(viewLoc, false, view.array)
        val projectionLoc = program.getUniformLocation("projection")
        uniformMatrix4fv(projectionLoc, false, projection.array)
        val modelLoc = program.getUniformLocation("model")
        uniformMatrix4fv(modelLoc, false, model.array)

        activeTexture(GL_TEXTURE0)
        bindTexture(GL_TEXTURE_2D, texture1)
        activeTexture(GL_TEXTURE1)
        bindTexture(GL_TEXTURE_2D, texture2)

        drawArrays(GL_TRIANGLES, 0, 36)
    }

    override fun end(gl: Kgl) {
    }
}