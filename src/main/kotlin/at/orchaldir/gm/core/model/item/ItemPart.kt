package at.orchaldir.gm.core.model.item

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.util.Color

data class ItemPart(
    val color: Color?,
    val material: MaterialId,
) : MadeFromParts {

    fun getColor(state: State): Color {
        if (color != null) {
            return color
        }

        return state.getMaterialStorage().getOrThrow(material).color
    }

    override fun parts() = emptyList<ItemPart>()
    override fun contains(id: MaterialId) = material == id
    override fun materials() = setOf(material)

}
