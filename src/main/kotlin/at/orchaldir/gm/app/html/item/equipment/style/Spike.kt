package at.orchaldir.gm.app.html.item.equipment.style

import at.orchaldir.gm.app.LENGTH
import at.orchaldir.gm.app.WIDTH
import at.orchaldir.gm.app.html.combine
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.html.util.math.fieldFactor
import at.orchaldir.gm.app.html.util.math.parseFactor
import at.orchaldir.gm.app.html.util.math.selectFactor
import at.orchaldir.gm.app.html.util.part.editItemPart
import at.orchaldir.gm.app.html.util.part.parseItemPart
import at.orchaldir.gm.app.html.util.part.showItemPart
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.SPIKE_MATERIALS
import at.orchaldir.gm.core.model.item.equipment.style.Spike
import at.orchaldir.gm.core.model.util.part.SOLID_MATERIALS
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
        showItemPart(call, state, spike.main)
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
        editItemPart(state, spike.main, param, allowedTypes = SPIKE_MATERIALS)
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
    parseItemPart(parameters, param, SPIKE_MATERIALS),
)
