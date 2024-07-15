package asubb.game

import asubb.game.ecs.EcsTest
import asubb.game.jogl.ConsoleLogger
import asubb.game.jogl.JavaTime
import asubb.game.jogl.JoglGlEngine


fun main() {
//    JoglGlEngine("triangle", HelloTriangle(), debug = true)
//    JoglGlEngine("texRect", TexRect(), debug = true)
//    JoglGlEngine("Coordinate system", CoordSystem(JavaTime(), ConsoleLogger()), debug = true)
    JoglGlEngine("ECS Test", EcsTest(JavaTime()), debug = true)
}

