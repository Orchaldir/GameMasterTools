package at.orchaldir.gm.core.model.util

import at.orchaldir.gm.core.model.BaseId
import at.orchaldir.gm.core.model.time.date.Date
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class OriginType {
    Combined,
    Created,
    Evolved,
    Modified,
    Natural,
    Translated,
    Undefined,
}

@Serializable
sealed class Origin<ID : BaseId<ID>> : Creation {

    fun getType() = when (this) {
        is CombinedOrigin -> OriginType.Combined
        is CreatedOrigin -> OriginType.Created
        is EvolvedOrigin -> OriginType.Evolved
        is ModifiedOrigin -> OriginType.Modified
        is NaturalOrigin -> OriginType.Natural
        is TranslatedOrigin -> OriginType.Translated
        is UndefinedOrigin -> OriginType.Undefined
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
        is UndefinedOrigin -> null
    }

}

@Serializable
@SerialName("Combined")
data class CombinedOrigin<ID : BaseId<ID>>(
    val parents: Set<ID>,
    val date: Date? = null,
) : Origin<ID>() {

    init {
        require(parents.size >= 2) { "The combined origin needs at least 2 parents!" }
    }

}

@Serializable
@SerialName("Created")
data class CreatedOrigin<ID : BaseId<ID>>(
    val creator: Creator,
    val date: Date? = null,
) : Origin<ID>()

@Serializable
@SerialName("Evolved")
data class EvolvedOrigin<ID : BaseId<ID>>(
    val parent: ID,
    val date: Date? = null,
) : Origin<ID>()

@Serializable
@SerialName("Modified")
data class ModifiedOrigin<ID : BaseId<ID>>(
    val parent: ID,
    val modifier: Creator,
    val date: Date? = null,
) : Origin<ID>()

@Serializable
@SerialName("Natural")
data class NaturalOrigin<ID : BaseId<ID>>(
    val date: Date? = null,
) : Origin<ID>()

@Serializable
@SerialName("Translated")
data class TranslatedOrigin<ID : BaseId<ID>>(
    val parent: ID,
    val translator: Creator,
    val date: Date? = null,
) : Origin<ID>()

@Serializable
@SerialName("Undefined")
class UndefinedOrigin<ID : BaseId<ID>>() : Origin<ID>()
