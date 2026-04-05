package at.orchaldir.gm.core.model.ecology.plant

import at.orchaldir.gm.core.model.economy.material.MaterialId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class PlantAppearanceType {
    Tree,
    Undefined,
}

@Serializable
sealed class PlantAppearance {

    fun getType() = when (this) {
        is Tree -> PlantAppearanceType.Tree
        UndefinedPlantAppearance -> PlantAppearanceType.Undefined
    }

}

@Serializable
@SerialName("Tree")
data class Tree(
    val wood: MaterialId? = null,
) : PlantAppearance()

@Serializable
@SerialName("Undefined")
data object UndefinedPlantAppearance : PlantAppearance()
