package at.orchaldir.gm.utils

import kotlinx.serialization.Serializable

interface Id<ID> {
    fun next(): ID
    fun type(): String
    fun value(): Int
}

interface Element<ID> {
    fun id(): ID
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
        return Storage(elements + mapOf(nextId to element), nextId.next(), nextId)
    }

    fun remove(id: ID) = copy(elements = elements - setOf(id))

    fun update(element: ELEMENT) = copy(elements = elements + mapOf(element.id() to element))

    fun update(updated: List<ELEMENT>) = copy(elements = elements + updated.associateBy { it.id() })

    fun getIds() = elements.keys

    fun getAll() = elements.values

    fun getSize() = elements.size

    fun getType() = nextId.type()

    fun get(id: ID) = elements[id]

    fun getOptional(id: ID?) = if (id != null) {
        elements[id]
    } else {
        null
    }

    fun getOrThrow(id: ID) =
        elements[id] ?: throw IllegalArgumentException("Requires unknown ${getType()} ${id.value()}!")

    fun contains(id: ID) = elements.containsKey(id)

    fun require(id: ID) {
        require(contains(id)) { "Requires unknown ${getType()} ${id.value()}!" }
    }

    fun require(id: ID, message: () -> String) {
        require(contains(id)) { message() }
    }
}
