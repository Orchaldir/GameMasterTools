package at.orchaldir.gm.app.html.util.math

import at.orchaldir.gm.app.ITEM
import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.RADIUS
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.parseInt
import at.orchaldir.gm.app.html.selectInt
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.html.combine
import at.orchaldir.gm.utils.math.CircularArrangement
import at.orchaldir.gm.utils.math.Factor
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag

// show

fun <T> HtmlBlockTag.showCircularArrangement(
    label: String,
    arrangement: CircularArrangement<T>,
    showItem: HtmlBlockTag.(T) -> Unit,
) {
    showDetails(label) {
        fieldFactor("Radius", arrangement.radius)
        field("Number", arrangement.number)
        showItem(arrangement.item)
    }
}

// edit

fun <T> HtmlBlockTag.editCircularArrangement(
    label: String,
    arrangement: CircularArrangement<T>,
    param: String,
    editItem: HtmlBlockTag.(T, String) -> Unit,
) {
    showDetails(label, true) {
        selectFactor(
            "Radius",
            combine(param, RADIUS),
            arrangement.radius,
            Factor.fromPercentage(10),
            Factor.fromPercentage(200),
        )
        selectInt(
            "Number",
            arrangement.number,
            3,
            32,
            1,
            combine(param, NUMBER),
        )
        editItem(arrangement.item, combine(param, ITEM))
    }
}

// parse

fun <T> parseCircularArrangement(
    parameters: Parameters,
    param: String,
    defaultNumber: Int,
    parseItem: (String) -> T,
) = CircularArrangement(
    parseItem(combine(param, ITEM)),
    parseInt(parameters, combine(param, NUMBER), defaultNumber),
    parseFactor(parameters, combine(param, RADIUS)),
)
