package at.orchaldir.gm.app.html.rpg

import at.orchaldir.gm.app.DIE
import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.html.combine
import at.orchaldir.gm.app.html.parseInt
import at.orchaldir.gm.core.model.rpg.dice.SimpleModifiedDice
import at.orchaldir.gm.core.model.rpg.dice.ModifiedDiceRange
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag

// show


// edit

fun HtmlBlockTag.editSimpleModifiedDice(
    range: ModifiedDiceRange,
    dice: SimpleModifiedDice,
    param: String,
) {
    selectDiceNumber(dice, param, range)
    selectDiceModifier(dice, param, range)
}

fun HtmlBlockTag.selectDiceNumber(
    entry: SimpleModifiedDice,
    param: String,
    range: ModifiedDiceRange,
) {
    selectFromRange(
        "Dice",
        range.dice,
        entry.dice,
        combine(param, DIE),
    )
}

fun HtmlBlockTag.selectDiceModifier(
    entry: SimpleModifiedDice,
    param: String,
    range: ModifiedDiceRange,
) = selectDiceModifier(param, entry.modifier, range)

fun HtmlBlockTag.selectDiceModifier(
    param: String,
    modifier: Int,
    range: ModifiedDiceRange,
) {
    selectFromRange(
        "Modifier",
        range.modifier,
        modifier,
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
