package at.orchaldir.gm.utils

interface Id<ID> {
    fun next(): ID
}

interface Element<ID> {
    fun id(): ID
}

data class Storage<ID : Id<ID>, ELEMENT : Element<ID>>(
    val elements: Map<ID, ELEMENT>,
    val nextId: ID,
    val lastId: ID = nextId
) {
    constructor(nextId: ID) : this(mapOf(), nextId)

    constructor(elements: List<ELEMENT>) : this(elements.associateBy { it.id() }, elements.map { it.id() }.last())

    fun add(element: ELEMENT): Storage<ID, ELEMENT> {
        return Storage(elements + mapOf(nextId to element), nextId.next(), nextId)
    }

    fun remove(id: ID): Storage<ID, ELEMENT> {
        return copy(elements = elements - setOf(id))
    }

    fun update(element: ELEMENT): Storage<ID, ELEMENT> {
        return copy(elements = elements + mapOf(element.id() to element))
    }

    fun getAll() = elements.values

    fun getSize() = elements.size

    fun get(id: ID) = elements[id]
}
