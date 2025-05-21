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
sealed class Origin<T> : Creation {

    fun getType() = when (this) {
        is CombinedOrigin -> OriginType.Combined
        is CreatedOrigin -> OriginType.Created
        is EvolvedOrigin -> OriginType.Evolved
        is ModifiedOrigin -> OriginType.Modified
        is NaturalOrigin -> OriginType.Natural
        is TranslatedOrigin -> OriginType.Translated
        is UndefinedOrigin -> OriginType.Undefined
    }

    fun isChildOf(id: T) = when (this) {
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
data class CombinedOrigin<T>(
    val parents: Set<T>,
    val date: Date? = null,
) : Origin<T>() {

    init {
        require(parents.size >= 2) { "The combined origin needs at least 2 parents!" }
    }

}

@Serializable
@SerialName("Created")
data class CreatedOrigin<T>(
    val creator: Creator,
    val date: Date? = null,
) : Origin<T>()

@Serializable
@SerialName("Evolved")
data class EvolvedOrigin<T>(
    val parent: T,
    val date: Date? = null,
) : Origin<T>()

@Serializable
@SerialName("Modified")
data class ModifiedOrigin<T>(
    val parent: T,
    val modifier: Creator,
    val date: Date? = null,
) : Origin<T>()

@Serializable
@SerialName("Natural")
data class NaturalOrigin<T>(
    val date: Date? = null,
) : Origin<T>()

@Serializable
@SerialName("Translated")
data class TranslatedOrigin<T>(
    val parent: T,
    val translator: Creator,
    val date: Date? = null,
) : Origin<T>()

@Serializable
@SerialName("Undefined")
class UndefinedOrigin<T>() : Origin<T>()
