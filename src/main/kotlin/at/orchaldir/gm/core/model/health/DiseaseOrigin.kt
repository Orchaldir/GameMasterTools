package at.orchaldir.gm.core.model.health

import at.orchaldir.gm.core.model.util.Creation
import at.orchaldir.gm.core.model.util.Creator
import at.orchaldir.gm.core.model.util.UndefinedCreator
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class DiseaseOriginType {
    Created,
    Evolved,
    Modified,
    Original,
    Undefined,
}

@Serializable
sealed class DiseaseOrigin : Creation {

    fun getType() = when (this) {
        is CreatedDisease -> DiseaseOriginType.Created
        is EvolvedDisease -> DiseaseOriginType.Evolved
        is ModifiedDisease -> DiseaseOriginType.Modified
        OriginalDisease -> DiseaseOriginType.Original
        UndefinedDiseaseOrigin -> DiseaseOriginType.Undefined
    }

    fun isChildOf(race: DiseaseId) = when (this) {
        is EvolvedDisease -> parent == race
        is ModifiedDisease -> parent == race
        else -> false
    }

    override fun creator() = when (this) {
        is CreatedDisease -> creator
        is ModifiedDisease -> modifier
        else -> UndefinedCreator
    }

}

@Serializable
@SerialName("Created")
data class CreatedDisease(
    val creator: Creator,
) : DiseaseOrigin()

@Serializable
@SerialName("Evolved")
data class EvolvedDisease(val parent: DiseaseId) : DiseaseOrigin()

@Serializable
@SerialName("Modified")
data class ModifiedDisease(
    val parent: DiseaseId,
    val modifier: Creator,
) : DiseaseOrigin()

@Serializable
@SerialName("Original")
data object OriginalDisease : DiseaseOrigin()

@Serializable
@SerialName("Undefined")
data object UndefinedDiseaseOrigin : DiseaseOrigin()
