package asubb.game.jogl

import asubb.game.TextureData
import asubb.game.TextureIO
import com.danielgergely.kgl.ByteBuffer
import com.jogamp.opengl.GL

class JoglTextureIO(private val gl: GL) : TextureIO {
    override fun loadTextureData(path: String): TextureData {
        val data = com.jogamp.opengl.util.texture.TextureIO.newTextureData(
            gl.glProfile,
            requireNotNull(this::class.java.getResourceAsStream(path)) { "Missing resource $path" },
            false,
            null
        )
        return TextureData(
            data.internalFormat,
            data.width,
            data.height,
            data.pixelFormat,
            ByteBuffer(data.buffer as java.nio.ByteBuffer)
        )
    }
}