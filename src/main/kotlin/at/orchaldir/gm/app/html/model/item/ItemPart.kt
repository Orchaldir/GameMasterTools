package at.orchaldir.gm.app.html.model.item

import at.orchaldir.gm.app.COLOR
import at.orchaldir.gm.app.FILL
import at.orchaldir.gm.app.MATERIAL
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.item.equipment.selectMaterial
import at.orchaldir.gm.app.html.model.parseMaterialId
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.ColorItemPart
import at.orchaldir.gm.core.model.item.FillItemPart
import at.orchaldir.gm.core.model.util.Color
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
    if (label != null) {
        fieldOptionalColor(part.color, "$label Color")
        fieldLink("$label Material", call, state, part.material)
    } else {
        fieldOptionalColor(part.color)
        fieldLink("Material", call, state, part.material)
    }
}

fun HtmlBlockTag.showFillItemPart(
    call: ApplicationCall,
    state: State,
    part: FillItemPart,
    label: String? = null,
) {
    if (label != null) {
        showOptionalFill("$label Fill", part.fill)
        fieldLink("$label Material", call, state, part.material)
    } else {
        showOptionalFill("Fill", part.fill)
        fieldLink("Material", call, state, part.material)
    }
}

// edit

fun HtmlBlockTag.editColorItemPart(
    state: State,
    part: ColorItemPart,
    param: String,
    label: String? = null,
) {
    if (label != null) {
        selectOptionalColor(part.color, combine(param, COLOR), "$label Color")
        selectMaterial(state, part.material, combine(param, MATERIAL), "$label Material")
    } else {
        selectOptionalColor(part.color, combine(param, COLOR))
        selectMaterial(state, part.material, combine(param, MATERIAL))
    }
}

fun HtmlBlockTag.editFillItemPart(
    state: State,
    part: FillItemPart,
    param: String,
    label: String? = null,
) {
    if (label != null) {
        selectOptionalFill("$label Fill", part.fill, combine(param, FILL))
        selectMaterial(state, part.material, combine(param, MATERIAL), "$label Material")
    } else {
        selectOptionalFill("Fill", part.fill, combine(param, FILL))
        selectMaterial(state, part.material, combine(param, MATERIAL))
    }
}

// parse

fun parseColorItemPart(parameters: Parameters, param: String) = ColorItemPart(
    parse<Color>(parameters, combine(param, COLOR)),
    parseMaterialId(parameters, combine(param, MATERIAL))
)

fun parseFillItemPart(parameters: Parameters, param: String) = FillItemPart(
    parseOptionalFill(parameters, combine(param, FILL)),
    parseMaterialId(parameters, combine(param, MATERIAL))
)