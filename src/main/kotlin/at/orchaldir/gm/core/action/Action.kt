package at.orchaldir.gm.core.action

import at.orchaldir.gm.core.model.Data
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.character.appearance.Appearance
import at.orchaldir.gm.core.model.character.title.Title
import at.orchaldir.gm.core.model.character.title.TitleId
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.culture.fashion.Fashion
import at.orchaldir.gm.core.model.culture.fashion.FashionId
import at.orchaldir.gm.core.model.culture.language.ComprehensionLevel
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
import at.orchaldir.gm.core.model.item.Uniform
import at.orchaldir.gm.core.model.item.UniformId
import at.orchaldir.gm.core.model.item.equipment.Equipment
import at.orchaldir.gm.core.model.item.equipment.EquipmentId
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap
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
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.time.holiday.Holiday
import at.orchaldir.gm.core.model.time.holiday.HolidayId
import at.orchaldir.gm.core.model.util.Creator
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.util.Owner
import at.orchaldir.gm.core.model.util.font.Font
import at.orchaldir.gm.core.model.util.font.FontId
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.name.NameList
import at.orchaldir.gm.core.model.util.name.NameListId
import at.orchaldir.gm.core.model.util.quote.Quote
import at.orchaldir.gm.core.model.util.quote.QuoteId
import at.orchaldir.gm.core.model.util.source.DataSource
import at.orchaldir.gm.core.model.util.source.DataSourceId
import at.orchaldir.gm.core.model.world.building.*
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
import at.orchaldir.gm.utils.map.MapSize2d
import at.orchaldir.gm.utils.map.Resize

sealed class Action

// META

data class LoadData(val path: String) : Action()

// character
data object CreateCharacter : Action()
data class DeleteCharacter(val id: CharacterId) : Action()
data class UpdateCharacter(val character: Character) : Action()
data class UpdateAppearance(
    val id: CharacterId,
    val appearance: Appearance,
) : Action()

data class UpdateEquipmentOfCharacter(
    val id: CharacterId,
    val map: EquipmentMap<EquipmentId>,
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
data class CloneCulture(val id: CultureId) : Action()
data class DeleteCulture(val id: CultureId) : Action()
data class UpdateCulture(val culture: Culture) : Action()

// title
data object CreateTitle : Action()
data class DeleteTitle(val id: TitleId) : Action()
data class UpdateTitle(val title: Title) : Action()

// data
data class UpdateData(val data: Data) : Action()

// data source
data object CreateDataSource : Action()
data class DeleteDataSource(val id: DataSourceId) : Action()
data class UpdateDataSource(val source: DataSource) : Action()

// fashion
data object CreateFashion : Action()
data class DeleteFashion(val id: FashionId) : Action()
data class UpdateFashion(val fashion: Fashion) : Action()

// font
data object CreateFont : Action()
data class DeleteFont(val id: FontId) : Action()
data class UpdateFont(val font: Font) : Action()

// holiday
data object CreateHoliday : Action()
data class DeleteHoliday(val id: HolidayId) : Action()
data class UpdateHoliday(val holiday: Holiday) : Action()

// language
data object CreateLanguage : Action()
data class DeleteLanguage(val id: LanguageId) : Action()
data class UpdateLanguage(val language: Language) : Action()

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

// quote
data object CreateQuote : Action()
data class DeleteQuote(val id: QuoteId) : Action()
data class UpdateQuote(val quote: Quote) : Action()

// race
data object CreateRace : Action()
data class CloneRace(val id: RaceId) : Action()
data class DeleteRace(val id: RaceId) : Action()
data class UpdateRace(val race: Race) : Action()

// race appearance
data object CreateRaceAppearance : Action()
data class CloneRaceAppearance(val id: RaceAppearanceId) : Action()
data class DeleteRaceAppearance(val id: RaceAppearanceId) : Action()
data class UpdateRaceAppearance(val appearance: RaceAppearance) : Action()

//-- items --

sealed class ItemAction : Action()

// article
data object CreateArticle : ItemAction()
data class DeleteArticle(val id: ArticleId) : ItemAction()
data class UpdateArticle(val article: Article) : ItemAction()

// equipment
data object CreateEquipment : ItemAction()
data class DeleteEquipment(val id: EquipmentId) : ItemAction()
data class UpdateEquipment(val equipment: Equipment) : ItemAction()

// periodical
data object CreatePeriodical : ItemAction()
data class DeletePeriodical(val id: PeriodicalId) : ItemAction()
data class UpdatePeriodical(val periodical: Periodical) : ItemAction()

// periodical issue
data object CreatePeriodicalIssue : ItemAction()
data class DeletePeriodicalIssue(val id: PeriodicalIssueId) : ItemAction()
data class UpdatePeriodicalIssue(val issue: PeriodicalIssue) : ItemAction()

// text
data object CreateText : ItemAction()
data class DeleteText(val id: TextId) : ItemAction()
data class UpdateText(val text: Text) : ItemAction()

// uniform
data object CreateUniform : ItemAction()
data class DeleteUniform(val id: UniformId) : ItemAction()
data class UpdateUniform(val uniform: Uniform) : ItemAction()

//-- economy --

sealed class EconomyAction : Action()

// business
data object CreateBusiness : EconomyAction()
data class DeleteBusiness(val id: BusinessId) : EconomyAction()
data class UpdateBusiness(val business: Business) : EconomyAction()

// currency
data object CreateCurrency : EconomyAction()
data class DeleteCurrency(val id: CurrencyId) : EconomyAction()
data class UpdateCurrency(val currency: Currency) : EconomyAction()

// currency unit
data object CreateCurrencyUnit : EconomyAction()
data class DeleteCurrencyUnit(val id: CurrencyUnitId) : EconomyAction()
data class UpdateCurrencyUnit(val unit: CurrencyUnit) : EconomyAction()

// job
data object CreateJob : EconomyAction()
data class DeleteJob(val id: JobId) : EconomyAction()
data class UpdateJob(val job: Job) : EconomyAction()

//-- magic --

sealed class MagicAction : Action()

// magic tradition
data object CreateMagicTradition : MagicAction()
data class DeleteMagicTradition(val id: MagicTraditionId) : MagicAction()
data class UpdateMagicTradition(val tradition: MagicTradition) : MagicAction()

// spell
data object CreateSpell : MagicAction()
data class DeleteSpell(val id: SpellId) : MagicAction()
data class UpdateSpell(val spell: Spell) : MagicAction()

// spell group
data object CreateSpellGroup : MagicAction()
data class DeleteSpellGroup(val id: SpellGroupId) : MagicAction()
data class UpdateSpellGroup(val group: SpellGroup) : MagicAction()

//-- organization --

sealed class OrganizationAction : Action()

// organization
data object CreateOrganization : OrganizationAction()
data class DeleteOrganization(val id: OrganizationId) : OrganizationAction()
data class UpdateOrganization(val organization: Organization) : OrganizationAction()

//-- realm --

sealed class RealmAction : Action()

// battle
data object CreateBattle : RealmAction()
data class DeleteBattle(val id: BattleId) : RealmAction()
data class UpdateBattle(val battle: Battle) : RealmAction()

// catastrophe
data object CreateCatastrophe : RealmAction()
data class DeleteCatastrophe(val id: CatastropheId) : RealmAction()
data class UpdateCatastrophe(val catastrophe: Catastrophe) : RealmAction()

// legal code
data object CreateLegalCode : RealmAction()
data class DeleteLegalCode(val id: LegalCodeId) : RealmAction()
data class UpdateLegalCode(val code: LegalCode) : RealmAction()

// realm
data object CreateRealm : RealmAction()
data class DeleteRealm(val id: RealmId) : RealmAction()
data class UpdateRealm(val realm: Realm) : RealmAction()

// town
data object CreateTown : RealmAction()
data class DeleteTown(val id: TownId) : RealmAction()
data class UpdateTown(val town: Town) : RealmAction()

// treaty
data object CreateTreaty : RealmAction()
data class DeleteTreaty(val id: TreatyId) : RealmAction()
data class UpdateTreaty(val treaty: Treaty) : RealmAction()

// war
data object CreateWar : RealmAction()
data class DeleteWar(val id: WarId) : RealmAction()
data class UpdateWar(val war: War) : RealmAction()

//-- religion --

sealed class ReligionAction : Action()

// domain
data object CreateDomain : ReligionAction()
data class DeleteDomain(val id: DomainId) : ReligionAction()
data class UpdateDomain(val domain: Domain) : ReligionAction()

// god
data object CreateGod : ReligionAction()
data class DeleteGod(val id: GodId) : ReligionAction()
data class UpdateGod(val god: God) : ReligionAction()

// pantheon
data object CreatePantheon : ReligionAction()
data class DeletePantheon(val id: PantheonId) : ReligionAction()
data class UpdatePantheon(val pantheon: Pantheon) : ReligionAction()

//-- world --

sealed class WorldAction : Action()

// architectural style
data object CreateArchitecturalStyle : WorldAction()
data class DeleteArchitecturalStyle(val id: ArchitecturalStyleId) : WorldAction()
data class UpdateArchitecturalStyle(val style: ArchitecturalStyle) : WorldAction()

// moon
data object CreateMoon : WorldAction()
data class DeleteMoon(val id: MoonId) : WorldAction()
data class UpdateMoon(val moon: Moon) : WorldAction()

// plane
data object CreatePlane : WorldAction()
data class DeletePlane(val id: PlaneId) : WorldAction()
data class UpdatePlane(val plane: Plane) : WorldAction()

// region
data object CreateRegion : WorldAction()
data class DeleteRegion(val id: RegionId) : WorldAction()
data class UpdateRegion(val region: Region) : WorldAction()

// river
data object CreateRiver : WorldAction()
data class DeleteRiver(val id: RiverId) : WorldAction()
data class UpdateRiver(val river: River) : WorldAction()

// street
data object CreateStreet : WorldAction()
data class DeleteStreet(val id: StreetId) : WorldAction()
data class UpdateStreet(val street: Street) : WorldAction()

// street template
data object CreateStreetTemplate : WorldAction()
data class DeleteStreetTemplate(val id: StreetTemplateId) : WorldAction()
data class UpdateStreetTemplate(val template: StreetTemplate) : WorldAction()

// town
data object CreateTownMap : WorldAction()
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

data class UpdateBuilding(
    val id: BuildingId,
    val name: Name?,
    val address: Address,
    val constructionDate: Date?,
    val ownership: History<Owner>,
    val style: ArchitecturalStyleId?,
    val purpose: BuildingPurpose,
    val builder: Creator,
) : WorldAction() {

    fun applyTo(building: Building) = building.copy(
        name = name,
        address = address,
        constructionDate = constructionDate,
        ownership = ownership,
        style = style,
        purpose = purpose,
        builder = builder,
    )
}

data class UpdateBuildingLot(
    val id: BuildingId,
    val tileIndex: Int,
    val size: MapSize2d,
) : WorldAction() {

    fun applyTo(building: Building) = building.copy(
        lot = building.lot.copy(tileIndex = tileIndex, size = size)
    )
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