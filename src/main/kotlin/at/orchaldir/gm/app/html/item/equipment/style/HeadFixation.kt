package at.orchaldir.gm.app.html.item.equipment.style

import at.orchaldir.gm.app.LENGTH
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.math.fieldFactor
import at.orchaldir.gm.app.html.util.math.parseFactor
import at.orchaldir.gm.app.html.util.math.selectFactor
import at.orchaldir.gm.app.html.util.part.editItemPart
import at.orchaldir.gm.app.html.util.part.parseColorSchemeItemPart
import at.orchaldir.gm.app.html.util.part.parseItemPart
import at.orchaldir.gm.app.html.util.part.selectColorSchemeItemParts
import at.orchaldir.gm.app.html.util.part.showItemPart
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.CATEGORIES_FOR_CLOTHING
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.part.ItemPartType
import at.orchaldir.gm.core.model.util.part.SOLID_MATERIALS
import at.orchaldir.gm.core.selector.util.sortMaterials
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.Factor
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.DETAILS
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showHeadFixation(
    call: ApplicationCall,
    state: State,
    fixation: HeadFixation,
) {
    showDetails("Fixation") {
        field("Type", fixation.getType())

        when (fixation) {
            NoHeadFixation -> doNothing()
            is BoundHeadHead -> {
                fieldFactor("Length", fixation.length)
                showItemPart(call, state, fixation.cord)
            }

            is Langets -> {
                fieldFactor("Length", fixation.length)
                showItemPart(call, state, fixation.part)
            }

            is SocketedHeadHead -> {
                fieldFactor("Length", fixation.length)
                showItemPart(call, state, fixation.part)
            }
        }
    }
}

// edit

fun HtmlBlockTag.editHeadFixation(
    state: State,
    fixation: HeadFixation,
    param: String,
) {
    val boundMaterials = state.sortMaterials(CATEGORIES_FOR_CLOTHING)

    showDetails("Fixation", true) {
        selectValue("Type", param, HeadFixationType.entries, fixation.getType()) {
            when (it) {
                HeadFixationType.Bound -> boundMaterials.isEmpty()
                else -> false
            }
        }

        when (fixation) {
            NoHeadFixation -> doNothing()
            is BoundHeadHead -> {
                selectLength(param, fixation.length)
                editItemPart(state, fixation.cord, param, "Cord", ItemPartType.Cord)
            }

            is Langets -> {
                selectLength(param, fixation.length, MIN_LANGETS_LENGTH, MAX_LANGETS_LENGTH)
                editItemPart(state, fixation.part, param, allowedTypes = SOLID_MATERIALS)
            }

            is SocketedHeadHead -> {
                selectLength(param, fixation.length)
                editItemPart(state, fixation.part, param, allowedTypes = SOLID_MATERIALS)
            }
        }
    }
}

private fun DETAILS.selectLength(
    param: String,
    length: Factor,
    min: Factor = MIN_FIXATION_LENGTH,
    max: Factor = MAX_FIXATION_LENGTH,
) {
    selectFactor(
        "Length",
        combine(param, LENGTH),
        length,
        min,
        max,
    )
}

// parse

fun parseHeadFixation(
    parameters: Parameters,
    param: String,
) = when (parse(parameters, param, HeadFixationType.None)) {
    HeadFixationType.None -> NoHeadFixation
    HeadFixationType.Bound -> BoundHeadHead(
        parseLength(parameters, param),
        parseItemPart(parameters, param),
    )

    HeadFixationType.Langets -> Langets(
        parseLength(parameters, param, DEFAULT_LANGETS_LENGTH),
        parseItemPart(parameters, param),
    )

    HeadFixationType.Socketed -> SocketedHeadHead(
        parseLength(parameters, param),
        parseItemPart(parameters, param),
    )
}

private fun parseLength(
    parameters: Parameters,
    param: String,
    default: Factor = DEFAULT_FIXATION_LENGTH,
) = parseFactor(parameters, combine(param, LENGTH), default)