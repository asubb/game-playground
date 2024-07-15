package asubb.game.jogl

import asubb.game.Logger


class ConsoleLogger : Logger {
    override fun log(message: () -> String) {
        println(message())
    }

}