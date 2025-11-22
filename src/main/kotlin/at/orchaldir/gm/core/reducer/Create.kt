package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.Action
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
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
import at.orchaldir.gm.core.model.rpg.combat.*
import at.orchaldir.gm.core.model.rpg.statistic.Statistic
import at.orchaldir.gm.core.model.rpg.statistic.StatisticId
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.time.calendar.CalendarId
import at.orchaldir.gm.core.model.time.holiday.Holiday
import at.orchaldir.gm.core.model.time.holiday.HolidayId
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
import at.orchaldir.gm.core.model.world.town.TownMap
import at.orchaldir.gm.core.model.world.town.TownMapId
import at.orchaldir.gm.core.selector.time.getCurrentDate
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.redux.noFollowUps

fun reduceCreateElement(
    state: State,
    id: Id<*>,
): Pair<State, List<Action>> = when (id) {
    is ArchitecturalStyleId -> createElement(state, ArchitecturalStyle(id))
    is ArmorTypeId -> createElement(state, ArmorType(id))
    is ArticleId -> createElement(state, Article(id))
    is BattleId -> createElement(state, Battle(id))
    is BusinessId -> createElement(state, Business(id))
    is CalendarId -> createElement(state, Calendar(id))
    is CatastropheId -> createElement(state, Catastrophe(id))
    is CharacterId -> createElement(state, Character(id, birthDate = state.getCurrentDate()))
    is CharacterTemplateId -> {
        val race = state.getRaceStorage().getIds().first()
        createElement(state, CharacterTemplate(id, race = race))
    }

    is ColorSchemeId -> createElement(state, ColorScheme(id))
    is CultureId -> createElement(state, Culture(id))
    is CurrencyId -> createElement(state, Currency(id))
    is CurrencyUnitId -> createElement(state, CurrencyUnit(id))
    is DamageTypeId -> createElement(state, DamageType(id))
    is DataSourceId -> createElement(state, DataSource(id))
    is DiseaseId -> createElement(state, Disease(id))
    is DistrictId -> createElement(state, District(id))
    is DomainId -> createElement(state, Domain(id))
    is EquipmentId -> createElement(state, Equipment(id))
    is EquipmentModifierId -> createElement(state, EquipmentModifier(id))
    is FashionId -> createElement(state, Fashion(id))
    is FontId -> createElement(state, Font(id))
    is GodId -> createElement(state, God(id))
    is HolidayId -> createElement(state, Holiday(id))
    is JobId -> createElement(state, Job(id))
    is LanguageId -> createElement(state, Language(id))
    is LegalCodeId -> createElement(state, LegalCode(id))
    is MagicTraditionId -> createElement(state, MagicTradition(id))
    is MaterialId -> createElement(state, Material(id))
    is MeleeWeaponTypeId -> createElement(state, MeleeWeaponType(id))
    is MoonId -> createElement(state, Moon(id))
    is NameListId -> createElement(state, NameList(id))
    is OrganizationId -> createElement(state, Organization(id))
    is PantheonId -> createElement(state, Pantheon(id))
    is PeriodicalId -> createElement(state, Periodical(id))
    is PeriodicalIssueId -> createElement(state, PeriodicalIssue(id))
    is PersonalityTraitId -> createElement(state, PersonalityTrait(id))
    is PlaneId -> createElement(state, Plane(id))
    is QuoteId -> createElement(state, Quote(id))
    is RaceId -> createElement(state, Race(id))
    is RaceAppearanceId -> createElement(state, RaceAppearance(id))
    is RealmId -> createElement(state, Realm(id))
    is RegionId -> createElement(state, Region(id))
    is RiverId -> createElement(state, River(id))
    is ShieldTypeId -> createElement(state, ShieldType(id))
    is SpellId -> createElement(state, Spell(id))
    is SpellGroupId -> createElement(state, SpellGroup(id))
    is StatisticId -> createElement(state, Statistic(id))
    is StreetId -> createElement(state, Street(id))
    is StreetTemplateId -> createElement(state, StreetTemplate(id))
    is TextId -> createElement(state, Text(id))
    is TitleId -> createElement(state, Title(id))
    is TownId -> createElement(state, Town(id))
    is TownMapId -> createElement(state, TownMap(id))
    is TreatyId -> createElement(state, Treaty(id))
    is UniformId -> createElement(state, Uniform(id))
    is WarId -> createElement(state, War(id))
    is WorldId -> createElement(state, World(id))

    else -> error("Creating is not supported!")
}

fun <ID : Id<ID>, ELEMENT : Element<ID>, Action> createElement(
    state: State,
    element: ELEMENT,
): Pair<State, List<Action>> {
    val storage = state.getStorage<ID, ELEMENT>(element.id())

    return noFollowUps(state.updateStorage(storage.add(element)))
}
