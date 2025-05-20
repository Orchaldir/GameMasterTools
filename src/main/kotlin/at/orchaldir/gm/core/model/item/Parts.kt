package at.orchaldir.gm.core.model.item

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Fill
import at.orchaldir.gm.core.model.util.Solid
import kotlinx.serialization.Serializable

interface ItemPart {

    fun contains(id: MaterialId): Boolean

    fun materials(): Set<MaterialId>

}

@Serializable
data class ColorItemPart(
    val material: MaterialId = MaterialId(0),
    val color: Color? = null,
) : ItemPart {

    constructor(color: Color) : this(MaterialId(0), color)

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
    val material: MaterialId = MaterialId(0),
    val fill: Fill? = null,
) : ItemPart {

    constructor(color: Color) : this(fill = Solid(color))

    fun getFill(state: State): Fill {
        if (fill != null) {
            return fill
        }

        return Solid(state.getMaterialStorage().get(material)?.color ?: Color.Pink)
    }

    override fun contains(id: MaterialId) = material == id
    override fun materials() = setOf(material)

}

interface MadeFromParts {

    fun parts(): List<ItemPart> = emptyList()

    fun contains(id: MaterialId): Boolean = parts().any { it.contains(id) }

    fun materials(): Set<MaterialId> {
        val sum: MutableSet<MaterialId> = mutableSetOf()

        parts().forEach { sum.addAll(it.materials()) }

        return sum
    }

}