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
            is NecklineWithOpening -> showOpeningStyle(call, state, neckline.opening)
            Strapless -> doNothing()
            is VNeck -> field("Depth", neckline.size)
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
            is NecklineWithOpening -> selectOpeningStyle( state, neckline.opening)
            Strapless -> doNothing()
            is VNeck -> selectValue(
                "Depth",
                combine(param, SIZE),
                Size.entries,
                neckline.size,
            )
        }
    }
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
            parseOpeningStyle(state, parameters, combine(param, OPENING)),
        )
        NecklineType.Strapless -> Strapless
        NecklineType.V -> VNeck(
            parse(parameters, combine(param, SIZE), Size.Medium),
        )
    }
}
