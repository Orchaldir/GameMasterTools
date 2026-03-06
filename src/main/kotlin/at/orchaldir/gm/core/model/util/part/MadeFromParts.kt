package at.orchaldir.gm.core.model.util.part

import at.orchaldir.gm.core.model.economy.material.MaterialId

interface MadeFromParts {

    fun parts(): List<ItemPart> = emptyList()

    fun contains(id: MaterialId): Boolean = parts().any { it.contains(id) }

    fun materials(): Set<MaterialId> {
        val sum: MutableSet<MaterialId> = mutableSetOf()

        parts().forEach { sum.add(it.material()) }

        return sum
    }

    /**
     * The main material of an equipment is relevant for the RPG stats.
     */
    fun mainMaterial(): MaterialId? = null

    fun requiredSchemaColors() = parts()
        .maxOfOrNull { it.requiredSchemaColors() } ?: 0

}