package asubb.game

import asubb.game.jogl.JoglGlEngine
import asubb.game.jogl.JoglTextureIO


fun main() {
//    JoglGlEngine("triangle", HelloTriangle(), debug = true)
    JoglGlEngine("texRect", TexRect(), debug = true)
//    JoglGlEngine("Coordinate system", CoordSystem(), debug = true)
}

