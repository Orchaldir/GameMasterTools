package at.orchaldir.gm.app.html.economy

import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.Economy
import at.orchaldir.gm.core.model.economy.EconomyWithNumbers
import at.orchaldir.gm.core.model.economy.EconomyWithPercentages
import at.orchaldir.gm.core.model.economy.HasEconomy
import at.orchaldir.gm.core.model.economy.IAbstractEconomy
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
import kotlinx.html.*

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
    //showEconomyOfElement(call, state, getPercentage, state.getRealmStorage(), totalOrZero, contains)
    //showEconomyOfElement(call, state, getPercentage, state.getTownStorage(), totalOrZero, contains)
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
        ELEMENT : HasEconomy {
    val elementsWithAbstractEconomy = getAbstractEconomies(storage, contains)
    val entries = getEconomyEntries(storage, getPercentage)

    if (elementsWithAbstractEconomy.isEmpty() && entries.isEmpty()) {
        return
    }

    h3 { +storage.getPlural() }

    if (entries.isNotEmpty()) {
        val id = entries.first().id

        table {
            tr {
                th { +id.plural() }
                thMultiLines(listOf("Percentage", "of", "Total"))
                thMultiLines(listOf("Percentage", "of", id.type()))
                th { +"Number" }
            }
            entries
                .sortedByDescending { it.number }
                .forEach {
                    val percentageOfTotal = Factor.fromNumber(it.number / total.toFloat())

                    tr {
                        tdLink(call, state, it.id)
                        tdPercentage(percentageOfTotal)
                        tdPercentage(it.percentage)
                        tdSkipZero(it.number)
                    }
                }
        }
    }

    fieldElements(call, state, "Abstract Economy In", elementsWithAbstractEconomy)
}
