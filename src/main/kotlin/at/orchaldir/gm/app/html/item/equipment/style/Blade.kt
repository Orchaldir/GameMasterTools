package at.orchaldir.gm.app.html.item.equipment.style

import at.orchaldir.gm.app.BLADE
import at.orchaldir.gm.app.LENGTH
import at.orchaldir.gm.app.SHAPE
import at.orchaldir.gm.app.WIDTH
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.html.util.fieldFactor
import at.orchaldir.gm.app.html.util.parseFactor
import at.orchaldir.gm.app.html.util.part.editColorSchemeItemPart
import at.orchaldir.gm.app.html.util.part.parseColorSchemeItemPart
import at.orchaldir.gm.app.html.util.part.showColorSchemeItemPart
import at.orchaldir.gm.app.html.util.selectFactor
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.utils.math.Factor
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.DETAILS
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showBlade(
    call: ApplicationCall,
    state: State,
    blade: Blade,
    label: String = "Blade",
) {
    showDetails(label) {
        field("Type", blade.getType())

        when (blade) {
            is SimpleBlade -> showSimpleBlade(call, state, blade)
        }
    }
}

private fun DETAILS.showSimpleBlade(
    call: ApplicationCall,
    state: State,
    blade: SimpleBlade,
) {
    field("Shape", blade.shape)
    fieldFactor("Length relative to Character", blade.length)
    fieldFactor("Width relative to Grip", blade.width)
    showColorSchemeItemPart(call, state, blade.part)
}

// edit

fun HtmlBlockTag.editBlade(
    state: State,
    blade: Blade,
    minLength: Factor,
    maxLength: Factor,
    param: String = BLADE,
    label: String = "Blade",
) {
    showDetails(label, true) {
        selectValue("Type", param, BladeType.entries, blade.getType())

        when (blade) {
            is SimpleBlade -> editSimpleBlade(state, blade, param, minLength, maxLength)
        }
    }
}

private fun DETAILS.editSimpleBlade(
    state: State,
    blade: SimpleBlade,
    param: String,
    minLength: Factor,
    maxLength: Factor,
) {
    selectValue(
        "Shape",
        combine(param, SHAPE),
        BladeShape.entries,
        blade.shape,
    )
    selectFactor(
        "Length relative to Character",
        combine(param, LENGTH),
        blade.length,
        minLength,
        maxLength,
    )
    selectFactor(
        "Width relative to Grip",
        combine(param, WIDTH),
        blade.width,
        MIN_BLADE_WIDTH,
        MAX_BLADE_WIDTH,
    )
    editColorSchemeItemPart(state, blade.part, param)
}

// parse

fun parseBlade(
    parameters: Parameters,
    defaultLength: Factor,
    param: String = BLADE,
) = when (parse(parameters, param, BladeType.Simple)) {
    BladeType.Simple -> parseSimpleBlade(parameters, defaultLength, param)
}

private fun parseSimpleBlade(
    parameters: Parameters,
    defaultLength: Factor,
    param: String,
) = SimpleBlade(
    parseBladeLength(parameters, defaultLength, param),
    parseBladeWidth(parameters, param),
    parse(parameters, combine(param, SHAPE), BladeShape.Straight),
    parseColorSchemeItemPart(parameters, param),
)

private fun parseBladeLength(parameters: Parameters, defaultLength: Factor, param: String) =
    parseFactor(parameters, combine(param, LENGTH), defaultLength)

private fun parseBladeWidth(parameters: Parameters, param: String) =
    parseFactor(parameters, combine(param, WIDTH), DEFAULT_BLADE_WIDTH)