package at.orchaldir.gm.app.html.item.equipment.style

import at.orchaldir.gm.app.AXE
import at.orchaldir.gm.app.CLUB
import at.orchaldir.gm.app.LINE
import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.SHAPE
import at.orchaldir.gm.app.SPIKE
import at.orchaldir.gm.app.SUB
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.math.*
import at.orchaldir.gm.app.html.util.math.editCircularArrangement
import at.orchaldir.gm.app.html.util.math.parseCircularArrangement
import at.orchaldir.gm.app.html.util.math.showCircularArrangement
import at.orchaldir.gm.app.html.util.part.editColorSchemeItemPart
import at.orchaldir.gm.app.html.util.part.parseColorSchemeItemPart
import at.orchaldir.gm.app.html.util.part.showColorSchemeItemPart
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showClubHead(
    call: ApplicationCall,
    state: State,
    head: ClubHead,
) {
    showDetails("Club Head") {
        field("Type", head.getType())

        when (head) {
            NoClubHead -> doNothing()
            is SimpleClubHead -> {
                showComplexShape(head.shape)
                showColorSchemeItemPart(call, state, head.part, "Head")
            }

            is SimpleFlangedHead -> {
                showComplexShape(head.shape)
                showColorSchemeItemPart(call, state, head.part, "Head")
            }

            is ComplexFlangedHead -> {
                showRotatedShape(head.shape)
                showColorSchemeItemPart(call, state, head.part, "Head")
            }

            is SpikedMaceHead -> {
                showSpike(call, state, head.spike)
                field("Rows", head.rows)
            }

            is FlailHead -> {
                showClubHead(call, state, head.head)
                showLineStyle(call, state, head.connection, "Connection")
            }

            is MorningStarHead -> {
                showCircularArrangement("Spikes", head.spikes) {
                    showSpike(call, state, it)
                }
                showColorSchemeItemPart(call, state, head.part, "Head")
            }

            is WarhammerHead -> {
                showComplexShape(head.shape)
                showSpike(call, state, head.spike)
                showColorSchemeItemPart(call, state, head.part, "Head")
            }
        }
    }
}
// edit

fun HtmlBlockTag.editClubHead(
    state: State,
    head: ClubHead,
    param: String,
    allowedTypes: Collection<ClubHeadType> = ClubHeadType.entries
) {
    showDetails("Club Head", true) {
        selectValue("Type", param, allowedTypes, head.getType())

        when (head) {
            NoClubHead -> doNothing()
            is SimpleClubHead -> {
                selectComplexShape(head.shape, combine(param, SHAPE))
                editColorSchemeItemPart(state, head.part, param, "Head")
            }

            is SimpleFlangedHead -> {
                selectComplexShape(head.shape, combine(param, SHAPE))
                editColorSchemeItemPart(state, head.part, param, "Head")
            }

            is ComplexFlangedHead -> {
                editRotatedShape(head.shape, combine(param, SHAPE))
                editColorSchemeItemPart(state, head.part, param, "Head")
            }

            is SpikedMaceHead -> {
                editSpike(state, head.spike, combine(param, SPIKE))
                field("Rows", head.rows)
                selectInt(
                    "Rows",
                    head.rows,
                    2,
                    10,
                    1,
                    combine(param, NUMBER),
                )
            }

            is FlailHead -> {
                editClubHead(
                    state,
                    head.head,
                    combine(param, SUB),
                    ALLOWED_FLAIL_HEADS,
                )
                editLineStyle(
                    state,
                    head.connection,
                    "Connection",
                    combine(param, LINE),
                )
            }

            is MorningStarHead -> {
                editCircularArrangement("Spikes", head.spikes, combine(param, SPIKE)) { spike, spikeParam ->
                    editSpike(state, spike, spikeParam)
                }
                editColorSchemeItemPart(state, head.part, param, "Head")
            }

            is WarhammerHead -> {
                selectComplexShape(head.shape, combine(param, SHAPE))
                editSpike(state, head.spike, combine(param, SPIKE))
                editColorSchemeItemPart(state, head.part, param, "Head")
            }
        }
    }
}

// parse

fun parseClubHead(
    parameters: Parameters,
    param: String = CLUB,
): ClubHead = when (parse(parameters, param, ClubHeadType.None)) {
    ClubHeadType.None -> NoClubHead
    ClubHeadType.Simple -> SimpleClubHead(
        parseComplexShape(parameters, combine(param, SHAPE)),
        parseColorSchemeItemPart(parameters, param),
    )

    ClubHeadType.SimpleFlanged -> SimpleFlangedHead(
        parseComplexShape(parameters, combine(param, SHAPE)),
        parseColorSchemeItemPart(parameters, param),
    )

    ClubHeadType.ComplexFlanged -> ComplexFlangedHead(
        parseRotatedShape(parameters, combine(param, SHAPE)),
        parseColorSchemeItemPart(parameters, param),
    )

    ClubHeadType.SpikedMace -> SpikedMaceHead(
        parseSpike(parameters, combine(param, SPIKE)),
        parseInt(parameters, combine(param, NUMBER), 3),
    )

    ClubHeadType.Flail -> FlailHead(
        parseClubHead(parameters, combine(param, SUB)),
        parseLineStyle(parameters, combine(param, LINE)),
    )

    ClubHeadType.MorningStar -> MorningStarHead(
        parseCircularArrangement(parameters, combine(param, SPIKE), 7) {
            parseSpike(parameters, it)
        },
        parseColorSchemeItemPart(parameters, param),
    )

    ClubHeadType.Warhammer -> WarhammerHead(
        parseSpike(parameters, combine(param, SPIKE)),
        parseComplexShape(parameters, combine(param, SHAPE)),
        parseColorSchemeItemPart(parameters, param),
    )
}
