package at.orchaldir.gm.core.model

import at.orchaldir.gm.core.generator.RarityGenerator
import at.orchaldir.gm.core.loadStorage
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.language.Language
import at.orchaldir.gm.core.model.language.LanguageId
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.saveStorage
import at.orchaldir.gm.utils.Storage

private const val CHARACTER = "Character"
private const val CULTURE = "Culture"
private const val LANGUAGE = "Language"
private const val PERSONALITY_TRAIT = "Personality Trait"
private const val RACE = "Race"

data class State(
    val path: String,
    val characters: Storage<CharacterId, Character> = Storage(CharacterId(0), CHARACTER),
    val cultures: Storage<CultureId, Culture> = Storage(CultureId(0), CULTURE),
    val languages: Storage<LanguageId, Language> = Storage(LanguageId(0), LANGUAGE),
    val personalityTraits: Storage<PersonalityTraitId, PersonalityTrait> = Storage(
        PersonalityTraitId(0),
        PERSONALITY_TRAIT
    ),
    val races: Storage<RaceId, Race> = Storage(RaceId(0), RACE),
    val rarityGenerator: RarityGenerator = RarityGenerator.empty(),
) {
    companion object {
        fun load(path: String) = State(
            path,
            loadStorage(path, CHARACTER),
            loadStorage(path, CULTURE),
            loadStorage(path, LANGUAGE),
            loadStorage(path, PERSONALITY_TRAIT),
            loadStorage(path, RACE),
        )
    }

    fun save() {
        saveStorage(path, characters)
        saveStorage(path, cultures)
        saveStorage(path, languages)
        saveStorage(path, personalityTraits)
        saveStorage(path, races)
    }
}