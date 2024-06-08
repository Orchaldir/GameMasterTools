package at.orchaldir.gm.core.model.language

import at.orchaldir.gm.core.model.character.CharacterId
import kotlinx.serialization.Serializable

@Serializable
sealed class LanguageOrigin

@Serializable
data class InventedLanguage(val inventor: CharacterId) : LanguageOrigin()

@Serializable
data class EvolvedLanguage(val parent: LanguageId) : LanguageOrigin()

@Serializable
data object OriginalLanguage : LanguageOrigin()
