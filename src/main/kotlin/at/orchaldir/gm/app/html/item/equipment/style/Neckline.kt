package at.orchaldir.gm.app.html.item.equipment.style

import at.orchaldir.gm.app.NECKLINE
import at.orchaldir.gm.app.OPENING
import at.orchaldir.gm.app.SIZE
import at.orchaldir.gm.app.STYLE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.DETAILS
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showNeckline(
    call: ApplicationCall,
    state: State,
    neckline: Neckline,
) {
    showDetails("Neckline") {
        field("Type", neckline.getType())

        when (neckline) {
            Asymmetrical -> doNothing()
            Crew -> doNothing()
            Halter -> doNothing()
            NoNeckline -> doNothing()
            is NecklineWithOpening -> {
                field("Height", neckline.height)
                showOpening(call, state, neckline.opening)
            }
            Strapless -> doNothing()
            is VNeck -> field("Height", neckline.height)
        }
    }
}

// edit

fun HtmlBlockTag.editNeckline(
    state: State,
    neckline: Neckline,
    allowedTypes: Collection<NecklineType> = NecklineType.entries,
    param: String = NECKLINE,
) {
    showDetails("Neckline", true) {
        selectValue(
            "Type", 
            combine(param, STYLE),
            allowedTypes,
            neckline.getType(),
        )

        when (neckline) {
            Asymmetrical -> doNothing()
            Crew -> doNothing()
            Halter -> doNothing()
            NoNeckline -> doNothing()
            is NecklineWithOpening -> {
                selectHeight(param, neckline.height)
                editOpening(
                    state,
                    neckline.opening,
                    ALL_OPENINGS,
                    combine(param, OPENING),
                )
            }
            Strapless -> doNothing()
            is VNeck -> selectHeight(param, neckline.height)
        }
    }
}

private fun DETAILS.selectHeight(
    param: String,
    height: Size,
) {
    selectValue(
        "Height",
        combine(param, SIZE),
        Size.entries,
        height,
    )
}

// parse

fun parseNeckline(
    state: State,
    parameters: Parameters,
    default: NecklineType = NecklineType.None,
    param: String = NECKLINE,
): Neckline {
    val type = parse(parameters, combine(param, STYLE), default)

    return when (type) {
        NecklineType.Asymmetrical -> Asymmetrical
        NecklineType.Crew -> Crew
        NecklineType.Halter -> Halter
        NecklineType.None -> NoNeckline
        NecklineType.Opening -> NecklineWithOpening(
            parseOpening(
                state,
                parameters,
                combine(param, OPENING),
                OpeningType.SingleBreasted,
            ),
            parseHeight(parameters, param),
        )
        NecklineType.Strapless -> Strapless
        NecklineType.V -> VNeck(
            parseHeight(parameters, param),
        )
    }
}

private fun parseHeight(parameters: Parameters, param: String) =
    parse(parameters, combine(param, SIZE), Size.Medium)
