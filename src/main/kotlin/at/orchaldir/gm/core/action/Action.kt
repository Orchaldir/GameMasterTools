package at.orchaldir.gm.core.action

import at.orchaldir.gm.core.model.Data
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.character.appearance.Appearance
import at.orchaldir.gm.core.model.character.statistic.Statistic
import at.orchaldir.gm.core.model.character.statistic.StatisticId
import at.orchaldir.gm.core.model.character.title.Title
import at.orchaldir.gm.core.model.character.title.TitleId
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.culture.fashion.Fashion
import at.orchaldir.gm.core.model.culture.fashion.FashionId
import at.orchaldir.gm.core.model.culture.language.Language
import at.orchaldir.gm.core.model.culture.language.LanguageId
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.economy.business.BusinessId
import at.orchaldir.gm.core.model.economy.job.Job
import at.orchaldir.gm.core.model.economy.job.JobId
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.economy.money.Currency
import at.orchaldir.gm.core.model.economy.money.CurrencyId
import at.orchaldir.gm.core.model.economy.money.CurrencyUnit
import at.orchaldir.gm.core.model.economy.money.CurrencyUnitId
import at.orchaldir.gm.core.model.health.Disease
import at.orchaldir.gm.core.model.health.DiseaseId
import at.orchaldir.gm.core.model.item.Uniform
import at.orchaldir.gm.core.model.item.UniformId
import at.orchaldir.gm.core.model.item.equipment.Equipment
import at.orchaldir.gm.core.model.item.equipment.EquipmentId
import at.orchaldir.gm.core.model.item.equipment.EquipmentIdMap
import at.orchaldir.gm.core.model.item.periodical.*
import at.orchaldir.gm.core.model.item.text.Text
import at.orchaldir.gm.core.model.item.text.TextId
import at.orchaldir.gm.core.model.magic.*
import at.orchaldir.gm.core.model.organization.Organization
import at.orchaldir.gm.core.model.organization.OrganizationId
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.race.appearance.RaceAppearance
import at.orchaldir.gm.core.model.race.appearance.RaceAppearanceId
import at.orchaldir.gm.core.model.realm.*
import at.orchaldir.gm.core.model.religion.*
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.time.calendar.CalendarId
import at.orchaldir.gm.core.model.time.holiday.Holiday
import at.orchaldir.gm.core.model.time.holiday.HolidayId
import at.orchaldir.gm.core.model.util.InTownMap
import at.orchaldir.gm.core.model.util.font.Font
import at.orchaldir.gm.core.model.util.font.FontId
import at.orchaldir.gm.core.model.util.name.NameList
import at.orchaldir.gm.core.model.util.name.NameListId
import at.orchaldir.gm.core.model.util.quote.Quote
import at.orchaldir.gm.core.model.util.quote.QuoteId
import at.orchaldir.gm.core.model.util.render.ColorScheme
import at.orchaldir.gm.core.model.util.render.ColorSchemeId
import at.orchaldir.gm.core.model.util.source.DataSource
import at.orchaldir.gm.core.model.util.source.DataSourceId
import at.orchaldir.gm.core.model.world.World
import at.orchaldir.gm.core.model.world.WorldId
import at.orchaldir.gm.core.model.world.building.ArchitecturalStyle
import at.orchaldir.gm.core.model.world.building.ArchitecturalStyleId
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.model.world.building.BuildingId
import at.orchaldir.gm.core.model.world.moon.Moon
import at.orchaldir.gm.core.model.world.moon.MoonId
import at.orchaldir.gm.core.model.world.plane.Plane
import at.orchaldir.gm.core.model.world.plane.PlaneId
import at.orchaldir.gm.core.model.world.street.Street
import at.orchaldir.gm.core.model.world.street.StreetId
import at.orchaldir.gm.core.model.world.street.StreetTemplate
import at.orchaldir.gm.core.model.world.street.StreetTemplateId
import at.orchaldir.gm.core.model.world.terrain.Region
import at.orchaldir.gm.core.model.world.terrain.RegionId
import at.orchaldir.gm.core.model.world.terrain.River
import at.orchaldir.gm.core.model.world.terrain.RiverId
import at.orchaldir.gm.core.model.world.town.TerrainType
import at.orchaldir.gm.core.model.world.town.TownMap
import at.orchaldir.gm.core.model.world.town.TownMapId
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.map.MapSize2d
import at.orchaldir.gm.utils.map.Resize

sealed class Action

// META

data class CreateAction<ID : Id<ID>>(val id: ID) : Action()
data class CloneAction<ID : Id<ID>>(val id: ID) : Action()
data class EditAction<ID : Id<ID>, ELEMENT: Element<ID>>(val element: ELEMENT) : Action()
data class LoadData(val path: String) : Action()

// calendar
data class DeleteCalendar(val id: CalendarId) : Action()
data class UpdateCalendar(val calendar: Calendar) : Action()

// color scheme
data class DeleteColorScheme(val id: ColorSchemeId) : Action()
data class UpdateColorScheme(val scheme: ColorScheme) : Action()

// culture
data class DeleteCulture(val id: CultureId) : Action()
data class UpdateCulture(val culture: Culture) : Action()

// title
data class DeleteTitle(val id: TitleId) : Action()
data class UpdateTitle(val title: Title) : Action()

// data
data class UpdateData(val data: Data) : Action()

// data source
data class DeleteDataSource(val id: DataSourceId) : Action()
data class UpdateDataSource(val source: DataSource) : Action()

// disease
data class DeleteDisease(val id: DiseaseId) : Action()
data class UpdateDisease(val disease: Disease) : Action()

// fashion
data class DeleteFashion(val id: FashionId) : Action()
data class UpdateFashion(val fashion: Fashion) : Action()

// font
data class DeleteFont(val id: FontId) : Action()
data class UpdateFont(val font: Font) : Action()

// holiday
data class DeleteHoliday(val id: HolidayId) : Action()
data class UpdateHoliday(val holiday: Holiday) : Action()

// language
data class DeleteLanguage(val id: LanguageId) : Action()
data class UpdateLanguage(val language: Language) : Action()

// material
data class DeleteMaterial(val id: MaterialId) : Action()
data class UpdateMaterial(val material: Material) : Action()

// name list
data class DeleteNameList(val id: NameListId) : Action()
data class UpdateNameList(val nameList: NameList) : Action()

// personality
data class DeletePersonalityTrait(val id: PersonalityTraitId) : Action()
data class UpdatePersonalityTrait(val trait: PersonalityTrait) : Action()

// quote
data class DeleteQuote(val id: QuoteId) : Action()
data class UpdateQuote(val quote: Quote) : Action()

// race
data class DeleteRace(val id: RaceId) : Action()
data class UpdateRace(val race: Race) : Action()

// race appearance
data class DeleteRaceAppearance(val id: RaceAppearanceId) : Action()
data class UpdateRaceAppearance(val appearance: RaceAppearance) : Action()

//-- characters --

sealed class CharacterAction : Action()

// character
data class DeleteCharacter(val id: CharacterId) : CharacterAction()
data class UpdateCharacter(val character: Character) : CharacterAction()
data class UpdateAppearance(
    val id: CharacterId,
    val appearance: Appearance,
) : CharacterAction()

data class UpdateEquipmentOfCharacter(
    val id: CharacterId,
    val map: EquipmentIdMap,
) : CharacterAction()

data class UpdateRelationships(
    val id: CharacterId,
    val relationships: Map<CharacterId, Set<InterpersonalRelationship>>,
) : CharacterAction()

// character template
data class DeleteCharacterTemplate(val id: CharacterTemplateId) : CharacterAction()
data class UpdateCharacterTemplate(val template: CharacterTemplate) : CharacterAction()

// statistic
data class DeleteStatistic(val id: StatisticId) : CharacterAction()
data class UpdateStatistic(val statistic: Statistic) : CharacterAction()

//-- items --

sealed class ItemAction : Action()

// article
data class DeleteArticle(val id: ArticleId) : ItemAction()
data class UpdateArticle(val article: Article) : ItemAction()

// equipment
data class DeleteEquipment(val id: EquipmentId) : ItemAction()
data class UpdateEquipment(val equipment: Equipment) : ItemAction()

// periodical
data class DeletePeriodical(val id: PeriodicalId) : ItemAction()
data class UpdatePeriodical(val periodical: Periodical) : ItemAction()

// periodical issue
data class DeletePeriodicalIssue(val id: PeriodicalIssueId) : ItemAction()
data class UpdatePeriodicalIssue(val issue: PeriodicalIssue) : ItemAction()

// text
data class DeleteText(val id: TextId) : ItemAction()
data class UpdateText(val text: Text) : ItemAction()

// uniform
data class DeleteUniform(val id: UniformId) : ItemAction()
data class UpdateUniform(val uniform: Uniform) : ItemAction()

//-- economy --

sealed class EconomyAction : Action()

// business
data class DeleteBusiness(val id: BusinessId) : EconomyAction()
data class UpdateBusiness(val business: Business) : EconomyAction()

// currency
data class DeleteCurrency(val id: CurrencyId) : EconomyAction()
data class UpdateCurrency(val currency: Currency) : EconomyAction()

// currency unit
data class DeleteCurrencyUnit(val id: CurrencyUnitId) : EconomyAction()
data class UpdateCurrencyUnit(val unit: CurrencyUnit) : EconomyAction()

// job
data class DeleteJob(val id: JobId) : EconomyAction()
data class UpdateJob(val job: Job) : EconomyAction()

//-- magic --

sealed class MagicAction : Action()

// magic tradition
data class DeleteMagicTradition(val id: MagicTraditionId) : MagicAction()
data class UpdateMagicTradition(val tradition: MagicTradition) : MagicAction()

// spell
data class DeleteSpell(val id: SpellId) : MagicAction()

// spell group
data class DeleteSpellGroup(val id: SpellGroupId) : MagicAction()
data class UpdateSpellGroup(val group: SpellGroup) : MagicAction()

//-- organization --

sealed class OrganizationAction : Action()

// organization
data class DeleteOrganization(val id: OrganizationId) : OrganizationAction()
data class UpdateOrganization(val organization: Organization) : OrganizationAction()

//-- realm --

sealed class RealmAction : Action()

// battle
data class DeleteBattle(val id: BattleId) : RealmAction()
data class UpdateBattle(val battle: Battle) : RealmAction()

// catastrophe
data class DeleteCatastrophe(val id: CatastropheId) : RealmAction()
data class UpdateCatastrophe(val catastrophe: Catastrophe) : RealmAction()

// district
data class DeleteDistrict(val id: DistrictId) : RealmAction()
data class UpdateDistrict(val district: District) : RealmAction()

// legal code
data class DeleteLegalCode(val id: LegalCodeId) : RealmAction()
data class UpdateLegalCode(val code: LegalCode) : RealmAction()

// realm
data class DeleteRealm(val id: RealmId) : RealmAction()
data class UpdateRealm(val realm: Realm) : RealmAction()

// town
data class DeleteTown(val id: TownId) : RealmAction()
data class UpdateTown(val town: Town) : RealmAction()

// treaty
data class DeleteTreaty(val id: TreatyId) : RealmAction()
data class UpdateTreaty(val treaty: Treaty) : RealmAction()

// war
data class DeleteWar(val id: WarId) : RealmAction()
data class UpdateWar(val war: War) : RealmAction()

//-- religion --

sealed class ReligionAction : Action()

// domain
data class DeleteDomain(val id: DomainId) : ReligionAction()
data class UpdateDomain(val domain: Domain) : ReligionAction()

// god
data class DeleteGod(val id: GodId) : ReligionAction()
data class UpdateGod(val god: God) : ReligionAction()

// pantheon
data class DeletePantheon(val id: PantheonId) : ReligionAction()
data class UpdatePantheon(val pantheon: Pantheon) : ReligionAction()

//-- world --

sealed class WorldAction : Action()

// architectural style
data class DeleteArchitecturalStyle(val id: ArchitecturalStyleId) : WorldAction()
data class UpdateArchitecturalStyle(val style: ArchitecturalStyle) : WorldAction()

// moon
data class DeleteMoon(val id: MoonId) : WorldAction()
data class UpdateMoon(val moon: Moon) : WorldAction()

// plane
data class DeletePlane(val id: PlaneId) : WorldAction()
data class UpdatePlane(val plane: Plane) : WorldAction()

// region
data class DeleteRegion(val id: RegionId) : WorldAction()
data class UpdateRegion(val region: Region) : WorldAction()

// river
data class DeleteRiver(val id: RiverId) : WorldAction()
data class UpdateRiver(val river: River) : WorldAction()

// street
data class DeleteStreet(val id: StreetId) : WorldAction()
data class UpdateStreet(val street: Street) : WorldAction()

// street template
data class DeleteStreetTemplate(val id: StreetTemplateId) : WorldAction()
data class UpdateStreetTemplate(val template: StreetTemplate) : WorldAction()

// town
data class DeleteTownMap(val id: TownMapId) : WorldAction()
data class UpdateTownMap(val townMap: TownMap) : WorldAction()

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

data class UpdateBuilding(val building: Building) : WorldAction()

data class UpdateBuildingLot(
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
        error("UpdateBuildingLot requires InTownMap!")
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
data class UpdateWorld(val world: World) : WorldAction()