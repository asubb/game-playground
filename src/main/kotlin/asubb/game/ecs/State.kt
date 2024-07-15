package asubb.game.ecs

import java.util.concurrent.ConcurrentHashMap

class State(
    private val entityManager: EntityManager
) {
    // 2d table to represent the following structure
    //                 |  component 1 key | component 2 key | component 3 key |
    //-----------------|------------------|-----------------|-----------------|
    // entity 1        | component 1      |                 | component 3     |
    // entity 2        |                  | component 2     |                 |
    // entity 3        | component 1      | component 2     |                 |
    //-----------------|------------------|-----------------|-----------------|
    private val table = ConcurrentHashMap<Entity, MutableMap<String, Component>>()

    fun <T : Component> get(key: ComponentKey, entity: Entity): T? {
        @Suppress("UNCHECKED_CAST")
        return table[entity]?.get(key) as T?
    }

    fun <T : Component> get(key: ComponentKey): List<Pair<Entity, T>> {
        @Suppress("UNCHECKED_CAST")
        return table.mapNotNull { (entity, components) -> entity to components[key] as T }
    }

    fun newEntity(): Entity {
        val entity = entityManager.newEntity()
        table[entity] = ConcurrentHashMap()
        return entity
    }

    fun addComponent(entity: Entity, component: Component) {
        table[entity]?.let { it[component.key] = component }
    }

    fun updateComponent(entity: Entity, component: Component) {
        // currently the same as update but can be a different operation with certain optimizations
        table[entity]?.let { it[component.key] = component }
    }

}

fun State.createEntity(builder: Pair<Entity, State>.() -> Unit): Entity {
    val newEntity = this.newEntity()
    builder(newEntity to this)
    return newEntity
}

fun Pair<Entity, State>.addComponent(component: Component) {
    this.second.addComponent(this.first, component)
}

inline fun <reified T: Component> State.withEntity(entity: Entity, noinline body: (T) -> Unit) {
    this.get<T>(entity)?.let { body(it)}
}

inline fun <reified T : Component> State.get(entity: Entity): T? {
    val key: ComponentKey = requireNotNull(T::class.simpleName)
    return this.get(key, entity)
}

inline fun <reified T : Component> State.get(): List<Pair<Entity, T>> {
    val key: ComponentKey = requireNotNull(T::class.simpleName)
    return get(key)
}

