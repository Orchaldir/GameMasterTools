package at.orchaldir.gm.core.model.economy.material

import at.orchaldir.gm.core.model.util.PercentageDistribution
import at.orchaldir.gm.core.model.util.Size
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class MaterialCategoryType {
    Undefined,
    Alloy,
    Fiber,
    Metal;
}

@Serializable
sealed class MaterialCategory {

    fun getType() = when (this) {
        is Alloy -> MaterialCategoryType.Alloy
        is Fiber -> MaterialCategoryType.Fiber
        is Metal -> MaterialCategoryType.Metal
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
@SerialName("Metal")
data object Metal : MaterialCategory()

@Serializable
@SerialName("Undefined")
data object UndefinedMaterialCategory : MaterialCategory()

