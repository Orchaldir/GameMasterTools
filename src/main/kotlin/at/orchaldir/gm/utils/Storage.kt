package at.orchaldir.gm.utils

interface Id<ID> {
    fun next(): ID
}

data class Storage<ID : Id<ID>, ELEMENT>(val elements: Map<ID, ELEMENT>, val nextId: ID) {

    fun add(element: ELEMENT): Storage<ID, ELEMENT> {
        return Storage(elements + mapOf(nextId to element), nextId.next())
    }

    fun getAll() = elements.values

    fun getSize() = elements.size

    fun get(id: ID) = elements[id]
}
