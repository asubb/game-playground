package asubb.game

import com.danielgergely.kgl.Kgl

interface Scene {
    fun init(gl: Kgl)
    fun display(gl: Kgl)
    fun reshape(gl: Kgl, width: Int, height: Int)
    fun end(gl: Kgl)
}