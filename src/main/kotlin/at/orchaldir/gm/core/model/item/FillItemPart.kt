package at.orchaldir.gm.core.model.item

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Fill
import at.orchaldir.gm.core.model.util.Solid
import kotlinx.serialization.Serializable

@Serializable
data class ColorItemPart(
    val color: Color? = null,
    val material: MaterialId = MaterialId(0),
) : MadeFromParts {

    fun getColor(state: State): Color {
        if (color != null) {
            return color
        }

        return state.getMaterialStorage().get(material)?.color ?: Color.Pink
    }

    override fun contains(id: MaterialId) = material == id
    override fun materials() = setOf(material)

}

@Serializable
data class FillItemPart(
    val fill: Fill? = null,
    val material: MaterialId = MaterialId(0),
) : MadeFromParts {

    constructor(color: Color) : this(Solid(color))

    fun getFill(state: State): Fill {
        if (fill != null) {
            return fill
        }

        return Solid(state.getMaterialStorage().get(material)?.color ?: Color.Pink)
    }

    override fun contains(id: MaterialId) = material == id
    override fun materials() = setOf(material)

}
