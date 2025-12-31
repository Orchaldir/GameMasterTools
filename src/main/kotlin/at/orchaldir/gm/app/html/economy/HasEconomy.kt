package at.orchaldir.gm.app.html.economy

import at.orchaldir.gm.app.html.optionalField
import at.orchaldir.gm.app.html.util.showRankingOfElements
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.*
import at.orchaldir.gm.core.model.economy.business.BusinessTemplateId
import at.orchaldir.gm.core.selector.economy.calculateEconomyIndex
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
    template: BusinessTemplateId,
) = showEconomyOfElement(
    call,
    state,
    state.getBusinessTemplateStorage(),
    template,
    { it.businesses().contains(template) },
    {
        when (val population = it.economy()) {
            is EconomyWithNumbers -> population.businesses.getData(template)
            is EconomyWithPercentages -> population.businesses.getData(template, population.total)
            else -> null
        }
    },
    { hasEconomy, id -> hasEconomy.getNumber(id) },
)

fun <ID : Id<ID>, ELEMENT : Element<ID>> HtmlBlockTag.showEconomyOfElement(
    call: ApplicationCall,
    state: State,
    storage: Storage<ID, ELEMENT>,
    id: ID,
    contains: (IAbstractEconomy) -> Boolean,
    getPercentage: (HasEconomy) -> Pair<Int, Factor>?,
    getEconomy: (Economy, ID) -> Int?,
) {
    h2 { +"Economy" }

    val total = state.calculateTotalNumberInEconomy { population ->
        getEconomy(population, id)
    }
    val totalOrZero = total ?: 0
    optionalField("Total", total)
    optionalField("Index", state.calculateEconomyIndex(storage, id, getEconomy))

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
