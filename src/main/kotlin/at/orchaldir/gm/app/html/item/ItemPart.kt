package at.orchaldir.gm.app.html.item

import at.orchaldir.gm.app.COLOR
import at.orchaldir.gm.app.FILL
import at.orchaldir.gm.app.MATERIAL
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.economy.material.parseMaterialId
import at.orchaldir.gm.app.html.item.equipment.selectMaterial
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.ColorItemPart
import at.orchaldir.gm.core.model.item.FillItemPart
import at.orchaldir.gm.core.model.util.render.Color
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showColorItemPart(
    call: ApplicationCall,
    state: State,
    part: ColorItemPart,
    label: String? = null,
) {
    showDetails(label, true) {
        fieldLink("Material", call, state, part.material)
        fieldOptionalColor(part.color)
    }
}

fun HtmlBlockTag.showFillItemPart(
    call: ApplicationCall,
    state: State,
    part: FillItemPart,
    label: String? = null,
) {
    showDetails(label, true) {
        fieldLink("Material", call, state, part.material)
        showOptionalFill(part.fill)
    }
}

// edit

fun HtmlBlockTag.editColorItemPart(
    state: State,
    part: ColorItemPart,
    param: String,
    label: String? = null,
) {
    showDetails(label, true) {
        selectMaterial(state, part.material, combine(param, MATERIAL))
        selectOptionalColor(part.color, combine(param, COLOR))
    }
}

fun HtmlBlockTag.editFillItemPart(
    state: State,
    part: FillItemPart,
    param: String,
    label: String? = null,
) {
    showDetails(label, true) {
        selectMaterial(state, part.material, combine(param, MATERIAL))
        selectOptionalFill(part.fill, combine(param, FILL))
    }
}

// parse

fun parseColorItemPart(parameters: Parameters, param: String) = ColorItemPart(
    parseMaterialId(parameters, combine(param, MATERIAL)),
    parse<Color>(parameters, combine(param, COLOR))
)

fun parseFillItemPart(parameters: Parameters, param: String) = FillItemPart(
    parseMaterialId(parameters, combine(param, MATERIAL)),
    parseOptionalFill(parameters, combine(param, FILL))
)