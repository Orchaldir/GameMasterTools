package at.orchaldir.gm.utils

interface Id<ID> {
    fun next(): ID
    fun value(): Int
}

interface Element<ID> {
    fun id(): ID
    fun name(): String
}

data class Storage<ID : Id<ID>, ELEMENT : Element<ID>>(
    private val name: String,
    private val elements: Map<ID, ELEMENT>,
    val nextId: ID,
    val lastId: ID = nextId,
) {
    constructor(nextId: ID, name: String = "Element") : this(name, mapOf(), nextId)

    constructor(elements: List<ELEMENT>, name: String = "Element") : this(
        name,
        elements.associateBy { it.id() },
        elements.map { it.id() }.last()
    )

    fun add(element: ELEMENT): Storage<ID, ELEMENT> {
        return Storage(name, elements + mapOf(nextId to element), nextId.next(), nextId)
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

    fun getOrThrow(id: ID) = elements[id] ?: throw IllegalArgumentException("Unknown $name ${id.value()}!")

    fun contains(id: ID) = elements.containsKey(id)

    fun require(id: ID) {
        require(contains(id)) { "Requires unknown $name ${id.value()}!" }
    }
}
