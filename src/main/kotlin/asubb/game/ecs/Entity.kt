package asubb.game.ecs

typealias Entity = Long

const val UndefinedEntity: Entity = 0L

fun Entity.isDefined(): Boolean = this != UndefinedEntity

class EntityManager {

    private var counter = 0L

    fun newEntity(): Entity = ++counter

}