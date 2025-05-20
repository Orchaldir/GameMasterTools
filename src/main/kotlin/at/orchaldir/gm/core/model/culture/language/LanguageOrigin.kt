package at.orchaldir.gm.core.model.culture.language

import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.Creation
import at.orchaldir.gm.core.model.util.Creator
import at.orchaldir.gm.core.model.util.UndefinedCreator
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class LanguageOriginType {
    Combined,
    Evolved,
    Invented,
    Original,
    Planar,
}

@Serializable
sealed class LanguageOrigin : Creation {

    fun getType() = when (this) {
        is CombinedLanguage -> LanguageOriginType.Combined
        is EvolvedLanguage -> LanguageOriginType.Evolved
        is InventedLanguage -> LanguageOriginType.Invented
        OriginalLanguage -> LanguageOriginType.Original
        PlanarLanguage -> LanguageOriginType.Planar
    }

    fun isChildOf(language: LanguageId) = when (this) {
        is CombinedLanguage -> parents.contains(language)
        is EvolvedLanguage -> parent == language
        else -> false
    }

    override fun creator() = if (this is InventedLanguage) {
        inventor
    } else {
        UndefinedCreator
    }

}

@Serializable
@SerialName("Combined")
data class CombinedLanguage(val parents: Set<LanguageId>) : LanguageOrigin()

@Serializable
@SerialName("Invented")
data class InventedLanguage(
    val inventor: Creator,
    val date: Date,
) : LanguageOrigin()

@Serializable
@SerialName("Evolved")
data class EvolvedLanguage(val parent: LanguageId) : LanguageOrigin()

@Serializable
@SerialName("Original")
data object OriginalLanguage : LanguageOrigin()

@Serializable
@SerialName("Planar")
data object PlanarLanguage : LanguageOrigin()
