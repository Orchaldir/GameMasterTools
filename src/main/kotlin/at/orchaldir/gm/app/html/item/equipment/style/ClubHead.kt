package at.orchaldir.gm.app.html.item.equipment.style

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.math.*
import at.orchaldir.gm.app.html.util.math.editCircularArrangement
import at.orchaldir.gm.app.html.util.math.parseCircularArrangement
import at.orchaldir.gm.app.html.util.math.showCircularArrangement
import at.orchaldir.gm.app.html.util.part.editItemPart
import at.orchaldir.gm.app.html.util.part.parseItemPart
import at.orchaldir.gm.app.html.util.part.showItemPart
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.part.SOLID_MATERIALS
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
                showItemPart(call, state, head.main)
            }

            is SimpleFlangedHead -> {
                showComplexShape(head.shape)
                showItemPart(call, state, head.main)
            }

            is ComplexFlangedHead -> {
                showRotatedShape(head.shape)
                showItemPart(call, state, head.main)
            }

            is SpikedMaceHead -> {
                showSpike(call, state, head.spike)
                field("Rows", head.main)
            }

            is FlailHead -> {
                showClubHead(call, state, head.head)
                showLineStyle(call, state, head.connection, "Connection")
            }

            is MorningStarHead -> {
                showCircularArrangement("Spikes", head.spikes) {
                    showSpike(call, state, it)
                }
                showItemPart(call, state, head.main)
            }

            is WarhammerHead -> {
                showComplexShape(head.shape)
                showSpike(call, state, head.spike)
                showItemPart(call, state, head.main)
            }
        }
    }
}
// edit

fun HtmlBlockTag.editClubHead(
    state: State,
    head: ClubHead,
    param: String,
    allowedTypes: Collection<ClubHeadType> = ClubHeadType.entries,
) {
    showDetails("Club Head", true) {
        selectValue("Type", param, allowedTypes, head.getType())

        when (head) {
            NoClubHead -> doNothing()
            is SimpleClubHead -> {
                selectComplexShape(head.shape, combine(param, SHAPE))
                editItemPart(state, head.main, param, allowedTypes = SOLID_MATERIALS)
            }

            is SimpleFlangedHead -> {
                selectComplexShape(head.shape, combine(param, SHAPE))
                editItemPart(state, head.main, param, allowedTypes = SOLID_MATERIALS)
            }

            is ComplexFlangedHead -> {
                editRotatedShape(head.shape, combine(param, SHAPE))
                editItemPart(state, head.main, param, allowedTypes = SOLID_MATERIALS)
            }

            is SpikedMaceHead -> {
                editSpike(state, head.spike, combine(param, SPIKE))
                field("Rows", head.main)
                selectInt(
                    "Rows",
                    head.main,
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
                editItemPart(state, head.main, param, allowedTypes = SOLID_MATERIALS)
            }

            is WarhammerHead -> {
                selectComplexShape(head.shape, combine(param, SHAPE))
                editSpike(state, head.spike, combine(param, SPIKE))
                editItemPart(state, head.main, param, allowedTypes = SOLID_MATERIALS)
            }
        }
    }
}

// parse

fun parseClubHead(
    parameters: Parameters,
    param: String,
    defaultType: ClubHeadType = ClubHeadType.None,
): ClubHead = when (parse(parameters, param, defaultType)) {
    ClubHeadType.None -> NoClubHead
    ClubHeadType.Simple -> SimpleClubHead(
        parseComplexShape(parameters, combine(param, SHAPE)),
        parseItemPart(parameters, param),
    )

    ClubHeadType.SimpleFlanged -> SimpleFlangedHead(
        parseComplexShape(parameters, combine(param, SHAPE)),
        parseItemPart(parameters, param),
    )

    ClubHeadType.ComplexFlanged -> ComplexFlangedHead(
        parseRotatedShape(parameters, combine(param, SHAPE)),
        parseItemPart(parameters, param),
    )

    ClubHeadType.SpikedMace -> SpikedMaceHead(
        parseSpike(parameters, combine(param, SPIKE)),
        parseInt(parameters, combine(param, NUMBER), 3),
    )

    ClubHeadType.Flail -> FlailHead(
        parseClubHead(parameters, combine(param, SUB), ClubHeadType.MorningStar),
        parseLineStyle(parameters, combine(param, LINE)),
    )

    ClubHeadType.MorningStar -> MorningStarHead(
        parseCircularArrangement(parameters, combine(param, SPIKE), 7) {
            parseSpike(parameters, it)
        },
        parseItemPart(parameters, param),
    )

    ClubHeadType.Warhammer -> WarhammerHead(
        parseSpike(parameters, combine(param, SPIKE)),
        parseComplexShape(parameters, combine(param, SHAPE)),
        parseItemPart(parameters, param),
    )
}
