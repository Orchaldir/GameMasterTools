package at.orchaldir.gm.utils

import at.orchaldir.gm.core.model.State
import kotlinx.serialization.Serializable

interface Id<ID> {
    fun next(): ID
    fun type(): String
    fun plural() = type() + "s"
    fun value(): Int
    fun print() = "${type()} ${value()}"
}

interface Element<ID> {
    fun id(): ID

    fun name(state: State): String
    fun toSortString(state: State) = name(state)

    fun clone(cloneId: ID): Element<ID> = error("Not supported!")
}

@Serializable
data class Storage<ID : Id<ID>, ELEMENT : Element<ID>>(
    private val elements: Map<ID, ELEMENT>,
    val nextId: ID,
    val lastId: ID = nextId,
) {
    constructor(nextId: ID) : this(mapOf(), nextId)

    constructor(element: ELEMENT) : this(listOf(element))

    constructor(elements: List<ELEMENT>) : this(
        elements.associateBy { it.id() },
        elements.map { it.id() }.last().next()
    )

    fun add(element: ELEMENT): Storage<ID, ELEMENT> {
        require(element.id() == nextId) { "Added ${element.id().print()} doesn't have the next free id!" }

        return Storage(elements + mapOf(nextId to element), nextId.next(), nextId)
    }

    fun remove(id: ID) = copy(elements = elements - setOf(id))

    fun update(element: ELEMENT) = copy(elements = elements + mapOf(element.id() to element))

    fun update(updated: List<ELEMENT>) = copy(elements = elements + updated.associateBy { it.id() })

    fun getIds() = elements.keys

    fun getAll() = elements.values

    fun getAllExcept(id: ID) = elements.values
        .filter { it.id() != id }

    fun getSize() = elements.size

    fun isEmpty() = elements.isEmpty()

    fun getType() = nextId.type()
    fun getPlural() = nextId.plural()

    fun getName(id: Id<*>, state: State) = elements[id]?.name(state) ?: "Unknown"

    fun get(id: ID) = elements[id]

    fun get(ids: Collection<ID>) = ids.mapNotNull { elements[it] }

    fun getOptional(id: ID?) = if (id != null) {
        elements[id]
    } else {
        null
    }

    fun getOrThrow(id: ID, message: () -> String) =
        elements[id] ?: throw IllegalArgumentException(message())

    fun getOrThrow(id: ID) = getOrThrow(id) { "Requires unknown ${id.print()}!" }

    fun contains(id: ID) = elements.containsKey(id)

    fun require(id: ID) {
        require(contains(id)) { "Requires unknown ${id.print()}!" }
    }

    fun requireOptional(id: ID?) {
        if (id != null) {
            require(id)
        }
    }

    fun requireOptional(id: ID?, message: () -> String) {
        if (id != null) {
            require(id, message)
        }
    }

    fun require(ids: Collection<ID>) = ids.forEach { require(it) }

    fun requireOptional(ids: Collection<ID?>) = ids.forEach { requireOptional(it) }

    fun require(id: ID, message: () -> String) {
        require(contains(id)) { message() }
    }
}
