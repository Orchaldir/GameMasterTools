package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.Action
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.character.CharacterTemplateId
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
import at.orchaldir.gm.core.model.item.periodical.ArticleId
import at.orchaldir.gm.core.model.item.periodical.PeriodicalId
import at.orchaldir.gm.core.model.item.periodical.PeriodicalIssueId
import at.orchaldir.gm.core.model.item.text.TextId
import at.orchaldir.gm.core.model.magic.MagicTraditionId
import at.orchaldir.gm.core.model.magic.SpellGroupId
import at.orchaldir.gm.core.model.magic.SpellId
import at.orchaldir.gm.core.model.organization.OrganizationId
import at.orchaldir.gm.core.model.race.RaceGroupId
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.race.appearance.RaceAppearanceId
import at.orchaldir.gm.core.model.realm.*
import at.orchaldir.gm.core.model.religion.DomainId
import at.orchaldir.gm.core.model.religion.GodId
import at.orchaldir.gm.core.model.religion.PantheonId
import at.orchaldir.gm.core.model.rpg.combat.*
import at.orchaldir.gm.core.model.rpg.statistic.StatisticId
import at.orchaldir.gm.core.model.rpg.trait.CharacterTraitId
import at.orchaldir.gm.core.model.time.calendar.CalendarId
import at.orchaldir.gm.core.model.time.holiday.HolidayId
import at.orchaldir.gm.core.model.util.font.FontId
import at.orchaldir.gm.core.model.util.name.NameListId
import at.orchaldir.gm.core.model.util.quote.QuoteId
import at.orchaldir.gm.core.model.util.render.ColorSchemeId
import at.orchaldir.gm.core.model.util.source.DataSourceId
import at.orchaldir.gm.core.model.world.WorldId
import at.orchaldir.gm.core.model.world.building.ArchitecturalStyleId
import at.orchaldir.gm.core.model.world.building.BuildingId
import at.orchaldir.gm.core.model.world.moon.MoonId
import at.orchaldir.gm.core.model.world.plane.PlaneId
import at.orchaldir.gm.core.model.world.street.StreetId
import at.orchaldir.gm.core.model.world.street.StreetTemplateId
import at.orchaldir.gm.core.model.world.terrain.RegionId
import at.orchaldir.gm.core.model.world.terrain.RiverId
import at.orchaldir.gm.core.model.world.town.TownMapId
import at.orchaldir.gm.core.reducer.world.deleteBuilding
import at.orchaldir.gm.core.selector.character.canDeleteCharacter
import at.orchaldir.gm.core.selector.character.canDeleteCharacterTemplate
import at.orchaldir.gm.core.selector.character.canDeleteTitle
import at.orchaldir.gm.core.selector.culture.canDeleteCulture
import at.orchaldir.gm.core.selector.culture.canDeleteFashion
import at.orchaldir.gm.core.selector.culture.canDeleteLanguage
import at.orchaldir.gm.core.selector.economy.canDeleteBusiness
import at.orchaldir.gm.core.selector.economy.canDeleteJob
import at.orchaldir.gm.core.selector.economy.canDeleteMaterial
import at.orchaldir.gm.core.selector.economy.money.canDeleteCurrency
import at.orchaldir.gm.core.selector.economy.money.canDeleteCurrencyUnit
import at.orchaldir.gm.core.selector.health.canDeleteDisease
import at.orchaldir.gm.core.selector.item.equipment.canDeleteEquipment
import at.orchaldir.gm.core.selector.item.canDeleteText
import at.orchaldir.gm.core.selector.item.canDeleteUniform
import at.orchaldir.gm.core.selector.item.periodical.canDeleteArticle
import at.orchaldir.gm.core.selector.item.periodical.canDeletePeriodical
import at.orchaldir.gm.core.selector.item.periodical.canDeletePeriodicalIssue
import at.orchaldir.gm.core.selector.magic.canDeleteMagicTradition
import at.orchaldir.gm.core.selector.magic.canDeleteSpell
import at.orchaldir.gm.core.selector.magic.canDeleteSpellGroup
import at.orchaldir.gm.core.selector.organization.canDeleteOrganization
import at.orchaldir.gm.core.selector.race.canDeleteRace
import at.orchaldir.gm.core.selector.race.canDeleteRaceAppearance
import at.orchaldir.gm.core.selector.race.canDeleteRaceGroup
import at.orchaldir.gm.core.selector.realm.*
import at.orchaldir.gm.core.selector.religion.canDeleteDomain
import at.orchaldir.gm.core.selector.religion.canDeleteGod
import at.orchaldir.gm.core.selector.religion.canDeletePantheon
import at.orchaldir.gm.core.selector.rpg.*
import at.orchaldir.gm.core.selector.time.canDeleteCalendar
import at.orchaldir.gm.core.selector.time.canDeleteHoliday
import at.orchaldir.gm.core.selector.util.*
import at.orchaldir.gm.core.selector.world.*
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.redux.noFollowUps

fun reduceDeleteElement(
    state: State,
    id: Id<*>,
): Pair<State, List<Action>> = when (id) {
    is ArchitecturalStyleId -> deleteElement(state, id, State::canDeleteArchitecturalStyle)
    is ArmorTypeId -> deleteElement(state, id, State::canDeleteArmorType)
    is ArticleId -> deleteElement(state, id, State::canDeleteArticle)
    is BattleId -> deleteElement(state, id, State::canDeleteBattle)
    is BuildingId -> deleteBuilding(state, id)
    is BusinessId -> deleteElement(state, id, State::canDeleteBusiness)
    is CalendarId -> deleteElement(state, id, State::canDeleteCalendar)
    is CatastropheId -> deleteElement(state, id, State::canDeleteCatastrophe)
    is CharacterId -> deleteElement(state, id, State::canDeleteCharacter)
    is CharacterTemplateId -> deleteElement(state, id, State::canDeleteCharacterTemplate)
    is ColorSchemeId -> deleteElement(state, id, State::canDeleteColorScheme)
    is CultureId -> deleteElement(state, id, State::canDeleteCulture)
    is CurrencyId -> deleteElement(state, id, State::canDeleteCurrency)
    is CurrencyUnitId -> deleteElement(state, id, State::canDeleteCurrencyUnit)
    is DamageTypeId -> deleteElement(state, id, State::canDeleteDamageType)
    is DataSourceId -> deleteElement(state, id, State::canDeleteDataSource)
    is DiseaseId -> deleteElement(state, id, State::canDeleteDisease)
    is DistrictId -> deleteElement(state, id, State::canDeleteDistrict)
    is DomainId -> deleteElement(state, id, State::canDeleteDomain)
    is EquipmentId -> deleteElement(state, id, State::canDeleteEquipment)
    is EquipmentModifierId -> deleteElement(state, id, State::canDeleteEquipmentModifier)
    is FashionId -> deleteElement(state, id, State::canDeleteFashion)
    is FontId -> deleteElement(state, id, State::canDeleteFont)
    is GodId -> deleteElement(state, id, State::canDeleteGod)
    is HolidayId -> deleteElement(state, id, State::canDeleteHoliday)
    is JobId -> deleteElement(state, id, State::canDeleteJob)
    is LanguageId -> deleteElement(state, id, State::canDeleteLanguage)
    is LegalCodeId -> deleteElement(state, id, State::canDeleteLegalCode)
    is MagicTraditionId -> deleteElement(state, id, State::canDeleteMagicTradition)
    is MaterialId -> deleteElement(state, id, State::canDeleteMaterial)
    is MoonId -> deleteElement(state, id, State::canDeleteMoon)
    is MeleeWeaponTypeId -> deleteElement(state, id, State::canDeleteMeleeWeaponType)
    is NameListId -> deleteElement(state, id, State::canDeleteNameList)
    is OrganizationId -> deleteElement(state, id, State::canDeleteOrganization)
    is PantheonId -> deleteElement(state, id, State::canDeletePantheon)
    is PeriodicalId -> deleteElement(state, id, State::canDeletePeriodical)
    is PeriodicalIssueId -> deleteElement(state, id, State::canDeletePeriodicalIssue)
    is CharacterTraitId -> deleteElement(state, id, State::canDeleteCharacterTrait)
    is PlaneId -> deleteElement(state, id, State::canDeletePlane)
    is QuoteId -> deleteElement(state, id, State::canDeleteQuote)
    is RaceId -> deleteElement(state, id, State::canDeleteRace)
    is RaceAppearanceId -> deleteElement(state, id, State::canDeleteRaceAppearance)
    is RaceGroupId -> deleteElement(state, id, State::canDeleteRaceGroup)
    is RealmId -> deleteElement(state, id, State::canDeleteRealm)
    is RegionId -> deleteElement(state, id, State::canDeleteRegion)
    is RiverId -> deleteElement(state, id, State::canDeleteRiver)
    is ShieldTypeId -> deleteElement(state, id, State::canDeleteShieldType)
    is SpellId -> deleteElement(state, id, State::canDeleteSpell)
    is SpellGroupId -> deleteElement(state, id, State::canDeleteSpellGroup)
    is StatisticId -> deleteElement(state, id, State::canDeleteStatistic)
    is StreetId -> deleteElement(state, id, State::canDeleteStreet)
    is StreetTemplateId -> deleteElement(state, id, State::canDeleteStreetTemplate)
    is TextId -> deleteElement(state, id, State::canDeleteText)
    is TitleId -> deleteElement(state, id, State::canDeleteTitle)
    is TownId -> deleteElement(state, id, State::canDeleteTown)
    is TownMapId -> deleteElement(state, id, State::canDeleteTownMap)
    is TreatyId -> deleteElement(state, id, State::canDeleteTreaty)
    is UniformId -> deleteElement(state, id, State::canDeleteUniform)
    is WarId -> deleteElement(state, id, State::canDeleteWar)
    is WorldId -> deleteElement(state, id, State::canDeleteWorld)

    else -> error("Deleting is not supported!")
}

fun <ID : Id<ID>, ELEMENT : Element<ID>, Action> deleteElement(
    state: State,
    id: ID,
    validation: (State, ID) -> DeleteResult,
): Pair<State, List<Action>> {
    val storage = state.getStorage<ID, ELEMENT>(id)

    storage.require(id)
    validation.invoke(state, id).validate()

    return noFollowUps(state.updateStorage(storage.remove(id)))
}
