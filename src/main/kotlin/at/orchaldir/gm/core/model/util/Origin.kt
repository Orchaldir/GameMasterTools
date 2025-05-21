package at.orchaldir.gm.core.model.util

import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class OriginType {
    Combined,
    Created,
    Evolved,
    Modified,
    Natural,
    Translated,
}

@Serializable
sealed class Origin<ID : Id<ID>> : Creation {

    fun getType() = when (this) {
        is CombinedOrigin -> OriginType.Combined
        is CreatedOrigin -> OriginType.Created
        is EvolvedOrigin -> OriginType.Evolved
        is ModifiedOrigin -> OriginType.Modified
        is NaturalOrigin -> OriginType.Natural
        is TranslatedOrigin -> OriginType.Translated
    }

    fun isChildOf(id: ID) = when (this) {
        is CombinedOrigin -> parents.contains(id)
        is EvolvedOrigin -> parent == id
        is ModifiedOrigin -> parent == id
        is TranslatedOrigin -> parent == id
        else -> false
    }

    override fun creator() = when (this) {
        is CreatedOrigin -> creator
        is ModifiedOrigin -> modifier
        is TranslatedOrigin -> translator
        else -> UndefinedCreator
    }

    fun date() = when (this) {
        is CombinedOrigin -> date
        is CreatedOrigin -> date
        is EvolvedOrigin -> date
        is ModifiedOrigin -> date
        is NaturalOrigin -> date
        is TranslatedOrigin -> date
    }

}

@Serializable
@SerialName("Combined")
data class CombinedOrigin<ID : Id<ID>>(
    val parents: Set<ID>,
    val date: Date? = null,
) : Origin<ID>() {

    init {
        require(parents.size >= 2) { "The combined origin needs at least 2 parents!" }
    }

}

@Serializable
@SerialName("Created")
data class CreatedOrigin<ID : Id<ID>>(
    val creator: Creator,
    val date: Date? = null,
) : Origin<ID>()

@Serializable
@SerialName("Evolved")
data class EvolvedOrigin<ID : Id<ID>>(
    val parent: ID,
    val date: Date? = null,
) : Origin<ID>()

@Serializable
@SerialName("Modified")
data class ModifiedOrigin<ID : Id<ID>>(
    val parent: ID,
    val modifier: Creator,
    val date: Date? = null,
) : Origin<ID>()

@Serializable
@SerialName("Natural")
data class NaturalOrigin<ID : Id<ID>>(
    val date: Date? = null,
) : Origin<ID>()

@Serializable
@SerialName("Translated")
data class TranslatedOrigin<ID : Id<ID>>(
    val parent: ID,
    val translator: Creator,
    val date: Date? = null,
) : Origin<ID>()
