package at.orchaldir.gm.core.model

import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.language.Language
import at.orchaldir.gm.core.model.language.LanguageId
import at.orchaldir.gm.utils.Storage

data class State(
    val characters: Storage<CharacterId, Character>,
    val cultures: Storage<CultureId, Culture>,
    val languages: Storage<LanguageId, Language>,
    val races: Storage<RaceId, Race>,
)