package at.orchaldir.gm.core.model.util.part

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.util.render.*
import kotlinx.serialization.Serializable

interface ItemPart {

    fun contains(id: MaterialId): Boolean

    fun materials(): Set<MaterialId>

    fun requiredSchemaColors() = 0

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

@Serializable
data class ColorSchemeItemPart(
    val material: MaterialId = MaterialId(0),
    val lookup: ColorLookup = LookupMaterial,
) : ItemPart {

    constructor(color: Color) : this(MaterialId(0), FixedColor(color))

    fun getColor(state: State, colors: Colors) = lookup.lookup(state, colors, material)

    override fun contains(id: MaterialId) = material == id
    override fun materials() = setOf(material)
    override fun requiredSchemaColors() = lookup.requiredSchemaColors()

}

@Serializable
data class FillLookupItemPart(
    val material: MaterialId = MaterialId(0),
    val fill: FillLookup = SolidLookup(LookupMaterial),
) : ItemPart {

    constructor(color: Color) : this(fill = SolidLookup(color))

    fun getFill(state: State, colors: Colors) = fill.lookup(state, colors, material)

    override fun contains(id: MaterialId) = material == id
    override fun materials() = setOf(material)
    override fun requiredSchemaColors() = fill.requiredSchemaColors()

}

interface MadeFromParts {

    fun parts(): List<ItemPart> = emptyList()

    fun contains(id: MaterialId): Boolean = parts().any { it.contains(id) }

    fun materials(): Set<MaterialId> {
        val sum: MutableSet<MaterialId> = mutableSetOf()

        parts().forEach { sum.addAll(it.materials()) }

        return sum
    }

    fun mainMaterial(): MaterialId? = null

    fun requiredSchemaColors() = parts()
        .maxOfOrNull { it.requiredSchemaColors() } ?: 0

}