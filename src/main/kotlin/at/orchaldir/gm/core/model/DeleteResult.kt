package at.orchaldir.gm.core.model

import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id

data class DeleteResult(
    val id: Id<*>,
    val elements: MutableMap<String, MutableSet<Id<*>>> = mutableMapOf(),
) {
    fun <ID : Id<ID>> addIds(ids: Collection<ID>): DeleteResult {
        val first = ids.firstOrNull() ?: return this

        elements.computeIfAbsent(first.type()) { _ ->
            mutableSetOf()
        }.addAll(ids)

        return this
    }

    fun <ID : Id<ID>> addId(id: ID) = addIds(setOf(id))

    fun <ID : Id<ID>, ELEMENT : Element<ID>> addElements(elements: Collection<ELEMENT>) =
        addIds(elements.map { it.id() })

    fun apply(function: (DeleteResult) -> Unit): DeleteResult {
        function.invoke(this)

        return this
    }

    fun countElements(): Int {
        var count = 0

        elements.forEach { (_, ids) ->
            count += ids.size
        }

        return count
    }

    fun validate() {
        if (elements.isNotEmpty()) {
            throw CannotDeleteException(this)
        }
    }
}