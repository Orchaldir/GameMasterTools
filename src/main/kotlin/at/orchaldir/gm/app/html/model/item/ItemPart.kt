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
        fieldLink("$label Material", call, state, part.material)
        fieldOptionalColor(part.color, "$label Color")
    } else {
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
    if (label != null) {
        fieldLink("$label Material", call, state, part.material)
        showOptionalFill("$label Fill", part.fill)
    } else {
        fieldLink("Material", call, state, part.material)
        showOptionalFill("Fill", part.fill)
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
        selectMaterial(state, part.material, combine(param, MATERIAL), "$label Material")
        selectOptionalColor(part.color, combine(param, COLOR), "$label Color")
    } else {
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
    if (label != null) {
        selectMaterial(state, part.material, combine(param, MATERIAL), "$label Material")
        selectOptionalFill("$label Fill", part.fill, combine(param, FILL))
    } else {
        selectMaterial(state, part.material, combine(param, MATERIAL))
        selectOptionalFill("Fill", part.fill, combine(param, FILL))
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