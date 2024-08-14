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
    Calendar,
    Character,
    Culture,
    Fashion,
    ItemTemplate,
    Language,
    Material,
    NameList,
    PersonalityTrait,
    Race;

    fun createStorage() = when (this) {
        Calendar -> Storage(CalendarId(0), name)
        Character -> Storage(CharacterId(0), name)
        Culture -> Storage(CultureId(0), name)
        Fashion -> Storage(FashionId(0), name)
        ItemTemplate -> Storage(ItemTemplateId(0), name)
        Language -> Storage(LanguageId(0), name)
        Material -> Storage(MaterialId(0), name)
        NameList -> Storage(NameListId(0), name)
        PersonalityTrait -> Storage(PersonalityTraitId(0), name)
        Race -> Storage(RaceId(0), name)
    }

    fun loadStorage(path: String) = when (this) {
        Calendar -> loadStorage(path, CalendarId(0), name)
        Character -> loadStorage(path, CharacterId(0), name)
        Culture -> loadStorage(path, CultureId(0), name)
        Fashion -> loadStorage(path, FashionId(0), name)
        ItemTemplate -> loadStorage(path, ItemTemplateId(0), name)
        Language -> loadStorage(path, LanguageId(0), name)
        Material -> loadStorage(path, MaterialId(0), name)
        NameList -> loadStorage(path, NameListId(0), name)
        PersonalityTrait -> loadStorage(path, PersonalityTraitId(0), name)
        Race -> loadStorage(path, RaceId(0), name)
    }

    fun createId0() = when (this) {
        Calendar -> CalendarId(0)
        Character -> CharacterId(0)
        Culture -> CultureId(0)
        Fashion -> FashionId(0)
        ItemTemplate -> ItemTemplateId(0)
        Language -> LanguageId(0)
        Material -> MaterialId(0)
        NameList -> NameListId(0)
        PersonalityTrait -> PersonalityTraitId(0)
        Race -> RaceId(0)
    }
}