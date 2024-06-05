package at.orchaldir.gm.core.model

import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.language.Language
import at.orchaldir.gm.core.model.language.LanguageId
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.utils.Storage

data class State(
    val characters: Storage<CharacterId, Character> = Storage(CharacterId(0), "Character"),
    val cultures: Storage<CultureId, Culture> = Storage(CultureId(0), "Culture"),
    val languages: Storage<LanguageId, Language> = Storage(LanguageId(0), "Language"),
    val personalityTraits: Storage<PersonalityTraitId, PersonalityTrait> = Storage(
        PersonalityTraitId(0),
        "Personality Trait"
    ),
    val races: Storage<RaceId, Race> = Storage(RaceId(0), "Race"),
)