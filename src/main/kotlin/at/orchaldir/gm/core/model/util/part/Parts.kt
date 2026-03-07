package at.orchaldir.gm.core.model.util.part

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.LeatherGrade
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.util.part.MadeFromFabric
import at.orchaldir.gm.core.model.util.render.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val CLOTHING_MATERIALS = listOf(
    ItemPartType.Fabric,
    ItemPartType.Leather,
)
val MADE_FROM_METALS = listOf(
    ItemPartType.Metal,
)
val SOLID_MATERIALS = listOf(
    ItemPartType.Metal,
    ItemPartType.Wood,
)

enum class ItemPartType {
    Color,
    ColorScheme,
    Fill,
    FillLookup,
    Fabric,
    Leather,
    Metal,
    Wood;
}

@Serializable
sealed class ItemPart {

    fun getType() = when (this) {
        is ColorItemPart -> ItemPartType.Color
        is ColorSchemeItemPart -> ItemPartType.ColorScheme
        is FillItemPart -> ItemPartType.Fill
        is FillLookupItemPart -> ItemPartType.FillLookup
        is MadeFromFabric -> ItemPartType.Fabric
        is MadeFromLeather -> ItemPartType.Leather
        is MadeFromMetal -> ItemPartType.Metal
        is MadeFromWood -> ItemPartType.Wood
    }

    abstract fun contains(id: MaterialId): Boolean

    abstract fun material(): MaterialId

    open fun requiredSchemaColors() = 0
}

@Serializable
data class ColorItemPart(
    val material: MaterialId = MaterialId(0),
    val color: Color? = null,
) : ItemPart() {

    constructor(color: Color) : this(MaterialId(0), color)

    fun getColor(state: State): Color {
        if (color != null) {
            return color
        }

        return state.getMaterialStorage().get(material)?.properties?.color ?: Color.Pink
    }

    override fun contains(id: MaterialId) = material == id
    override fun material() = material

}

@Serializable
data class FillItemPart(
    val material: MaterialId = MaterialId(0),
    val fill: Fill? = null,
) : ItemPart() {

    constructor(color: Color) : this(fill = Solid(color))

    fun getFill(state: State): Fill {
        if (fill != null) {
            return fill
        }

        return Solid(state.getMaterialStorage().get(material)?.properties?.color ?: Color.Pink)
    }

    override fun contains(id: MaterialId) = material == id
    override fun material() = material

}

@Serializable
data class ColorSchemeItemPart(
    val material: MaterialId = MaterialId(0),
    val lookup: ColorLookup = LookupMaterial,
) : ItemPart() {

    constructor(color: Color) : this(MaterialId(0), FixedColor(color))

    fun getColor(state: State, colors: Colors) = lookup.lookup(state, colors, material)

    override fun contains(id: MaterialId) = material == id
    override fun material() = material
    override fun requiredSchemaColors() = lookup.requiredSchemaColors()

}

@Serializable
data class FillLookupItemPart(
    val material: MaterialId = MaterialId(0),
    val fill: FillLookup = SolidLookup(LookupMaterial),
) : ItemPart() {

    constructor(color: Color) : this(fill = SolidLookup(color))

    fun getFill(state: State, colors: Colors) = fill.lookup(state, colors, material)

    override fun contains(id: MaterialId) = material == id
    override fun material() = material
    override fun requiredSchemaColors() = fill.requiredSchemaColors()

}

@Serializable
data class MadeFromFabric(
    val material: MaterialId = MaterialId(0),
    val weight: FabricWeight = FabricWeight.Medium,
    val type: FabricType = FabricType.Woven,
    val fill: FillLookup = SolidLookup(LookupMaterial),
) : ItemPart() {

    constructor(color: Color) : this(MaterialId(0), fill = SolidLookup(color))

    fun getFill(state: State, colors: Colors) = fill.lookup(state, colors, material)

    override fun contains(id: MaterialId) = material == id
    override fun material() = material
    override fun requiredSchemaColors() = fill.requiredSchemaColors()

}

@Serializable
data class MadeFromLeather(
    val material: MaterialId = MaterialId(0),
    val grade: LeatherGrade = LeatherGrade.Undefined,
    val color: ColorLookup = LookupMaterial,
) : ItemPart() {

    constructor(color: Color) : this(MaterialId(0), color =  FixedColor(color))

    fun getColor(state: State, colors: Colors) = color.lookup(state, colors, material)

    override fun contains(id: MaterialId) = material == id
    override fun material() = material
    override fun requiredSchemaColors() = color.requiredSchemaColors()

}

@Serializable
data class MadeFromMetal(
    val material: MaterialId = MaterialId(0),
    val color: ColorLookup = LookupMaterial,
) : ItemPart() {

    constructor(color: Color) : this(MaterialId(0), color =  FixedColor(color))

    fun getColor(state: State, colors: Colors) = color.lookup(state, colors, material)

    override fun contains(id: MaterialId) = material == id
    override fun material() = material
    override fun requiredSchemaColors() = color.requiredSchemaColors()

}

@Serializable
data class MadeFromWood(
    val material: MaterialId = MaterialId(0),
    val fill: FillLookup = SolidLookup(LookupMaterial),
) : ItemPart() {

    constructor(color: Color) : this(MaterialId(0), fill = SolidLookup(color))

    fun getFill(state: State, colors: Colors) = fill.lookup(state, colors, material)

    override fun contains(id: MaterialId) = material == id
    override fun material() = material
    override fun requiredSchemaColors() = fill.requiredSchemaColors()

}
