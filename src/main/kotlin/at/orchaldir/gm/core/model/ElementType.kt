package at.orchaldir.gm.core.model

import at.orchaldir.gm.core.loadStorage
import at.orchaldir.gm.core.model.calendar.CALENDAR
import at.orchaldir.gm.core.model.calendar.Calendar
import at.orchaldir.gm.core.model.calendar.CalendarId
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.culture.CULTURE
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.fashion.FASHION
import at.orchaldir.gm.core.model.fashion.Fashion
import at.orchaldir.gm.core.model.fashion.FashionId
import at.orchaldir.gm.core.model.item.ITEM_TEMPLATE
import at.orchaldir.gm.core.model.item.ItemTemplate
import at.orchaldir.gm.core.model.item.ItemTemplateId
import at.orchaldir.gm.core.model.language.LANGUAGE
import at.orchaldir.gm.core.model.language.Language
import at.orchaldir.gm.core.model.language.LanguageId
import at.orchaldir.gm.core.model.material.MATERIAL
import at.orchaldir.gm.core.model.material.Material
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.race.RACE
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.utils.Storage

val ELEMENTS =
    setOf(CALENDAR, CHARACTER, CULTURE, FASHION, ITEM_TEMPLATE, LANGUAGE, MATERIAL, NAME_LIST, PERSONALITY_TRAIT, RACE)


fun createStorage(type: String) = when (type) {
    CALENDAR -> Storage(CalendarId(0))
    CHARACTER -> Storage(CharacterId(0))
    CULTURE -> Storage(CultureId(0))
    FASHION -> Storage(FashionId(0))
    ITEM_TEMPLATE -> Storage(ItemTemplateId(0))
    LANGUAGE -> Storage(LanguageId(0))
    MATERIAL -> Storage(MaterialId(0))
    NAME_LIST -> Storage(NameListId(0))
    PERSONALITY_TRAIT -> Storage(PersonalityTraitId(0))
    RACE -> Storage(RaceId(0))
    else -> throw IllegalArgumentException("Unknown type $type")
}

fun loadStorage(path: String, type: String): Storage<*, *> = when (type) {
    CALENDAR -> loadStorage<CalendarId, Calendar>(path, CalendarId(0), type)
    CHARACTER -> loadStorage<CharacterId, Character>(path, CharacterId(0), type)
    CULTURE -> loadStorage<CultureId, Culture>(path, CultureId(0), type)
    FASHION -> loadStorage<FashionId, Fashion>(path, FashionId(0), type)
    ITEM_TEMPLATE -> loadStorage<ItemTemplateId, ItemTemplate>(path, ItemTemplateId(0), type)
    LANGUAGE -> loadStorage<LanguageId, Language>(path, LanguageId(0), type)
    MATERIAL -> loadStorage<MaterialId, Material>(path, MaterialId(0), type)
    NAME_LIST -> loadStorage<NameListId, NameList>(path, NameListId(0), type)
    PERSONALITY_TRAIT -> loadStorage<PersonalityTraitId, PersonalityTrait>(path, PersonalityTraitId(0), type)
    RACE -> loadStorage<RaceId, Race>(path, RaceId(0), type)
    else -> throw IllegalArgumentException("Unknown type $type")
}
