package at.orchaldir.gm.app.html.economy.money

import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.WEIGHT
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.money.*
import at.orchaldir.gm.core.selector.getDefaultCurrency
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.unit.VolumePerMaterial
import at.orchaldir.gm.utils.math.unit.Weight
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.*

// show

fun HtmlBlockTag.displayPriceLookup(
    call: ApplicationCall,
    currency: Currency,
    lookup: PriceLookup,
    showZero: Boolean = false,
    calculate: () -> Price,
) {
    val price = when (lookup) {
        CalculatedPrice -> calculate()
        is UserDefinedPrice -> lookup.price
    }

    displayPrice(call, currency, price, showZero)
}

fun HtmlBlockTag.showPriceLookupDetails(
    call: ApplicationCall,
    state: State,
    lookup: PriceLookup,
    vpm: VolumePerMaterial,
) {
    showDetails("Price", true) {
        field("Type", lookup.getType())

        showVolumePerMaterial(call, state, vpm)

        when (lookup) {
            CalculatedPrice -> fieldPrice(call, state, "Calculated Price", vpm.getPrice(state))
            is UserDefinedPrice -> fieldPrice(call, state, "User Defined Price", lookup.price)
        }
    }
}

fun HtmlBlockTag.showVolumePerMaterial(
    call: ApplicationCall,
    state: State,
    vpm: VolumePerMaterial,
) {
    val currency = state.getDefaultCurrency()

    table {
        tr {
            th { +"Material" }
            th { +"Weight" }
            th { +"Price per Kilogram" }
            th { +"Price" }
        }
        vpm.getMap().forEach { (id, volume) ->
            val material = state.getMaterialStorage().getOrThrow(id)
            val weight = Weight.fromVolume(volume, material.density)
            val price = Price.fromWeight(weight, material.pricePerKilogram)

            tr {
                tdLink(call, state, material)
                tdString(weight.toString())
                td {
                    displayPrice(call, currency, material.pricePerKilogram)
                }
                td {
                    displayPrice(call, currency, price)
                }
            }
        }
    }
}

// edit

fun HtmlBlockTag.selectPriceLookup(
    state: State,
    lookup: PriceLookup,
    minPrice: Int,
    maxPrice: Int,
    param: String = WEIGHT,
) {
    showDetails("Price", true) {
        selectValue("Type", combine(param, TYPE), PriceLookupType.entries, lookup.getType())

        when (lookup) {
            CalculatedPrice -> doNothing()
            is UserDefinedPrice -> selectPrice(
                state,
                "Price",
                lookup.price,
                param,
                minPrice,
                maxPrice,
            )
        }
    }
}

// parse

fun parsePriceLookup(
    state: State,
    parameters: Parameters,
    param: String = WEIGHT,
) = when (parse(parameters, combine(param, TYPE), PriceLookupType.Calculated)) {
    PriceLookupType.Calculated -> CalculatedPrice
    PriceLookupType.UserDefined -> UserDefinedPrice(
        parsePrice(state, parameters, param),
    )
}
