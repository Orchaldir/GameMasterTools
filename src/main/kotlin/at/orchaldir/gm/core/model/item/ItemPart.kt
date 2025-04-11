package at.orchaldir.gm.core.model.item

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.util.Fill
import at.orchaldir.gm.core.model.util.Solid

data class ItemPart(
    val fill: Fill?,
    val material: MaterialId,
) : MadeFromParts {

    fun getFill(state: State): Fill {
        if (fill != null) {
            return fill
        }

        return Solid(state.getMaterialStorage().getOrThrow(material).color)
    }

    override fun parts() = emptyList<ItemPart>()
    override fun contains(id: MaterialId) = material == id
    override fun materials() = setOf(material)

}
