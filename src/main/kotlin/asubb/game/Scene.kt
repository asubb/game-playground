package asubb.game

import com.danielgergely.kgl.Kgl

interface Scene {
    fun init(gl: Kgl, textureIO: TextureIO)
    fun display(gl: Kgl)
    fun reshape(gl: Kgl, width: Int, height: Int)
    fun end(gl: Kgl)

    fun keyPressed(keyCode: Short) {}
    fun keyReleased(keyCode: Short) {}
    fun mouseMoved(x: Int, y: Int) {}
    fun mouseWheelMoved(x: Float, y: Float) {}
}