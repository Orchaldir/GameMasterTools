package at.orchaldir.gm.core.model.ecology

import at.orchaldir.gm.core.model.ecology.plant.PlantId
import at.orchaldir.gm.core.model.util.RarityMap
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class EcologyType {
    Sets,
    Rarity,
    Undefined,
}

@Serializable
sealed class Ecology {

    fun getType() = when (this) {
        is EcologyWithSets -> EcologyType.Sets
        is EcologyWithRarity -> EcologyType.Rarity
        UndefinedEcology -> EcologyType.Undefined
    }

    fun contains(id: PlantId) = when (this) {
        is EcologyWithSets -> plants.contains(id)
        is EcologyWithRarity -> plants.contains(id)
        UndefinedEcology -> false
    }

}

@Serializable
@SerialName("Sets")
data class EcologyWithSets(
    val plants: Set<PlantId> = emptySet(),
) : Ecology()

@Serializable
@SerialName("Rarity")
data class EcologyWithRarity(
    val plants: RarityMap<PlantId>,
) : Ecology()

@Serializable
@SerialName("Undefined")
data object UndefinedEcology : Ecology()