package asubb.game

interface Logger {
    fun log(message: () -> String)
}