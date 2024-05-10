package at.orchaldir.gm.utils

interface Storage<ID, ELEMENT> {

    fun create(f: (ID) -> ELEMENT): ID

    fun getAll(): Collection<ELEMENT>

    fun get(id: ID): ELEMENT?

    fun  delete(id: ID): ELEMENT?

}

data class MapStorage<ID, ELEMENT>(private val elements: MutableMap<ID,ELEMENT> = mutableMapOf()): Storage<ID,ELEMENT> {
    override fun create(f: (ID) -> ELEMENT): ID {
        TODO("Not yet implemented")
    }

    override fun getAll() = elements.values

    override fun get(id: ID) = elements.get(id)

    override fun delete(id: ID) = elements.remove(id)

}