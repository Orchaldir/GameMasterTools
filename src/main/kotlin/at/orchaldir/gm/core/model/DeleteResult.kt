package at.orchaldir.gm.core.model

import at.orchaldir.gm.utils.Id

data class DeleteResult(
    val id: Id<*>,
    val elements: MutableMap<String, MutableSet<Id<*>>> = mutableMapOf(),
) {
    fun <ID : Id<ID>> addElements(newElements: Set<ID>) {
        val first = newElements.firstOrNull() ?: return

        elements.computeIfAbsent(first.type()) { _ ->
            mutableSetOf()
        }.addAll(newElements)
    }

    fun countElements(): Int {
        var count = 0

        elements.forEach { (_, ids) ->
            count += ids.size
        }

        return count
    }

    fun validateCanDelete() {
        if (elements.isNotEmpty()) {
            throw CannotDeleteException(this)
        }
    }
}