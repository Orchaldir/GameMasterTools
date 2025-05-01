package at.orchaldir.gm.app.html.model.economy

import at.orchaldir.gm.app.PRICE
import at.orchaldir.gm.app.html.model.economy.money.editPrice
import at.orchaldir.gm.app.html.model.economy.money.parsePrice
import at.orchaldir.gm.app.html.model.economy.money.showPrice
import at.orchaldir.gm.app.html.parseInt
import at.orchaldir.gm.app.html.parseName
import at.orchaldir.gm.app.html.selectName
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.standard.StandardOfLiving
import at.orchaldir.gm.core.model.economy.standard.StandardOfLivingId
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showStandardOfLiving(
    call: ApplicationCall,
    state: State,
    standard: StandardOfLiving,
) {
    showPrice(state, "Cost Per Day", standard.costPerDay)
}

// edit

fun FORM.editStandardOfLiving(
    state: State,
    standard: StandardOfLiving,
) {
    selectName(standard.name)
    editPrice(state, "Cost Per Day", standard.costPerDay, PRICE, 0, 100000)
}

// parse

fun parseStandardOfLivingId(parameters: Parameters, param: String) = StandardOfLivingId(parseInt(parameters, param))

fun parseStandardOfLiving(id: StandardOfLivingId, parameters: Parameters) = StandardOfLiving(
    id,
    parseName(parameters),
    parsePrice(parameters, PRICE),
)
