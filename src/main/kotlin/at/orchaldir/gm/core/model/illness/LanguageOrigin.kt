package at.orchaldir.gm.core.model.illness

import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.Creation
import at.orchaldir.gm.core.model.util.Creator
import at.orchaldir.gm.core.model.util.UndefinedCreator
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class IllnessOriginType {
    Evolved,
    Invented,
    Modified,
    Natural,
}

@Serializable
sealed class IllnessOrigin : Creation {

    fun getType() = when (this) {
        is InventedIllness -> IllnessOriginType.Invented
        is EvolvedIllness -> IllnessOriginType.Evolved
        is ModifiedIllness -> IllnessOriginType.Modified
        NaturalIllness -> IllnessOriginType.Natural
    }

    fun isChildOf(illness: IllnessId) = when (this) {
        is EvolvedIllness -> parent == illness
        is ModifiedIllness -> parent == illness
        else -> false
    }

    override fun creator() = when (this) {
        is InventedIllness -> inventor
        is ModifiedIllness -> modifier
        else -> UndefinedCreator
    }

}

@Serializable
@SerialName("Evolved")
data class EvolvedIllness(
    val parent: IllnessId,
    val date: Date?,
) : IllnessOrigin()

@Serializable
@SerialName("Invented")
data class InventedIllness(
    val inventor: Creator,
    val date: Date?,
) : IllnessOrigin()

@Serializable
@SerialName("Modified")
data class ModifiedIllness(
    val parent: IllnessId,
    val modifier: Creator,
    val date: Date?,
) : IllnessOrigin()

@Serializable
@SerialName("Natural")
data object NaturalIllness : IllnessOrigin()
