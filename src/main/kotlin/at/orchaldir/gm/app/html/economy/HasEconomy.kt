package at.orchaldir.gm.app.html.economy

import at.orchaldir.gm.app.html.optionalField
import at.orchaldir.gm.app.html.util.showRankingOfElements
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.*
import at.orchaldir.gm.core.model.economy.business.BusinessTemplate
import at.orchaldir.gm.core.selector.economy.calculateIndexOfElementBasedOnEconomy
import at.orchaldir.gm.core.selector.economy.calculateTotalNumberInEconomy
import at.orchaldir.gm.core.selector.economy.getAbstractEconomies
import at.orchaldir.gm.core.selector.economy.getEconomyEntries
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.Factor
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2

// show

fun HtmlBlockTag.showEconomyOfBusinessTemplate(
    call: ApplicationCall,
    state: State,
    template: BusinessTemplate,
) = showEconomyOfElement(
    call,
    state,
    template,
    { it.businesses().contains(template.id) },
    {
        when (val population = it.economy()) {
            is EconomyWithNumbers -> population.businesses.getData(template.id)
            is EconomyWithPercentages -> population.businesses.getData(template.id, population.total)
            else -> null
        }
    },
    { economy, other -> economy.getNumber(other.id) },
)

fun <ID : Id<ID>, ELEMENT : Element<ID>> HtmlBlockTag.showEconomyOfElement(
    call: ApplicationCall,
    state: State,
    element: ELEMENT,
    contains: (IAbstractEconomy) -> Boolean,
    getPercentage: (HasEconomy) -> Pair<Int, Factor>?,
    getEconomy: (Economy, ELEMENT) -> Int?,
) {
    h2 { +"Economy" }

    val total = state.calculateTotalNumberInEconomy { population ->
        getEconomy(population, element)
    }
    val totalOrZero = total ?: 0
    optionalField("Total", total)
    optionalField("Index", state.calculateIndexOfElementBasedOnEconomy(element, getEconomy))

    showEconomyOfElement(call, state, getPercentage, state.getDistrictStorage(), totalOrZero, contains)
    showEconomyOfElement(call, state, getPercentage, state.getRealmStorage(), totalOrZero, contains)
    showEconomyOfElement(call, state, getPercentage, state.getTownStorage(), totalOrZero, contains)
}

private fun <ID : Id<ID>, ELEMENT> HtmlBlockTag.showEconomyOfElement(
    call: ApplicationCall,
    state: State,
    getPercentage: (HasEconomy) -> Pair<Int, Factor>?,
    storage: Storage<ID, ELEMENT>,
    total: Int,
    contains: (IAbstractEconomy) -> Boolean,
) where
        ELEMENT : Element<ID>,
        ELEMENT : HasEconomy = showRankingOfElements(
    call,
    state,
    total,
    getAbstractEconomies(storage, contains),
    getEconomyEntries(storage, getPercentage),
)
