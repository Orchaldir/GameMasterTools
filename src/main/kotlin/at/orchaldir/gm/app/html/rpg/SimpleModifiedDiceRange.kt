package at.orchaldir.gm.app.html.rpg

import at.orchaldir.gm.app.DIE
import at.orchaldir.gm.app.MAX
import at.orchaldir.gm.app.MIN
import at.orchaldir.gm.app.MODIFIER
import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.parseInt
import at.orchaldir.gm.app.html.selectInt
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.core.model.rpg.SimpleModifiedDiceRange
import io.ktor.http.*
import kotlinx.html.DETAILS
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showSimpleModifiedDiceRange(
    label: String,
    range: SimpleModifiedDiceRange,
) {
    showDetails(label) {
        field("Min Dice", range.minDice)
        field("Max Dice", range.maxDice)
        field("Min Modifier", range.minModifier)
        field("Max Modifier", range.maxModifier)
    }
}

// edit

fun HtmlBlockTag.editSimpleModifiedDiceRange(
    label: String,
    range: SimpleModifiedDiceRange,
    param: String,
) {
    showDetails(label, true) {
        editMinMax(
            "Dice",
            param,
            DIE,
            range.minDice,
            range.maxDice,
        )
        editMinMax(
            "Modifier",
            param,
            NUMBER,
            range.minModifier,
            range.maxModifier,
        )
    }
}

private fun HtmlBlockTag.editMinMax(
    label: String,
    param: String,
    param2: String,
    minValue: Int,
    maxValue: Int,
) {
    selectInt(
        "Min $label",
        minValue,
        -100,
        maxValue - 1,
        1,
        combine(param, param2, MIN),
    )
    selectInt(
        "Max $label",
        maxValue,
        minValue + 1,
        100,
        1,
        combine(param, param2, MAX),
    )
}

// parse

fun parseSimpleModifiedDiceRange(
    parameters: Parameters,
    param: String,
) = SimpleModifiedDiceRange(
    parseInt(parameters, combine(param, DIE, MIN)),
    parseInt(parameters, combine(param, DIE, MAX)),
    parseInt(parameters, combine(param, NUMBER, MIN)),
    parseInt(parameters, combine(param, NUMBER, MAX)),
)
