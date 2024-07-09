package asubb.game.jogl

import asubb.game.TextureData
import asubb.game.TextureIO
import com.danielgergely.kgl.ByteBuffer
import com.jogamp.opengl.GL
import java.io.File

class JoglTextureIO(private val gl: GL) : TextureIO {
    override fun loadTextureData(path: String): TextureData {
        val data = com.jogamp.opengl.util.texture.TextureIO.newTextureData(
            gl.glProfile,
            File(path),
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