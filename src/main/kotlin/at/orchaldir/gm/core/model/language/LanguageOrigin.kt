package at.orchaldir.gm.core.model.language

import at.orchaldir.gm.core.model.character.CharacterId
import kotlinx.serialization.Serializable

@Serializable
sealed class LanguageOrigin
data class InventedLanguage(val inventor: CharacterId) : LanguageOrigin()
data class EvolvedLanguage(val parent: LanguageId) : LanguageOrigin()
data object OriginalLanguage : LanguageOrigin()
