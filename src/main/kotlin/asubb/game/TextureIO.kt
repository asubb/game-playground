package asubb.game

import com.danielgergely.kgl.Buffer

data class TextureData(
    val internalFormat: Int,
    val width: Int,
    val height: Int,
    val pixelFormat: Int,
    val buffer: Buffer
)

interface TextureIO {
    fun loadTextureData(path: String): TextureData
}