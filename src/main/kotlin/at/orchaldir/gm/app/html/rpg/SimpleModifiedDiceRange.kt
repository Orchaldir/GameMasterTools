package at.orchaldir.gm.app.html.rpg

import at.orchaldir.gm.app.DIE
import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.core.model.rpg.SimpleModifiedDiceRange
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showSimpleModifiedDiceRange(
    label: String,
    range: SimpleModifiedDiceRange,
) {
    showDetails(label) {
        fieldRange("Dice", range.dice)
        fieldRange("Modifier", range.modifier)
    }
}

// edit

fun HtmlBlockTag.editSimpleModifiedDiceRange(
    label: String,
    range: SimpleModifiedDiceRange,
    param: String,
) {
    showDetails(label, true) {
        editRange(
            "Dice",
            range.dice,
            combine(param, DIE),
        )
        editRange(
            "Modifier",
            range.modifier,
            combine(param, NUMBER),
        )
    }
}

// parse

fun parseSimpleModifiedDiceRange(
    parameters: Parameters,
    param: String,
) = SimpleModifiedDiceRange(
    parseRange(parameters, combine(param, DIE)),
    parseRange(parameters, combine(param, NUMBER)),
)
