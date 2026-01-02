package at.orchaldir.gm.app.html.economy.money

import at.orchaldir.gm.app.NAME
import at.orchaldir.gm.app.PREFIX
import at.orchaldir.gm.app.SPACE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.combine
import at.orchaldir.gm.core.model.economy.money.Denomination
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag

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
    selectNotEmptyString("Denomination", denomination.text, combine(param, NAME))
    selectBool("Is prefix", denomination.isPrefix, combine(param, PREFIX))
    selectBool("Has Space", denomination.hasSpace, combine(param, SPACE))
    field("Example", denomination.display(1))
}

// parse

fun parseDenomination(parameters: Parameters, param: String) = Denomination(
    parseNotEmptyString(parameters, combine(param, NAME), "gp"),
    parseBool(parameters, combine(param, PREFIX)),
    parseBool(parameters, combine(param, SPACE)),
)
