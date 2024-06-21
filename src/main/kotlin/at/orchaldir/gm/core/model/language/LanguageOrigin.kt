package at.orchaldir.gm.core.model.language

import at.orchaldir.gm.core.model.character.CharacterId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class LanguageOrigin

@Serializable
@SerialName("Combined")
data class CombinedLanguage(val parents: List<LanguageId>) : LanguageOrigin()

@Serializable
@SerialName("Invented")
data class InventedLanguage(val inventor: CharacterId) : LanguageOrigin()

@Serializable
@SerialName("Evolved")
data class EvolvedLanguage(val parent: LanguageId) : LanguageOrigin()

@Serializable
@SerialName("Original")
data object OriginalLanguage : LanguageOrigin()
