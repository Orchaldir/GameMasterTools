package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.Action
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterTemplate
import at.orchaldir.gm.core.model.character.title.Title
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.fashion.Fashion
import at.orchaldir.gm.core.model.culture.language.Language
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.economy.business.BusinessTemplate
import at.orchaldir.gm.core.model.economy.job.Job
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.core.model.economy.money.Currency
import at.orchaldir.gm.core.model.economy.money.CurrencyUnit
import at.orchaldir.gm.core.model.health.Disease
import at.orchaldir.gm.core.model.item.Uniform
import at.orchaldir.gm.core.model.item.ammunition.Ammunition
import at.orchaldir.gm.core.model.item.equipment.Equipment
import at.orchaldir.gm.core.model.item.periodical.Article
import at.orchaldir.gm.core.model.item.periodical.Periodical
import at.orchaldir.gm.core.model.item.periodical.PeriodicalIssue
import at.orchaldir.gm.core.model.item.text.Text
import at.orchaldir.gm.core.model.magic.MagicTradition
import at.orchaldir.gm.core.model.magic.Spell
import at.orchaldir.gm.core.model.magic.SpellGroup
import at.orchaldir.gm.core.model.organization.Organization
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.RaceGroup
import at.orchaldir.gm.core.model.race.appearance.RaceAppearance
import at.orchaldir.gm.core.model.realm.*
import at.orchaldir.gm.core.model.religion.Domain
import at.orchaldir.gm.core.model.religion.God
import at.orchaldir.gm.core.model.religion.Pantheon
import at.orchaldir.gm.core.model.rpg.combat.*
import at.orchaldir.gm.core.model.rpg.statistic.Statistic
import at.orchaldir.gm.core.model.rpg.trait.CharacterTrait
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.time.holiday.Holiday
import at.orchaldir.gm.core.model.util.font.Font
import at.orchaldir.gm.core.model.util.name.NameList
import at.orchaldir.gm.core.model.util.quote.Quote
import at.orchaldir.gm.core.model.util.render.ColorScheme
import at.orchaldir.gm.core.model.util.source.DataSource
import at.orchaldir.gm.core.model.world.World
import at.orchaldir.gm.core.model.world.building.ArchitecturalStyle
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.model.world.moon.Moon
import at.orchaldir.gm.core.model.world.plane.Plane
import at.orchaldir.gm.core.model.world.settlement.SettlementMap
import at.orchaldir.gm.core.model.world.street.Street
import at.orchaldir.gm.core.model.world.street.StreetTemplate
import at.orchaldir.gm.core.model.world.terrain.Region
import at.orchaldir.gm.core.model.world.terrain.River
import at.orchaldir.gm.core.reducer.culture.updateCulture
import at.orchaldir.gm.core.reducer.culture.updateFashion
import at.orchaldir.gm.core.reducer.item.updateEquipment
import at.orchaldir.gm.core.reducer.util.name.updateNameList
import at.orchaldir.gm.core.reducer.world.updateBuilding
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.redux.noFollowUps

fun reduceUpdateElement(
    state: State,
    element: Element<*>,
): Pair<State, List<Action>> = when (element) {
    is Ammunition -> updateElement(state, element)
    is AmmunitionType -> updateElement(state, element)
    is ArchitecturalStyle -> updateElement(state, element)
    is ArmorType -> updateElement(state, element)
    is Article -> updateElement(state, element)
    is Battle -> updateElement(state, element)
    is Building -> updateBuilding(state, element)
    is Business -> updateElement(state, element)
    is BusinessTemplate -> updateElement(state, element)
    is Calendar -> updateElement(state, element)
    is Catastrophe -> updateElement(state, element)
    is Character -> updateElement(state, element)
    is CharacterTemplate -> updateElement(state, element)
    is ColorScheme -> updateElement(state, element)
    is Culture -> updateCulture(state, element)
    is Currency -> updateElement(state, element)
    is CurrencyUnit -> updateElement(state, element)
    is DamageType -> updateElement(state, element)
    is DataSource -> updateElement(state, element)
    is Disease -> updateElement(state, element)
    is District -> updateElement(state, element)
    is Domain -> updateElement(state, element)
    is Equipment -> updateEquipment(state, element)
    is EquipmentModifier -> updateElement(state, element)
    is Fashion -> updateFashion(state, element)
    is Font -> updateElement(state, element)
    is God -> updateElement(state, element)
    is Holiday -> updateElement(state, element)
    is Job -> updateElement(state, element)
    is Language -> updateElement(state, element)
    is LegalCode -> updateElement(state, element)
    is MagicTradition -> updateElement(state, element)
    is Material -> updateElement(state, element)
    is MeleeWeaponType -> updateElement(state, element)
    is Moon -> updateElement(state, element)
    is NameList -> updateNameList(state, element)
    is Organization -> updateElement(state, element)
    is Pantheon -> updateElement(state, element)
    is Periodical -> updateElement(state, element)
    is PeriodicalIssue -> updateElement(state, element)
    is CharacterTrait -> updateElement(state, element)
    is Plane -> updateElement(state, element)
    is Quote -> updateElement(state, element)
    is Race -> updateElement(state, element)
    is RaceAppearance -> updateElement(state, element)
    is RaceGroup -> updateElement(state, element)
    is RangedWeaponType -> updateElement(state, element)
    is Realm -> updateElement(state, element)
    is Region -> updateElement(state, element)
    is River -> updateElement(state, element)
    is Settlement -> updateElement(state, element)
    is SettlementMap -> updateElement(state, element)
    is SettlementSize -> updateElement(state, element)
    is ShieldType -> updateElement(state, element)
    is Spell -> updateElement(state, element)
    is SpellGroup -> updateElement(state, element)
    is Statistic -> updateElement(state, element)
    is Street -> updateElement(state, element)
    is StreetTemplate -> updateElement(state, element)
    is Text -> updateElement(state, element)
    is Title -> updateElement(state, element)
    is Treaty -> updateElement(state, element)
    is Uniform -> updateElement(state, element)
    is War -> updateElement(state, element)
    is World -> updateElement(state, element)
    else -> error("Updating is not supported!")
}

fun <ID : Id<ID>, ELEMENT : Element<ID>, Action> updateElement(
    state: State,
    element: ELEMENT,
): Pair<State, List<Action>> {
    val storage = state.getStorage<ID, ELEMENT>(element.id())

    storage.require(element.id())
    element.validate(state)

    return noFollowUps(state.updateStorage(storage.update(element)))
}

