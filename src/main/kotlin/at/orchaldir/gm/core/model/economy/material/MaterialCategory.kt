package at.orchaldir.gm.core.model.economy.material

import at.orchaldir.gm.core.model.character.appearance.hair.HairColor
import at.orchaldir.gm.core.model.character.appearance.hair.NormalHairColor
import at.orchaldir.gm.core.model.character.appearance.hair.NormalHairColorEnum
import at.orchaldir.gm.core.model.race.appearance.HairColorOptions
import at.orchaldir.gm.core.model.race.appearance.HairOptions
import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.core.model.util.PercentageDistribution
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.render.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val ALLOYS_OR_METALS = setOf(MaterialCategoryType.Alloy, MaterialCategoryType.Metal)
val CATEGORIES_FOR_ALLOY = setOf(MaterialCategoryType.Metal)
val CATEGORIES_FOR_CLOTHING = setOf(MaterialCategoryType.Fiber, MaterialCategoryType.Leather)
val CATEGORIES_FOR_GEM = setOf(MaterialCategoryType.Mineral)
val CATEGORIES_FOR_ROCK = setOf(MaterialCategoryType.Mineral)

enum class MaterialCategoryType {
    Undefined,
    Alloy,
    Fiber,
    Fur,
    Glass,
    Hide,
    Leather,
    Metal,
    Mineral,
    Paper,
    Rock,
    Wood;
}

@Serializable
sealed class MaterialCategory {

    fun getType() = when (this) {
        is Alloy -> MaterialCategoryType.Alloy
        is Fiber -> MaterialCategoryType.Fiber
        is Fur -> MaterialCategoryType.Fur
        is Glass -> MaterialCategoryType.Glass
        is Hide -> MaterialCategoryType.Hide
        is Leather -> MaterialCategoryType.Leather
        is Metal -> MaterialCategoryType.Metal
        is Mineral -> MaterialCategoryType.Mineral
        is Paper -> MaterialCategoryType.Paper
        is Rock -> MaterialCategoryType.Rock
        is Wood -> MaterialCategoryType.Wood
        is UndefinedMaterialCategory -> MaterialCategoryType.Undefined
    }

    fun contains(material: MaterialId) = when (this) {
        is Alloy -> components.map.containsKey(material)
        is Leather -> hide == material
        is Rock -> components.contains(material)
        else -> false
    }
    
    fun getMostCommonColor() = when (this) {
        is Alloy -> color
        is Fiber -> color
        is Fur -> null
        is Glass -> color
        is Hide -> color
        is Leather -> color
        is Metal -> color
        is Mineral -> colors.getMostCommon()
        is Paper -> color
        is Rock -> colors.getMostCommon()
        is Wood -> color
        UndefinedMaterialCategory -> null
    }
}

@Serializable
@SerialName("Alloy")
data class Alloy(
    val color: Color,
    val components: PercentageDistribution<MaterialId>,
) : MaterialCategory()

@Serializable
@SerialName("Fiber")
data class Fiber(
    val color: Color,
    val weight: Size = Size.Medium,
) : MaterialCategory()

@Serializable
@SerialName("Fur")
data class Fur(
    val colors: HairColorOptions,
    val thickness: LeatherThickness = LeatherThickness.Medium,
) : MaterialCategory()

@Serializable
@SerialName("Glass")
data class Glass(
    val color: Color,
) : MaterialCategory()

@Serializable
@SerialName("Hide")
data class Hide(
    val color: Color,
    val thickness: LeatherThickness = LeatherThickness.Medium,
) : MaterialCategory()

@Serializable
@SerialName("Leather")
data class Leather(
    val color: Color,
    val hide: MaterialId? = null,
    val grade: LeatherGrade = LeatherGrade.Undefined,
    val thickness: LeatherThickness = LeatherThickness.Medium,
) : MaterialCategory()

@Serializable
@SerialName("Metal")
data class Metal(
    val color: Color = Color.Gray,
) : MaterialCategory()

@Serializable
@SerialName("Mineral")
data class Mineral(
    val colors: OneOf<Color>,
) : MaterialCategory()

@Serializable
@SerialName("Paper")
data class Paper(
    val color: Color = Color.White,
) : MaterialCategory()

@Serializable
@SerialName("Rock")
data class Rock(
    val colors: OneOf<Color>,
    val components: Set<MaterialId>,
    val type: RockType = RockType.Undefined,
) : MaterialCategory()

@Serializable
@SerialName("Wood")
data class Wood(
    val color: Color,
) : MaterialCategory()

@Serializable
@SerialName("Undefined")
data object UndefinedMaterialCategory : MaterialCategory()

