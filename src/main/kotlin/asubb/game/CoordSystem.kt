package asubb.game

import asubb.game.jogl.JoglTextureIO
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

private const val posLocation = 0
private const val texLocation = 1

private const val strVertexShader = """
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

private const val strFragmentShader = """
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

class CoordSystem(
    private val time: Time,
    private val logger: Logger,
) : Scene {
    private var program: Program = 0
    private var texture1: Texture = 0
    private var texture2: Texture = 0
    private var vao: VertexArrayObject = 0
    private var vbo: GlBuffer = 0
    private var cameraPos = Vec3(0.0f, 0.0f, 3.0f)
    private var cameraFront = Vec3(0.0f, 0.0f, -1.0f)
    private var cameraUp = Vec3(0.0f, 1.0f, 0.0f);
    private var deltaTime = 0.0 // Time between current frame and last frame
    private var lastFrame = 0.0 // Time of last framek
    var lastX = 400f
    var lastY = 300f
    private var yaw = -90.0f
    private var pitch = 0.0f
    private var roll = 0.0f
    private var fov = 45.0f

    override fun init(gl: Kgl, textureIO: TextureIO) = with(gl) {
        clearColor(1.0f, 1.0f, 1.0f, 0.0f)
        initializeProgram()
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

        useProgram(program)
        val tex1location = requireNotNull(getUniformLocation(program, "texture1")) { "texture1" }
        val tex2location = requireNotNull(getUniformLocation(program, "texture2")) { "texture2" }
        uniform1i(tex1location, 0)
        uniform1i(tex2location, 1)

        enable(GL_DEPTH_TEST);

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
        logger.log { "Compiler lnfo log in $strShaderType shader: $strInfoLog" }

        return shader
    }

    override fun display(gl: Kgl) = with(gl) {
        val currentFrame = time.getCurrentTime().toDouble()
        deltaTime = currentFrame - lastFrame;
        lastFrame = currentFrame;

        clear(GL.GL_COLOR_BUFFER_BIT or GL.GL_DEPTH_BUFFER_BIT);

        activeTexture(GL_TEXTURE0)
        bindTexture(GL_TEXTURE_2D, texture1)
        activeTexture(GL_TEXTURE1)
        bindTexture(GL_TEXTURE_2D, texture2)

        // manually rotating view matrix
//        var view = Mat4(1.0f);
//        view = glm.translate(view, Vec3(0.0f, 0.0f, -10.0f));
//        val angle = sin(System.currentTimeMillis().toDouble() / 10000.0).toFloat() * 360.0f
//        view = glm.rotate(view, glm.radians(angle), Vec3(0f, 1f, 0f))

        // lookAt rotating around the beginning of the coordinates
//        val radius = 10.0f;
//        val camX = sin(System.currentTimeMillis().toDouble() / 1000.0) * radius;
//        val camZ = cos(System.currentTimeMillis().toDouble() / 1000.0) * radius;
//        val view = glm.lookAt(
//            Vec3(camX, 0.0, camZ),
//            Vec3(0.0, 0.0, 0.0),
//            Vec3(0.0, 1.0, 0.0)
//        );

        // key press handling
        val direction = Vec3(
            x = cos(glm.radians(yaw)) * cos(glm.radians(pitch)),
            y = sin(glm.radians(pitch)),
            z = sin(glm.radians(yaw)) * cos(glm.radians(pitch)),
        )
        cameraFront = glm.normalize(direction);
        val view = glm.lookAt(cameraPos, cameraPos + cameraFront, cameraUp);

        val viewLoc = requireNotNull(getUniformLocation(program, "view"))
        uniformMatrix4fv(viewLoc, false, view.array)

        val projection = glm.perspective(glm.radians(fov), 800.0f / 600.0f, 0.1f, 100.0f);
        val projectionLoc = requireNotNull(getUniformLocation(program, "projection"))
        uniformMatrix4fv(projectionLoc, false, projection.array)

        useProgram(program)
        bindVertexArray(vao)
        val cubePositions = listOf(
            Vec3(0.0f, 0.0f, 0.0f),
            Vec3(2.0f, 5.0f, -15.0f),
            Vec3(-1.5f, -2.2f, -2.5f),
            Vec3(-3.8f, -2.0f, -12.3f),
            Vec3(2.4f, -0.4f, -3.5f),
            Vec3(-1.7f, 3.0f, -7.5f),
            Vec3(1.3f, -2.0f, -2.5f),
            Vec3(1.5f, 2.0f, -2.5f),
            Vec3(1.5f, 0.2f, -1.5f),
            Vec3(-1.3f, 1.0f, -1.5f)
        );
        cubePositions.forEachIndexed { i, cubePos ->
            var model = Mat4(1.0f);
            model = glm.translate(model, cubePos)
            val angle = if (i > 0)
                sin(time.getCurrentTime().toDouble() / 10000.0 / i).toFloat() * 360.0f * i
            else
                0f
            model = glm.rotate(model, glm.radians(angle), Vec3(1.0f, 0.0f, 0.0f));
            val modelLoc = requireNotNull(getUniformLocation(program, "model"))
            uniformMatrix4fv(modelLoc, false, model.array)
            drawArrays(GL_TRIANGLES, 0, 36)
        }
    }

    override fun reshape(gl: Kgl, width: Int, height: Int) = with(gl) {
        viewport(0, 0, width, height)
    }

    override fun end(gl: Kgl) {
    }

    override fun keyPressed(keyCode: Short) {
        val cameraSpeed = 2.5f * deltaTime;
        when (keyCode.toInt()) {
            'W'.code -> cameraPos = cameraPos + cameraFront * cameraSpeed
            'A'.code -> cameraPos = cameraPos - glm.normalize(glm.cross(cameraFront, cameraUp)) * cameraSpeed
            'S'.code -> cameraPos = cameraPos - cameraFront * cameraSpeed
            'D'.code -> cameraPos = cameraPos + glm.normalize(glm.cross(cameraFront, cameraUp)) * cameraSpeed
        }
    }

    override fun mouseMoved(x: Int, y: Int) {
        var xoffset = x.toFloat() - lastX
        var yoffset = lastY - y.toFloat() // reversed since y-coordinates range from bottom to top
        lastX = x.toFloat()
        lastY = y.toFloat()

        val sensitivity = 0.1f
        xoffset *= sensitivity
        yoffset *= sensitivity
        yaw += xoffset
        pitch += yoffset

        if (pitch > 89.0f)
            pitch = 89.0f
        if (pitch < -89.0f)
            pitch = -89.0f
    }

    override fun mouseWheelMoved(x: Float, y: Float) {
        fov -= y
        if (fov < 1.0f)
            fov = 1.0f;
        if (fov > 45.0f)
            fov = 45.0f;
    }
}