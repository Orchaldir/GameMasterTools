package at.orchaldir.gm.core.model.economy.material

import at.orchaldir.gm.core.model.util.PercentageDistribution
import at.orchaldir.gm.core.model.util.Size
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class MaterialCategoryType {
    Undefined,
    Alloy,
    Fiber,
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
        is Hide -> MaterialCategoryType.Hide
        is Leather -> MaterialCategoryType.Leather
        is Metal -> MaterialCategoryType.Metal
        is Mineral -> MaterialCategoryType.Mineral
        is Paper -> MaterialCategoryType.Paper
        is Rock -> MaterialCategoryType.Rock
        is Wood -> MaterialCategoryType.Wood
        is UndefinedMaterialCategory -> MaterialCategoryType.Undefined
    }
}

@Serializable
@SerialName("Alloy")
data class Alloy(
    val components: PercentageDistribution<MaterialId>,
) : MaterialCategory()

@Serializable
@SerialName("Fiber")
data class Fiber(
    val components: PercentageDistribution<MaterialId>,
    val weight: Size = Size.Medium,
) : MaterialCategory()

@Serializable
@SerialName("Hide")
data class Hide(
    val components: PercentageDistribution<MaterialId>,
    val thickness: LeatherThickness = LeatherThickness.Medium,
) : MaterialCategory()

@Serializable
@SerialName("Leather")
data class Leather(
    val hide: MaterialId? = null,
    val grade: LeatherGrade = LeatherGrade.Undefined,
    val thickness: LeatherThickness = LeatherThickness.Medium,
) : MaterialCategory()

@Serializable
@SerialName("Metal")
data object Metal : MaterialCategory()

@Serializable
@SerialName("Mineral")
data class Mineral(
    val components: PercentageDistribution<MaterialId>,
) : MaterialCategory()

@Serializable
@SerialName("Paper")
data object Paper : MaterialCategory()

@Serializable
@SerialName("Rock")
data class Rock(
    val components: PercentageDistribution<MaterialId>,
    val type: RockType = RockType.Undefined,
) : MaterialCategory()

@Serializable
@SerialName("Wood")
data object Wood : MaterialCategory()

@Serializable
@SerialName("Undefined")
data object UndefinedMaterialCategory : MaterialCategory()

