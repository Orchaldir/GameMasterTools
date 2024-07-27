package at.orchaldir.gm.core.model

import at.orchaldir.gm.core.generator.RarityGenerator
import at.orchaldir.gm.core.loadStorage
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.character.PersonalityTrait
import at.orchaldir.gm.core.model.character.PersonalityTraitId
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.fashion.Fashion
import at.orchaldir.gm.core.model.fashion.FashionId
import at.orchaldir.gm.core.model.item.ItemTemplate
import at.orchaldir.gm.core.model.item.ItemTemplateId
import at.orchaldir.gm.core.model.language.Language
import at.orchaldir.gm.core.model.language.LanguageId
import at.orchaldir.gm.core.model.material.Material
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.saveStorage
import at.orchaldir.gm.utils.Storage

private const val CHARACTER = "Character"
private const val CULTURE = "Culture"
private const val FASHION = "Fashion"
private const val ITEM_TEMPLATE = "Item Template"
private const val ITEM = "Item"
private const val LANGUAGE = "Language"
private const val MATERIAL = "Material"
private const val NAME_LIST = "Name List"
private const val PERSONALITY_TRAIT = "Personality Trait"
private const val RACE = "Race"

data class State(
    val path: String = "data",
    val characters: Storage<CharacterId, Character> = Storage(CharacterId(0), CHARACTER),
    val cultures: Storage<CultureId, Culture> = Storage(CultureId(0), CULTURE),
    val fashion: Storage<FashionId, Fashion> = Storage(FashionId(0), FASHION),
    val itemTemplates: Storage<ItemTemplateId, ItemTemplate> = Storage(ItemTemplateId(0), ITEM_TEMPLATE),
    val languages: Storage<LanguageId, Language> = Storage(LanguageId(0), LANGUAGE),
    val materials: Storage<MaterialId, Material> = Storage(MaterialId(0), MATERIAL),
    val nameLists: Storage<NameListId, NameList> = Storage(NameListId(0), NAME_LIST),
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
            loadStorage(path, CHARACTER, CharacterId(0)),
            loadStorage(path, CULTURE, CultureId(0)),
            loadStorage(path, FASHION, FashionId(0)),
            loadStorage(path, ITEM_TEMPLATE, ItemTemplateId(0)),
            loadStorage(path, LANGUAGE, LanguageId(0)),
            loadStorage(path, MATERIAL, MaterialId(0)),
            loadStorage(path, NAME_LIST, NameListId(0)),
            loadStorage(path, PERSONALITY_TRAIT, PersonalityTraitId(0)),
            loadStorage(path, RACE, RaceId(0)),
        )
    }

    fun save() {
        saveStorage(path, characters)
        saveStorage(path, cultures)
        saveStorage(path, fashion)
        saveStorage(path, itemTemplates)
        saveStorage(path, languages)
        saveStorage(path, materials)
        saveStorage(path, nameLists)
        saveStorage(path, personalityTraits)
        saveStorage(path, races)
    }
}