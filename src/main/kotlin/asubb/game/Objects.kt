package asubb.game

import com.danielgergely.kgl.*

interface Component {
}

interface Positionable {
    val position: Vector
}

interface Scalable {
    val size: Vector
}

interface Rotatable {
    val rotation: Vector
}

interface Renderable {
    fun init(gl: Kgl, textureIO: TextureIO)
    fun draw(gl: Kgl)
    fun end(gl: Kgl)
}

interface Entity

interface System

data class Vector(val x: Int, val y: Int, val z: Int)

