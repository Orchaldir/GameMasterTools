package at.orchaldir.gm.core.model

import at.orchaldir.gm.utils.Id

data class DeleteResult(
    val elements: MutableMap<String, MutableSet<Id<*>>> = mutableMapOf(),
) {
    fun <ID : Id<ID>> addElements(newElements: Set<ID>) {
        val first = newElements.firstOrNull() ?: return

        elements.computeIfAbsent(first.type()) { _ ->
            mutableSetOf()
        }.addAll(newElements)
    }
}