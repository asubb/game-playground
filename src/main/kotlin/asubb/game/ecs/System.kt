package asubb.game.ecs


interface System {
    fun init()
    fun update(timeSpan: TimeSpan)
    fun destroy()
}

interface Action
interface ActionableSystem : System {
    fun newAction(action: Action)
}

data class TimeSpan(
    val globalTime: Long,
    val delta: Long,
)