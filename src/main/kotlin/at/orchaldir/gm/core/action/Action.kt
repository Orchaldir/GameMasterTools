package at.orchaldir.gm.core.action

import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.character.CultureId
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.character.RaceId

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

// race actions
data object CreateRace : Action()
data class DeleteRace(val id: RaceId) : Action()
data class UpdateRace(
    val id: RaceId,
    val name: String,
) : Action()

// culture actions
data object CreateCulture : Action()
data class DeleteCulture(val id: CultureId) : Action()
data class UpdateCulture(
    val id: CultureId,
    val name: String,
) : Action()