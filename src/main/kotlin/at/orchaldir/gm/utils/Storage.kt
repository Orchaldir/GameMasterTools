package at.orchaldir.gm.utils

interface Id<ID> {
    fun next(): ID
}

data class Storage<ID : Id<ID>, ELEMENT>(val elements: Map<ID, ELEMENT>, val nextId: ID, val lastId: ID = nextId) {

    fun add(element: ELEMENT): Storage<ID, ELEMENT> {
        return Storage(elements + mapOf(nextId to element), nextId.next(), nextId)
    }

    fun remove(id: ID): Storage<ID, ELEMENT> {
        return Storage(elements - setOf(id), nextId, lastId)
    }

    fun getAll() = elements.values

    fun getSize() = elements.size

    fun get(id: ID) = elements[id]
}
