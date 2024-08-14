package at.orchaldir.gm.core.model

import at.orchaldir.gm.core.model.calendar.CalendarId
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.character.PersonalityTraitId
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.fashion.FashionId
import at.orchaldir.gm.core.model.item.ItemTemplateId
import at.orchaldir.gm.core.model.language.LanguageId
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.core.loadStorage

enum class ElementType {
    CALENDAR,
    CHARACTER,
    CULTURE,
    FASHION,
    ITEM_TEMPLATE,
    LANGUAGE,
    MATERIAL,
    NAME_LIST,
    PERSONALITY_TRAIT,
    RACE;

    fun createStorage() = when (this) {
        CALENDAR -> Storage(CalendarId(0), name)
        CHARACTER -> Storage(CharacterId(0), name)
        CULTURE -> Storage(CultureId(0), name)
        FASHION -> Storage(FashionId(0), name)
        ITEM_TEMPLATE -> Storage(ItemTemplateId(0), name)
        LANGUAGE -> Storage(LanguageId(0), name)
        MATERIAL -> Storage(MaterialId(0), name)
        NAME_LIST -> Storage(NameListId(0), name)
        PERSONALITY_TRAIT -> Storage(PersonalityTraitId(0), name)
        RACE -> Storage(RaceId(0), name)
    }

    fun loadStorage(path: String) = when (this) {
        CALENDAR -> loadStorage(path, CalendarId(0), name)
        CHARACTER -> loadStorage(path, CharacterId(0), name)
        CULTURE -> loadStorage(path, CultureId(0), name)
        FASHION -> loadStorage(path, FashionId(0), name)
        ITEM_TEMPLATE -> loadStorage(path, ItemTemplateId(0), name)
        LANGUAGE -> loadStorage(path, LanguageId(0), name)
        MATERIAL -> loadStorage(path, MaterialId(0), name)
        NAME_LIST -> loadStorage(path, NameListId(0), name)
        PERSONALITY_TRAIT -> loadStorage(path, PersonalityTraitId(0), name)
        RACE -> loadStorage(path, RaceId(0), name)
    }

    fun createId0() = when (this) {
        CALENDAR -> CalendarId(0)
        CHARACTER -> CharacterId(0)
        CULTURE -> CultureId(0)
        FASHION -> FashionId(0)
        ITEM_TEMPLATE -> ItemTemplateId(0)
        LANGUAGE -> LanguageId(0)
        MATERIAL -> MaterialId(0)
        NAME_LIST -> NameListId(0)
        PERSONALITY_TRAIT -> PersonalityTraitId(0)
        RACE -> RaceId(0)
    }
}