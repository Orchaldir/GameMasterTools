package at.orchaldir.gm.core.model.ecology.plant

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.ecology.plant.appearance.NoTreeSilhouette
import at.orchaldir.gm.core.model.ecology.plant.appearance.TreeSilhouette
import at.orchaldir.gm.core.model.ecology.plant.appearance.Trunk
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.utils.doNothing
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

    fun contains(material: MaterialId) = when (this) {
        is Tree -> wood == material
        UndefinedPlantAppearance -> false
    }

    fun validate(state: State) = when (this) {
        is Tree -> {
            trunk.validate(state)
            silhouette.validate()
            state.getMaterialStorage().requireOptional(wood)
        }
        UndefinedPlantAppearance -> doNothing()
    }

}

@Serializable
@SerialName("Tree")
data class Tree(
    val trunk: Trunk = Trunk(),
    val silhouette: TreeSilhouette = NoTreeSilhouette,
    val wood: MaterialId? = null,
) : PlantAppearance()

@Serializable
@SerialName("Undefined")
data object UndefinedPlantAppearance : PlantAppearance()
