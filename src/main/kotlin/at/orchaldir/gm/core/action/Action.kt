package at.orchaldir.gm.core.action

import at.orchaldir.gm.core.model.Data
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.character.CharacterTemplateId
import at.orchaldir.gm.core.model.character.InterpersonalRelationship
import at.orchaldir.gm.core.model.character.PersonalityTraitId
import at.orchaldir.gm.core.model.character.appearance.Appearance
import at.orchaldir.gm.core.model.character.statistic.StatisticId
import at.orchaldir.gm.core.model.character.title.TitleId
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.culture.fashion.FashionId
import at.orchaldir.gm.core.model.culture.language.LanguageId
import at.orchaldir.gm.core.model.economy.business.BusinessId
import at.orchaldir.gm.core.model.economy.job.JobId
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.economy.money.CurrencyId
import at.orchaldir.gm.core.model.economy.money.CurrencyUnitId
import at.orchaldir.gm.core.model.health.DiseaseId
import at.orchaldir.gm.core.model.item.UniformId
import at.orchaldir.gm.core.model.item.equipment.EquipmentId
import at.orchaldir.gm.core.model.item.equipment.EquipmentIdMap
import at.orchaldir.gm.core.model.item.periodical.ArticleId
import at.orchaldir.gm.core.model.item.periodical.PeriodicalId
import at.orchaldir.gm.core.model.item.periodical.PeriodicalIssueId
import at.orchaldir.gm.core.model.item.text.TextId
import at.orchaldir.gm.core.model.magic.MagicTraditionId
import at.orchaldir.gm.core.model.magic.SpellGroupId
import at.orchaldir.gm.core.model.magic.SpellId
import at.orchaldir.gm.core.model.organization.OrganizationId
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.race.appearance.RaceAppearanceId
import at.orchaldir.gm.core.model.realm.*
import at.orchaldir.gm.core.model.religion.DomainId
import at.orchaldir.gm.core.model.religion.GodId
import at.orchaldir.gm.core.model.religion.PantheonId
import at.orchaldir.gm.core.model.time.calendar.CalendarId
import at.orchaldir.gm.core.model.time.holiday.HolidayId
import at.orchaldir.gm.core.model.util.InTownMap
import at.orchaldir.gm.core.model.util.font.FontId
import at.orchaldir.gm.core.model.util.name.NameListId
import at.orchaldir.gm.core.model.util.quote.QuoteId
import at.orchaldir.gm.core.model.util.render.ColorSchemeId
import at.orchaldir.gm.core.model.util.source.DataSourceId
import at.orchaldir.gm.core.model.world.WorldId
import at.orchaldir.gm.core.model.world.building.ArchitecturalStyleId
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.model.world.building.BuildingId
import at.orchaldir.gm.core.model.world.moon.MoonId
import at.orchaldir.gm.core.model.world.plane.PlaneId
import at.orchaldir.gm.core.model.world.street.StreetId
import at.orchaldir.gm.core.model.world.street.StreetTemplateId
import at.orchaldir.gm.core.model.world.terrain.RegionId
import at.orchaldir.gm.core.model.world.terrain.RiverId
import at.orchaldir.gm.core.model.world.town.TerrainType
import at.orchaldir.gm.core.model.world.town.TownMapId
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.map.MapSize2d
import at.orchaldir.gm.utils.map.Resize

sealed class Action

// data
data class LoadData(val path: String) : Action()

// element
data class CreateAction<ID : Id<ID>>(val id: ID) : Action()
data class CloneAction<ID : Id<ID>>(val id: ID) : Action()
data class UpdateAction<ID : Id<ID>, ELEMENT : Element<ID>>(val element: ELEMENT) : Action()

// calendar
data class DeleteCalendar(val id: CalendarId) : Action()

// color scheme
data class DeleteColorScheme(val id: ColorSchemeId) : Action()

// culture
data class DeleteCulture(val id: CultureId) : Action()

// title
data class DeleteTitle(val id: TitleId) : Action()

// data
data class UpdateData(val data: Data) : Action()

// data source
data class DeleteDataSource(val id: DataSourceId) : Action()

// disease
data class DeleteDisease(val id: DiseaseId) : Action()

// fashion
data class DeleteFashion(val id: FashionId) : Action()

// font
data class DeleteFont(val id: FontId) : Action()

// holiday
data class DeleteHoliday(val id: HolidayId) : Action()

// language
data class DeleteLanguage(val id: LanguageId) : Action()

// material
data class DeleteMaterial(val id: MaterialId) : Action()

// name list
data class DeleteNameList(val id: NameListId) : Action()

// personality
data class DeletePersonalityTrait(val id: PersonalityTraitId) : Action()

// quote
data class DeleteQuote(val id: QuoteId) : Action()

// race
data class DeleteRace(val id: RaceId) : Action()

// race appearance
data class DeleteRaceAppearance(val id: RaceAppearanceId) : Action()

//-- characters --

sealed class CharacterAction : Action()

// character
data class DeleteCharacter(val id: CharacterId) : CharacterAction()
data class UpdateAppearance(
    val id: CharacterId,
    val appearance: Appearance,
) : CharacterAction()

data class UpdateActionOfCharacter(
    val id: CharacterId,
    val map: EquipmentIdMap,
) : CharacterAction()

data class UpdateRelationships(
    val id: CharacterId,
    val relationships: Map<CharacterId, Set<InterpersonalRelationship>>,
) : CharacterAction()

// character template
data class DeleteCharacterTemplate(val id: CharacterTemplateId) : CharacterAction()

// statistic
data class DeleteStatistic(val id: StatisticId) : CharacterAction()

//-- items --

sealed class ItemAction : Action()

// article
data class DeleteArticle(val id: ArticleId) : ItemAction()

// equipment
data class DeleteEquipment(val id: EquipmentId) : ItemAction()

// periodical
data class DeletePeriodical(val id: PeriodicalId) : ItemAction()

// periodical issue
data class DeletePeriodicalIssue(val id: PeriodicalIssueId) : ItemAction()

// text
data class DeleteText(val id: TextId) : ItemAction()

// uniform
data class DeleteUniform(val id: UniformId) : ItemAction()

//-- economy --

sealed class EconomyAction : Action()

// business
data class DeleteBusiness(val id: BusinessId) : EconomyAction()

// currency
data class DeleteCurrency(val id: CurrencyId) : EconomyAction()

// currency unit
data class DeleteCurrencyUnit(val id: CurrencyUnitId) : EconomyAction()

// job
data class DeleteJob(val id: JobId) : EconomyAction()

//-- magic --

sealed class MagicAction : Action()

// magic tradition
data class DeleteMagicTradition(val id: MagicTraditionId) : MagicAction()

// spell
data class DeleteSpell(val id: SpellId) : MagicAction()

// spell group
data class DeleteSpellGroup(val id: SpellGroupId) : MagicAction()

//-- organization --

sealed class OrganizationAction : Action()

// organization
data class DeleteOrganization(val id: OrganizationId) : OrganizationAction()

//-- realm --

sealed class RealmAction : Action()

// battle
data class DeleteBattle(val id: BattleId) : RealmAction()

// catastrophe
data class DeleteCatastrophe(val id: CatastropheId) : RealmAction()

// district
data class DeleteDistrict(val id: DistrictId) : RealmAction()

// legal code
data class DeleteLegalCode(val id: LegalCodeId) : RealmAction()

// realm
data class DeleteRealm(val id: RealmId) : RealmAction()

// town
data class DeleteTown(val id: TownId) : RealmAction()

// treaty
data class DeleteTreaty(val id: TreatyId) : RealmAction()

// war
data class DeleteWar(val id: WarId) : RealmAction()

//-- religion --

sealed class ReligionAction : Action()

// domain
data class DeleteDomain(val id: DomainId) : ReligionAction()

// god
data class DeleteGod(val id: GodId) : ReligionAction()

// pantheon
data class DeletePantheon(val id: PantheonId) : ReligionAction()

//-- world --

sealed class WorldAction : Action()

// architectural style
data class DeleteArchitecturalStyle(val id: ArchitecturalStyleId) : WorldAction()

// moon
data class DeleteMoon(val id: MoonId) : WorldAction()

// plane
data class DeletePlane(val id: PlaneId) : WorldAction()

// region
data class DeleteRegion(val id: RegionId) : WorldAction()

// river
data class DeleteRiver(val id: RiverId) : WorldAction()

// street
data class DeleteStreet(val id: StreetId) : WorldAction()

// street template
data class DeleteStreetTemplate(val id: StreetTemplateId) : WorldAction()

// town
data class DeleteTownMap(val id: TownMapId) : WorldAction()

// town's abstract buildings

data class AddAbstractBuilding(
    val town: TownMapId,
    val tileIndex: Int,
    val size: MapSize2d = MapSize2d.square(1),
) : WorldAction()

data class RemoveAbstractBuilding(
    val town: TownMapId,
    val tileIndex: Int,
) : WorldAction()

// town's buildings

data class AddBuilding(
    val town: TownMapId,
    val tileIndex: Int,
    val size: MapSize2d,
) : WorldAction()

data class DeleteBuilding(val id: BuildingId) : WorldAction()

data class UpdateActionLot(
    val id: BuildingId,
    val tileIndex: Int,
    val size: MapSize2d,
) : WorldAction() {

    fun applyTo(building: Building) = if (building.position is InTownMap) {
        building.copy(
            position = building.position.copy(tileIndex = tileIndex),
            size = size,
        )
    } else {
        error("UpdateActionLot requires InTownMap!")
    }
}

// town's streets

data class AddStreetTile(
    val town: TownMapId,
    val tileIndex: Int,
    val type: StreetTemplateId,
    val street: StreetId?,
) : WorldAction()

data class RemoveStreetTile(
    val town: TownMapId,
    val tileIndex: Int,
) : WorldAction()

// town's terrain

data class ResizeTerrain(
    val town: TownMapId,
    val resize: Resize,
    val terrainType: TerrainType = TerrainType.Plain,
    val terrainId: Int = 0,
) : WorldAction()

data class SetTerrainTile(
    val town: TownMapId,
    val terrainType: TerrainType,
    val terrainId: Int,
    val tileIndex: Int,
) : WorldAction()

// world
data class DeleteWorld(val id: WorldId) : WorldAction()