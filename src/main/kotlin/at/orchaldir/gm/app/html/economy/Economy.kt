package at.orchaldir.gm.app.html.economy

import at.orchaldir.gm.app.BUSINESS
import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.POPULATION
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.*
import at.orchaldir.gm.app.html.util.math.parseFactor
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseElements
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.*
import at.orchaldir.gm.core.model.economy.business.BusinessTemplate
import at.orchaldir.gm.core.selector.util.sortBusinessTemplates
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.ZERO
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.DETAILS
import kotlinx.html.HtmlBlockTag

// show

fun <ID : Id<ID>, ELEMENT> HtmlBlockTag.showEconomyDetails(
    call: ApplicationCall,
    state: State,
    element: ELEMENT,
) where
        ELEMENT : Element<ID>,
        ELEMENT : HasEconomy {
    val economy = element.economy()

    if (economy is UndefinedEconomy) {
        return
    }

    val total = economy.getNumberOfBusinesses()
    val totalOrZero = total ?: 0

    showDetails("Economy", true) {
        optionalField("Businesses", total)

        when (economy) {
            is CommonBusinesses -> fieldIds(call, state, economy.businesses)
            is EconomyWithNumbers -> showNumberDistribution(
                call,
                state,
                "Race",
                economy.businesses,
                totalOrZero,
            )

            is EconomyWithPercentages -> showPercentageDistribution(
                call,
                state,
                "Race",
                economy.businesses,
                totalOrZero,
            )

            UndefinedEconomy -> doNothing()
        }
    }
}

// edit

fun HtmlBlockTag.editEconomy(
    call: ApplicationCall,
    state: State,
    economy: Economy,
    param: String = POPULATION,
) {
    val total = economy.getNumberOfBusinesses() ?: 0

    showDetails("Economy", true) {
        selectValue("Type", param, EconomyType.entries, economy.getType())

        when (economy) {
            is CommonBusinesses -> selectElements(
                state,
                "Businesses",
                combine(param, BUSINESS),
                state.sortBusinessTemplates(),
                economy.businesses,
            )

            is EconomyWithNumbers -> editNumberDistribution(
                call,
                state,
                "Business",
                combine(param, BUSINESS),
                state.sortBusinessTemplates(),
                economy.businesses,
                total,
            )

            is EconomyWithPercentages -> {
                selectBusinessNumber(param, economy.total)
                editPercentageDistribution(
                    call,
                    state,
                    "Business",
                    combine(param, BUSINESS),
                    state.sortBusinessTemplates(),
                    economy.businesses,
                    total,
                )
            }

            UndefinedEconomy -> doNothing()
        }
    }
}

private fun DETAILS.selectBusinessNumber(param: String, number: Int) {
    selectInt(
        "Number of Businesses",
        number,
        0,
        Int.MAX_VALUE,
        1,
        combine(param, NUMBER),
    )
}

// parse

fun parseEconomy(
    parameters: Parameters,
    state: State,
    param: String = POPULATION,
) = when (parse(parameters, param, EconomyType.Undefined)) {
    EconomyType.Businesses -> CommonBusinesses(
        parseElements(parameters, combine(param, BUSINESS), ::parseBusinessTemplateId)
    )

    EconomyType.Numbers -> EconomyWithNumbers(
        parseNumberDistribution(
            state.getBusinessTemplateStorage(),
            parameters,
            param,
            ::parseNumberOfBusiness,
        ),
    )

    EconomyType.Percentages -> EconomyWithPercentages(
        parseBusinessNumber(parameters, param),
        parsePercentageDistribution(
            state.getBusinessTemplateStorage(),
            parameters,
            param,
            ::parsePercentageOfBusiness,
        ),
    )

    EconomyType.Undefined -> UndefinedEconomy
}

private fun parseBusinessNumber(parameters: Parameters, param: String): Int =
    parseInt(parameters, combine(param, NUMBER), 0)

fun parseNumberOfBusiness(parameters: Parameters, param: String, template: BusinessTemplate) =
    parseInt(parameters, combine(param, BUSINESS, template.id.value))

fun parsePercentageOfBusiness(parameters: Parameters, param: String, template: BusinessTemplate) =
    parseFactor(parameters, combine(param, BUSINESS, template.id.value), ZERO)
