package at.orchaldir.gm.core.model.util.part

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.LeatherGrade
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.util.render.*
import at.orchaldir.gm.core.selector.economy.getMaterialColor
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.HALF
import kotlinx.serialization.Serializable

val BOOK_COVER_MATERIALS = listOf(
    ItemPartType.Fabric,
    ItemPartType.Leather,
    ItemPartType.Paper,
    ItemPartType.Wood,
)
val CLOTHING_MATERIALS = listOf(
    ItemPartType.Fabric,
    ItemPartType.Leather,
)
val FRAGILE_MATERIALS = listOf(
    ItemPartType.Gem,
    ItemPartType.Glass,
    ItemPartType.Metal,
    ItemPartType.Wood,
)
val LINE_MATERIALS = listOf(
    ItemPartType.Cord,
    ItemPartType.Leather,
)
val MADE_FROM_METALS = listOf(
    ItemPartType.Metal,
)
val PAGE_MATERIALS = listOf(
    ItemPartType.Leather,
    ItemPartType.Paper,
)
val SOLID_MATERIALS = listOf(
    ItemPartType.Metal,
    ItemPartType.Wood,
)

enum class ItemPartType {
    Cord,
    Fabric,
    Gem,
    Glass,
    Leather,
    Metal,
    Paper,
    Wood;
}

interface HasColor {

    fun getColor(state: State, colors: Colors): Color

}

@Serializable
sealed class ItemPart : HasColor {

    fun getType() = when (this) {
        is MadeFromCord -> ItemPartType.Cord
        is MadeFromFabric -> ItemPartType.Fabric
        is MadeFromGem -> ItemPartType.Gem
        is MadeFromGlass -> ItemPartType.Glass
        is MadeFromLeather -> ItemPartType.Leather
        is MadeFromMetal -> ItemPartType.Metal
        is MadeFromPaper -> ItemPartType.Paper
        is MadeFromWood -> ItemPartType.Wood
    }

    abstract fun contains(id: MaterialId): Boolean

    abstract fun material(): MaterialId

    override fun getColor(state: State, colors: Colors): Color = error("Unsupported!")

    open fun requiredSchemaColors() = 0
}

interface HasFill {

    fun getFill(state: State, colors: Colors): Fill

}

@Serializable
data class MadeFromCord(
    val material: MaterialId = MaterialId(0),
    val color: ColorLookup = LookupMaterial,
) : ItemPart(), HasColor {

    constructor(color: Color) : this(MaterialId(0), color = FixedColor(color))

    override fun getColor(state: State, colors: Colors) = color.lookup(state, colors, material)

    override fun contains(id: MaterialId) = material == id
    override fun material() = material
    override fun requiredSchemaColors() = color.requiredSchemaColors()

}

@Serializable
data class MadeFromFabric(
    val material: MaterialId = MaterialId(0),
    val weight: FabricWeight = FabricWeight.Medium,
    val type: FabricType = FabricType.Woven,
    val fill: FillLookup = SolidLookup(LookupMaterial),
) : ItemPart(), HasFill {

    constructor(color: Color) : this(MaterialId(0), fill = SolidLookup(color))

    override fun getFill(state: State, colors: Colors) = fill.lookup(state, colors, material)

    override fun contains(id: MaterialId) = material == id
    override fun material() = material
    override fun requiredSchemaColors() = fill.requiredSchemaColors()

}

@Serializable
data class MadeFromGem(
    val material: MaterialId = MaterialId(0),
) : ItemPart(), HasColor {

    override fun getColor(state: State, colors: Colors) = state.getMaterialColor(material)

    override fun contains(id: MaterialId) = material == id
    override fun material() = material

}

@Serializable
data class MadeFromGlass(
    val material: MaterialId = MaterialId(0),
    val color: ColorLookup = LookupMaterial,
    val opacity: Factor = HALF,
) : ItemPart(), HasColor {

    constructor(color: Color) : this(MaterialId(0), color = FixedColor(color))

    override fun getColor(state: State, colors: Colors) = color.lookup(state, colors, material)

    override fun contains(id: MaterialId) = material == id
    override fun material() = material
    override fun requiredSchemaColors() = color.requiredSchemaColors()

}

@Serializable
data class MadeFromLeather(
    val material: MaterialId = MaterialId(0),
    val grade: LeatherGrade = LeatherGrade.Undefined,
    val color: ColorLookup = LookupMaterial,
) : ItemPart(), HasColor {

    constructor(color: Color) : this(MaterialId(0), color = FixedColor(color))

    override fun getColor(state: State, colors: Colors) = color.lookup(state, colors, material)

    override fun contains(id: MaterialId) = material == id
    override fun material() = material
    override fun requiredSchemaColors() = color.requiredSchemaColors()

}

@Serializable
data class MadeFromMetal(
    val material: MaterialId = MaterialId(0),
) : ItemPart(), HasColor {

    override fun getColor(state: State, colors: Colors) = state.getMaterialColor(material)

    override fun contains(id: MaterialId) = material == id
    override fun material() = material

}

@Serializable
data class MadeFromPaper(
    val material: MaterialId = MaterialId(0),
    val color: ColorLookup = LookupMaterial,
) : ItemPart(), HasColor {

    constructor(color: Color) : this(MaterialId(0), color = FixedColor(color))

    override fun getColor(state: State, colors: Colors) = color.lookup(state, colors, material)

    override fun contains(id: MaterialId) = material == id
    override fun material() = material
    override fun requiredSchemaColors() = color.requiredSchemaColors()

}

@Serializable
data class MadeFromWood(
    val material: MaterialId = MaterialId(0),
    val fill: FillLookup = SolidLookup(LookupMaterial),
) : ItemPart(), HasFill {

    constructor(color: Color) : this(MaterialId(0), fill = SolidLookup(color))

    override fun getColor(state: State, colors: Colors) = fill.getColor(state, colors, material)
        ?: error("Not supported by Fill!")

    override fun getFill(state: State, colors: Colors) = fill.lookup(state, colors, material)

    override fun contains(id: MaterialId) = material == id
    override fun material() = material
    override fun requiredSchemaColors() = fill.requiredSchemaColors()

}
