package at.orchaldir.gm.core.action

import at.orchaldir.gm.core.model.NameList
import at.orchaldir.gm.core.model.NameListId
import at.orchaldir.gm.core.model.calendar.Calendar
import at.orchaldir.gm.core.model.calendar.CalendarId
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.character.appearance.Appearance
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.fashion.Fashion
import at.orchaldir.gm.core.model.fashion.FashionId
import at.orchaldir.gm.core.model.item.ItemTemplate
import at.orchaldir.gm.core.model.item.ItemTemplateId
import at.orchaldir.gm.core.model.language.ComprehensionLevel
import at.orchaldir.gm.core.model.language.Language
import at.orchaldir.gm.core.model.language.LanguageId
import at.orchaldir.gm.core.model.material.Material
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.time.Time

sealed class Action

// character
data object CreateCharacter : Action()
data class DeleteCharacter(val id: CharacterId) : Action()
data class UpdateCharacter(val character: Character) : Action()
data class UpdateAppearance(
    val id: CharacterId,
    val appearance: Appearance,
) : Action()

data class UpdateEquipment(
    val id: CharacterId,
    val map: EquipmentMap,
) : Action()

data class UpdateRelationships(
    val id: CharacterId,
    val relationships: Map<CharacterId, Set<InterpersonalRelationship>>,
) : Action()

// character's languages

data class AddLanguage(
    val id: CharacterId,
    val language: LanguageId,
    val level: ComprehensionLevel,
) : Action()

data class RemoveLanguages(
    val id: CharacterId,
    val languages: Set<LanguageId>,
) : Action()

// calendar
data object CreateCalendar : Action()
data class DeleteCalendar(val id: CalendarId) : Action()
data class UpdateCalendar(val calendar: Calendar) : Action()

// culture
data object CreateCulture : Action()
data class DeleteCulture(val id: CultureId) : Action()
data class UpdateCulture(val culture: Culture) : Action()

// fashion
data object CreateFashion : Action()
data class DeleteFashion(val id: FashionId) : Action()
data class UpdateFashion(val fashion: Fashion) : Action()

// language
data object CreateLanguage : Action()
data class DeleteLanguage(val id: LanguageId) : Action()
data class UpdateLanguage(val language: Language) : Action()

// item template
data object CreateItemTemplate : Action()
data class DeleteItemTemplate(val id: ItemTemplateId) : Action()
data class UpdateItemTemplate(val itemTemplate: ItemTemplate) : Action()

// material
data object CreateMaterial : Action()
data class DeleteMaterial(val id: MaterialId) : Action()
data class UpdateMaterial(val material: Material) : Action()

// name list
data object CreateNameList : Action()
data class DeleteNameList(val id: NameListId) : Action()
data class UpdateNameList(val nameList: NameList) : Action()

// personality
data object CreatePersonalityTrait : Action()
data class DeletePersonalityTrait(val id: PersonalityTraitId) : Action()
data class UpdatePersonalityTrait(val trait: PersonalityTrait) : Action()

// race
data object CreateRace : Action()
data class DeleteRace(val id: RaceId) : Action()
data class UpdateRace(val race: Race) : Action()

// time
data class UpdateTime(val time: Time) : Action()