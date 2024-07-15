package asubb.game.ecs

interface System {
    fun init()
    fun update(timeSpan: TimeSpan)
    fun destroy()
}

data class TimeSpan(
    val globalTime: Long,
    val delta: Long,
)