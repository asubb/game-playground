package asubb.game.ecs

typealias Entity = Long

class EntityManager {

    private var counter = 0L

    fun newEntity(): Entity = ++counter

}