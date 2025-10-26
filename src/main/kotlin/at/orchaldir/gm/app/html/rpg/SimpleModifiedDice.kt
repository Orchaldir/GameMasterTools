package at.orchaldir.gm.app.html.rpg

import at.orchaldir.gm.app.DIE
import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.html.parseInt
import at.orchaldir.gm.app.html.selectInt
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.core.model.rpg.SimpleModifiedDice
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag

// show


// edit

fun HtmlBlockTag.selectDiceNumber(
    entry: SimpleModifiedDice,
    param: String,
) {
    selectInt(
        entry.dice,
        1,
        100,
        1,
        combine(param, DIE),
    )
}

fun HtmlBlockTag.selectDiceModifier(
    entry: SimpleModifiedDice,
    entryParam: String,
) {
    selectInt(
        entry.modifier,
        -10,
        +10,
        1,
        combine(entryParam, NUMBER),
    )
}

// parse

fun parseSimpleModifiedDice(
    parameters: Parameters,
    param: String,
) = SimpleModifiedDice(
    parseInt(parameters, combine(param, DIE), 1),
    parseInt(parameters, combine(param, NUMBER), 0),
)
