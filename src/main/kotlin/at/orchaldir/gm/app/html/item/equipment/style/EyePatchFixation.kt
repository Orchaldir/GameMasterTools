package at.orchaldir.gm.app.html.item.equipment.style

import at.orchaldir.gm.app.FIXATION
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.parse
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.DETAILS
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showEyePatchFixation(
    call: ApplicationCall,
    state: State,
    fixation: EyePatchFixation,
) {
    showDetails("Fixation") {
        field("Type", fixation.getType())

        when (fixation) {
            NoFixation -> doNothing()
            is OneBand -> showLineStyle(call, state, fixation.band, "Band")
            is DiagonalBand -> showLineStyle(call, state, fixation.band, "Band")
            is TwoBands -> showLineStyle(call, state, fixation.band, "Band")
        }
    }
}

// edit

fun HtmlBlockTag.editEyePatchFixation(
    state: State,
    fixation: EyePatchFixation,
) {
    showDetails("Fixation", true) {
        selectValue("Type", FIXATION, EyePatchFixationType.entries, fixation.getType())

        when (fixation) {
            NoFixation -> doNothing()
            is OneBand -> selectBand(state, fixation.band)
            is DiagonalBand -> selectBand(state, fixation.band)
            is TwoBands -> selectBand(state, fixation.band)
        }
    }
}

private fun DETAILS.selectBand(
    state: State,
    band: LineStyle,
) {
    editLineStyle(state, band, "Band", FIXATION, WITHOUT_ORNAMENT_LINE)
}

// parse

fun parseEyePatchFixation(
    state: State,
    parameters: Parameters,
) = when (parse(parameters, FIXATION, EyePatchFixationType.None)) {
    EyePatchFixationType.None -> NoFixation
    EyePatchFixationType.OneBand -> OneBand(
        parseLineStyle(state, parameters, FIXATION),
    )

    EyePatchFixationType.DiagonalBand -> DiagonalBand(
        parseLineStyle(state, parameters, FIXATION),
    )

    EyePatchFixationType.TwoBands -> TwoBands(
        parseLineStyle(state, parameters, FIXATION),
    )
}

