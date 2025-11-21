package at.orchaldir.gm.app.html.rpg

import at.orchaldir.gm.app.DIE
import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.html.parseInt
import at.orchaldir.gm.app.html.selectInt
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.core.model.rpg.SimpleModifiedDice
import at.orchaldir.gm.core.model.rpg.SimpleModifiedDiceRange
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag

// show


// edit

fun HtmlBlockTag.editSimpleModifiedDice(
    range: SimpleModifiedDiceRange,
    dice: SimpleModifiedDice,
    param: String,
) {
    selectDiceNumber(dice, param, range)
    selectDiceModifier(dice, param, range)
}

fun HtmlBlockTag.selectDiceNumber(
    entry: SimpleModifiedDice,
    param: String,
    range: SimpleModifiedDiceRange,
) {
    selectInt(
        "Dice",
        entry.dice,
        range.minDice,
        range.maxDice,
        1,
        combine(param, DIE),
    )
}

fun HtmlBlockTag.selectDiceModifier(
    entry: SimpleModifiedDice,
    param: String,
    range: SimpleModifiedDiceRange,
) = selectDiceModifier(param, entry.modifier, range)

fun HtmlBlockTag.selectDiceModifier(
    param: String,
    modifier: Int,
    range: SimpleModifiedDiceRange,
) {
    selectInt(
        "Modifier",
        modifier,
        range.minModifier,
        range.maxModifier,
        1,
        combine(param, NUMBER),
    )
}

// parse

fun parseSimpleModifiedDice(
    parameters: Parameters,
    param: String,
) = SimpleModifiedDice(
    parseInt(parameters, combine(param, DIE), 1),
    parseDiceModifier(parameters, param),
)

fun parseDiceModifier(parameters: Parameters, param: String): Int = parseInt(parameters, combine(param, NUMBER), 0)
