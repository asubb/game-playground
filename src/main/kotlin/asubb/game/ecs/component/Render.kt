package asubb.game.ecs.component

import asubb.game.TextureIO
import asubb.game.ecs.Component
import com.danielgergely.kgl.Kgl
import glm_.mat4x4.Mat4

interface Render : Component {
    override val key get() = requireNotNull(Render::class.simpleName)
    val initialized: Boolean
    fun init(gl: Kgl, textureIO: TextureIO)
    fun draw(gl: Kgl, model: Mat4, view: Mat4, projection: Mat4)
    fun end(gl: Kgl)
}