package at.orchaldir.gm.core.model.language

import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.util.Creator
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class LanguageOriginType {
    Combined,
    Evolved,
    Invented,
    Original,
}

@Serializable
sealed class LanguageOrigin

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
