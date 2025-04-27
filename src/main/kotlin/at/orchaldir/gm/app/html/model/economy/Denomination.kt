package at.orchaldir.gm.app.html.model.economy

import at.orchaldir.gm.app.NAME
import at.orchaldir.gm.app.PREFIX
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.model.*
import at.orchaldir.gm.app.html.selectBool
import at.orchaldir.gm.app.html.selectText
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parseBool
import at.orchaldir.gm.core.model.economy.money.Denomination
import io.ktor.http.*
import kotlinx.html.*

// show

fun HtmlBlockTag.showDenomination(
    denomination: Denomination,
) {
    field("Denomination", denomination.display(1))
}

// edit

fun HtmlBlockTag.editDenomination(
    denomination: Denomination,
    param: String,
) {
    selectText("Denomination", denomination.text.text, combine(param, NAME))
    selectBool("Is prefix", denomination.isPrefix, combine(param, PREFIX), true)
}

// parse

fun parseDenomination(parameters: Parameters, param: String) = Denomination(
    parseNoTEmptyString(parameters, combine(param, NAME)),
    parseBool(parameters, combine(param, PREFIX)),
)
