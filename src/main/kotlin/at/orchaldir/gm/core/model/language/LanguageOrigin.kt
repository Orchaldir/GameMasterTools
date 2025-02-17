package at.orchaldir.gm.core.model.language

import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.util.Creator
import at.orchaldir.gm.core.model.world.plane.PlaneId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class LanguageOriginType {
    Combined,
    Cosmic,
    Evolved,
    Invented,
    Original,
    Planar,
}

@Serializable
sealed class LanguageOrigin {

    fun getType() = when (this) {
        is CombinedLanguage -> LanguageOriginType.Combined
        is CosmicLanguage -> LanguageOriginType.Cosmic
        is EvolvedLanguage -> LanguageOriginType.Evolved
        is InventedLanguage -> LanguageOriginType.Invented
        OriginalLanguage -> LanguageOriginType.Original
        is PlanarLanguage -> LanguageOriginType.Planar
    }

    fun isChildOf(language: LanguageId) = when (this) {
        is CombinedLanguage -> parents.contains(language)
        is EvolvedLanguage -> parent == language
        else -> false
    }

}

@Serializable
@SerialName("Combined")
data class CombinedLanguage(val parents: Set<LanguageId>) : LanguageOrigin()

@Serializable
@SerialName("Cosmic")
data object CosmicLanguage : LanguageOrigin()

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
data class PlanarLanguage(val plane: PlaneId) : LanguageOrigin()
