package at.orchaldir.gm.utils

interface Storage<ID, ELEMENT> {

    fun create(f: (ID) -> ELEMENT): ID

    fun getAll(): List<ELEMENT>

    fun get(id: ID): ELEMENT

    fun  delete(id: ID): Boolean

}