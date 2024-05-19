package at.orchaldir.gm.core.action

import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.language.ComprehensionLevel
import at.orchaldir.gm.core.model.language.Language
import at.orchaldir.gm.core.model.language.LanguageId

sealed class Action

// character actions
data object CreateCharacter : Action()
data class DeleteCharacter(val id: CharacterId) : Action()
data class UpdateCharacter(
    val id: CharacterId,
    val name: String,
    val race: RaceId,
    val gender: Gender,
    val culture: CultureId?,
) : Action()

data class AddLanguage(
    val id: CharacterId,
    val language: LanguageId,
    val level: ComprehensionLevel,
) : Action()

data class RemoveLanguages(
    val id: CharacterId,
    val languages: Set<LanguageId>,
) : Action()

// culture actions
data object CreateCulture : Action()
data class DeleteCulture(val id: CultureId) : Action()
data class UpdateCulture(
    val id: CultureId,
    val name: String,
) : Action()

// language actions
data object CreateLanguage : Action()
data class DeleteLanguage(val id: LanguageId) : Action()
data class UpdateLanguage(val language: Language) : Action()

// personality actions
data object CreatePersonalityTrait : Action()
data class DeletePersonalityTrait(val id: PersonalityTraitId) : Action()
data class UpdatePersonalityTrait(val trait: PersonalityTrait) : Action()

// race actions
data object CreateRace : Action()
data class DeleteRace(val id: RaceId) : Action()
data class UpdateRace(
    val id: RaceId,
    val name: String,
) : Action()
