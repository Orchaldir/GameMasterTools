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
import at.orchaldir.gm.core.model.holiday.Holiday
import at.orchaldir.gm.core.model.holiday.HolidayId
import at.orchaldir.gm.core.model.item.ItemTemplate
import at.orchaldir.gm.core.model.item.ItemTemplateId
import at.orchaldir.gm.core.model.language.ComprehensionLevel
import at.orchaldir.gm.core.model.language.Language
import at.orchaldir.gm.core.model.language.LanguageId
import at.orchaldir.gm.core.model.material.Material
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.race.appearance.RaceAppearance
import at.orchaldir.gm.core.model.race.appearance.RaceAppearanceId
import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.time.Time
import at.orchaldir.gm.core.model.world.building.Address
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.model.world.building.BuildingId
import at.orchaldir.gm.core.model.world.building.Ownership
import at.orchaldir.gm.core.model.world.moon.Moon
import at.orchaldir.gm.core.model.world.moon.MoonId
import at.orchaldir.gm.core.model.world.street.Street
import at.orchaldir.gm.core.model.world.street.StreetId
import at.orchaldir.gm.core.model.world.terrain.*
import at.orchaldir.gm.core.model.world.town.Town
import at.orchaldir.gm.core.model.world.town.TownId
import at.orchaldir.gm.utils.map.MapSize2d
import at.orchaldir.gm.utils.map.Resize

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

// holiday
data object CreateHoliday : Action()
data class DeleteHoliday(val id: HolidayId) : Action()
data class UpdateHoliday(val holiday: Holiday) : Action()

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

// race appearance
data object CreateRaceAppearance : Action()
data class DeleteRaceAppearance(val id: RaceAppearanceId) : Action()
data class UpdateRaceAppearance(val race: RaceAppearance) : Action()

// time
data class UpdateTime(val time: Time) : Action()

//-- world --

sealed class WorldAction : Action()

// moon
data object CreateMoon : WorldAction()
data class DeleteMoon(val id: MoonId) : WorldAction()
data class UpdateMoon(val moon: Moon) : WorldAction()

// mountain
data object CreateMountain : WorldAction()
data class DeleteMountain(val id: MountainId) : WorldAction()
data class UpdateMountain(val mountain: Mountain) : WorldAction()

// river
data object CreateRiver : WorldAction()
data class DeleteRiver(val id: RiverId) : WorldAction()
data class UpdateRiver(val river: River) : WorldAction()

// street
data object CreateStreet : WorldAction()
data class DeleteStreet(val id: StreetId) : WorldAction()
data class UpdateStreet(val street: Street) : WorldAction()

// town
data object CreateTown : WorldAction()
data class DeleteTown(val id: TownId) : WorldAction()
data class UpdateTown(val town: Town) : WorldAction()

// town's buildings

data class AddBuilding(
    val town: TownId,
    val tileIndex: Int,
    val size: MapSize2d,
) : WorldAction()

data class DeleteBuilding(val id: BuildingId) : WorldAction()
data class UpdateBuilding(
    val id: BuildingId,
    val name: String,
    val address: Address,
    val constructionDate: Date,
    val ownership: Ownership,
) : WorldAction() {

    fun applyTo(building: Building) = building.copy(
        name = name,
        address = address,
        constructionDate = constructionDate,
        ownership = ownership,
    )
}

// town's streets

data class AddStreetTile(
    val town: TownId,
    val tileIndex: Int,
    val street: StreetId,
) : WorldAction()

data class RemoveStreetTile(
    val town: TownId,
    val tileIndex: Int,
) : WorldAction()

// town's terrain

data class ResizeTown(
    val town: TownId,
    val resize: Resize,
    val terrainType: TerrainType = TerrainType.Plain,
    val terrainId: Int = 0,
) : WorldAction()

data class SetTerrainTile(
    val town: TownId,
    val terrainType: TerrainType,
    val terrainId: Int,
    val tileIndex: Int,
) : WorldAction()