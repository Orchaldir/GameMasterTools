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
}