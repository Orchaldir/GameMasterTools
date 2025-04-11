package at.orchaldir.gm.core.model.item

import at.orchaldir.gm.core.model.material.MaterialId

interface MadeFromParts {

    fun parts(): List<MadeFromParts> = emptyList<ItemPart>()

    fun contains(id: MaterialId): Boolean = parts().any { it.contains(id) }

    fun materials(): Set<MaterialId> {
        val sum: MutableSet<MaterialId> = mutableSetOf()

        parts().forEach { sum.addAll(it.materials()) }

        return sum
    }

}