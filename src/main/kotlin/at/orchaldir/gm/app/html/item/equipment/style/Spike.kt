package at.orchaldir.gm.app.html.item.equipment.style

import at.orchaldir.gm.app.LENGTH
import at.orchaldir.gm.app.WIDTH
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.html.util.math.fieldFactor
import at.orchaldir.gm.app.html.util.math.parseFactor
import at.orchaldir.gm.app.html.util.part.editColorSchemeItemPart
import at.orchaldir.gm.app.html.util.part.parseColorSchemeItemPart
import at.orchaldir.gm.app.html.util.part.showColorSchemeItemPart
import at.orchaldir.gm.app.html.util.math.selectFactor
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.style.Spike
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.HALF
import at.orchaldir.gm.utils.math.THIRD
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showSpike(
    call: ApplicationCall,
    state: State,
    spike: Spike,
) {
    showDetails("Spike") {
        fieldFactor("Length", spike.length)
        fieldFactor("Width", spike.width)
        showColorSchemeItemPart(call, state, spike.part, "Part")
    }
}

// edit

fun HtmlBlockTag.editSpike(
    state: State,
    spike: Spike,
    param: String,
) {
    showDetails("Spike", true) {
        selectFactor(
            "Length",
            combine(param, LENGTH),
            spike.length,
            fromPercentage(10),
            fromPercentage(200),
        )
        selectFactor(
            "Width",
            combine(param, WIDTH),
            spike.width,
            fromPercentage(10),
            fromPercentage(50),
        )
        editColorSchemeItemPart(state, spike.part, param, "Part")
    }
}

// parse

fun parseSpike(
    parameters: Parameters,
    param: String,
    defaultLength: Factor = HALF,
) = Spike(
    parseFactor(parameters, combine(param, LENGTH), defaultLength),
    parseFactor(parameters, combine(param, WIDTH), THIRD),
    parseColorSchemeItemPart(parameters, param),
)
