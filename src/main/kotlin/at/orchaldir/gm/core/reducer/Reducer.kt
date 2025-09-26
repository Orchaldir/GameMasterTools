package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
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
import at.orchaldir.gm.core.reducer.character.CHARACTER_REDUCER
import at.orchaldir.gm.core.reducer.character.UPDATE_PERSONALITY_TRAIT
import at.orchaldir.gm.core.reducer.character.UPDATE_TITLE
import at.orchaldir.gm.core.reducer.culture.UPDATE_CULTURE
import at.orchaldir.gm.core.reducer.culture.UPDATE_FASHION
import at.orchaldir.gm.core.reducer.culture.UPDATE_LANGUAGE
import at.orchaldir.gm.core.reducer.economy.ECONOMY_REDUCER
import at.orchaldir.gm.core.reducer.economy.UPDATE_MATERIAL
import at.orchaldir.gm.core.reducer.health.UPDATE_DISEASE
import at.orchaldir.gm.core.reducer.item.ITEM_REDUCER
import at.orchaldir.gm.core.reducer.magic.MAGIC_REDUCER
import at.orchaldir.gm.core.reducer.organization.ORGANIZATION_REDUCER
import at.orchaldir.gm.core.reducer.race.UPDATE_RACE
import at.orchaldir.gm.core.reducer.race.UPDATE_RACE_APPEARANCE
import at.orchaldir.gm.core.reducer.realm.REALM_REDUCER
import at.orchaldir.gm.core.reducer.religion.RELIGION_REDUCER
import at.orchaldir.gm.core.reducer.time.UPDATE_CALENDAR
import at.orchaldir.gm.core.reducer.time.UPDATE_HOLIDAY
import at.orchaldir.gm.core.reducer.util.color.UPDATE_COLOR_SCHEME
import at.orchaldir.gm.core.reducer.util.font.UPDATE_FONT
import at.orchaldir.gm.core.reducer.util.name.UPDATE_NAME_LIST
import at.orchaldir.gm.core.reducer.util.quote.UPDATE_QUOTE
import at.orchaldir.gm.core.reducer.util.source.UPDATE_DATA_SOURCE
import at.orchaldir.gm.core.reducer.world.WORLD_REDUCER
import at.orchaldir.gm.core.selector.character.canDeletePersonalityTrait
import at.orchaldir.gm.core.selector.character.canDeleteTitle
import at.orchaldir.gm.core.selector.culture.canDeleteCulture
import at.orchaldir.gm.core.selector.culture.canDeleteFashion
import at.orchaldir.gm.core.selector.culture.canDeleteLanguage
import at.orchaldir.gm.core.selector.economy.canDeleteMaterial
import at.orchaldir.gm.core.selector.health.canDeleteDisease
import at.orchaldir.gm.core.selector.race.canDeleteRace
import at.orchaldir.gm.core.selector.race.canDeleteRaceAppearance
import at.orchaldir.gm.core.selector.time.canDeleteCalendar
import at.orchaldir.gm.core.selector.time.canDeleteHoliday
import at.orchaldir.gm.core.selector.time.getCurrentDate
import at.orchaldir.gm.core.selector.util.*
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.redux.Reducer

val REDUCER: Reducer<Action, State> = { state, action ->
    when (action) {
        // create
        is CreateAction<*> -> createElement(state, action.id)
        // clone
        is CloneAction<*> -> when (action.id) {
            is CharacterTemplateId -> cloneElement(state, action.id)
            is CultureId -> cloneElement(state, action.id)
            is RaceAppearanceId -> cloneElement(state, action.id)
            is RaceId -> cloneElement(state, action.id)
            else -> error("Cloning is not supported!")
        }
        // meta
        is LoadData -> LOAD_DATA(state, action)
        // calendar
        is DeleteCalendar -> deleteElement(state, action.id, State::canDeleteCalendar)
        is UpdateCalendar -> UPDATE_CALENDAR(state, action)
        // color schemes
        is DeleteColorScheme -> deleteElement(state, action.id, State::canDeleteColorScheme)
        is UpdateColorScheme -> UPDATE_COLOR_SCHEME(state, action)
        // culture
        is DeleteCulture -> deleteElement(state, action.id, State::canDeleteCulture)
        is UpdateCulture -> UPDATE_CULTURE(state, action)
        // data
        is UpdateData -> UPDATE_DATA(state, action)
        // data source
        is DeleteDataSource -> deleteElement(state, action.id, State::canDeleteDataSource)
        is UpdateDataSource -> UPDATE_DATA_SOURCE(state, action)
        // disease
        is DeleteDisease -> deleteElement(state, action.id, State::canDeleteDisease)
        is UpdateDisease -> UPDATE_DISEASE(state, action)
        // fashion
        is DeleteFashion -> deleteElement(state, action.id, State::canDeleteFashion)
        is UpdateFashion -> UPDATE_FASHION(state, action)
        // font
        is DeleteFont -> deleteElement(state, action.id, State::canDeleteFont)
        is UpdateFont -> UPDATE_FONT(state, action)
        // holiday
        is DeleteHoliday -> deleteElement(state, action.id, State::canDeleteHoliday)
        is UpdateHoliday -> UPDATE_HOLIDAY(state, action)
        // language
        is DeleteLanguage -> deleteElement(state, action.id, State::canDeleteLanguage)
        is UpdateLanguage -> UPDATE_LANGUAGE(state, action)
        // material
        is DeleteMaterial -> deleteElement(state, action.id, State::canDeleteMaterial)
        is UpdateMaterial -> UPDATE_MATERIAL(state, action)
        // name list
        is DeleteNameList -> deleteElement(state, action.id, State::canDeleteNameList)
        is UpdateNameList -> UPDATE_NAME_LIST(state, action)
        // personality
        is DeletePersonalityTrait -> deleteElement(state, action.id, State::canDeletePersonalityTrait)
        is UpdatePersonalityTrait -> UPDATE_PERSONALITY_TRAIT(state, action)
        // quote
        is DeleteQuote -> deleteElement(state, action.id, State::canDeleteQuote)
        is UpdateQuote -> UPDATE_QUOTE(state, action)
        // race
        is DeleteRace -> deleteElement(state, action.id, State::canDeleteRace)
        is UpdateRace -> UPDATE_RACE(state, action)
        // race appearance
        is DeleteRaceAppearance -> deleteElement(state, action.id, State::canDeleteRaceAppearance)
        is UpdateRaceAppearance -> UPDATE_RACE_APPEARANCE(state, action)
        // title
        is DeleteTitle -> deleteElement(state, action.id, State::canDeleteTitle)
        is UpdateTitle -> UPDATE_TITLE(state, action)
        // sub reducers
        is CharacterAction -> CHARACTER_REDUCER(state, action)
        is ItemAction -> ITEM_REDUCER(state, action)
        is EconomyAction -> ECONOMY_REDUCER(state, action)
        is MagicAction -> MAGIC_REDUCER(state, action)
        is OrganizationAction -> ORGANIZATION_REDUCER(state, action)
        is RealmAction -> REALM_REDUCER(state, action)
        is ReligionAction -> RELIGION_REDUCER(state, action)
        is WorldAction -> WORLD_REDUCER(state, action)
    }
}

private fun createElement(
    state: State,
    id: Id<*>,
): Pair<State, List<Action>> = when (id) {
    is ArchitecturalStyleId -> createElement(state, ArchitecturalStyle(id))
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
    is DataSourceId -> createElement(state, DataSource(id))
    is DiseaseId -> createElement(state, Disease(id))
    is DistrictId -> createElement(state, District(id))
    is DomainId -> createElement(state, Domain(id))
    is EquipmentId -> createElement(state, Equipment(id))
    is FashionId -> createElement(state, Fashion(id))
    is FontId -> createElement(state, Font(id))
    is GodId -> createElement(state, God(id))
    is HolidayId -> createElement(state, Holiday(id))
    is JobId -> createElement(state, Job(id))
    is LanguageId -> createElement(state, Language(id))
    is LegalCodeId -> createElement(state, LegalCode(id))
    is MagicTraditionId -> createElement(state, MagicTradition(id))
    is MaterialId -> createElement(state, Material(id))
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

