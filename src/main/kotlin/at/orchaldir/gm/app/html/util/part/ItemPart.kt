package at.orchaldir.gm.app.html.util.part

import at.orchaldir.gm.app.COLOR
import at.orchaldir.gm.app.FILL
import at.orchaldir.gm.app.MATERIAL
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.economy.material.parseMaterialId
import at.orchaldir.gm.app.html.item.equipment.style.selectMaterial
import at.orchaldir.gm.app.html.util.color.*
import at.orchaldir.gm.app.html.combine
import at.orchaldir.gm.app.html.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.part.ColorItemPart
import at.orchaldir.gm.core.model.util.part.ColorSchemeItemPart
import at.orchaldir.gm.core.model.util.part.FillItemPart
import at.orchaldir.gm.core.model.util.part.FillLookupItemPart
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

fun HtmlBlockTag.showColorSchemeItemPart(
    call: ApplicationCall,
    state: State,
    part: ColorSchemeItemPart,
    label: String? = null,
) {
    showDetails(label, true) {
        fieldLink("Material", call, state, part.material)
        fieldColorLookup("Color", part.lookup)
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

fun HtmlBlockTag.showFillLookupItemPart(
    call: ApplicationCall,
    state: State,
    part: FillLookupItemPart,
    label: String? = null,
) {
    showDetails(label, true) {
        fieldLink("Material", call, state, part.material)
        showFillLookup(part.fill)
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

fun HtmlBlockTag.editColorSchemeItemPart(
    state: State,
    part: ColorSchemeItemPart,
    param: String,
    label: String? = null,
) {
    showDetails(label, true) {
        selectMaterial(state, part.material, combine(param, MATERIAL))
        editColorLookup("Color Lookup", part.lookup, param, Color.entries)
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

fun HtmlBlockTag.editFillLookupItemPart(
    state: State,
    part: FillLookupItemPart,
    param: String,
    label: String? = null,
) {
    showDetails(label, true) {
        selectMaterial(state, part.material, combine(param, MATERIAL))
        selectFillLookup(part.fill, combine(param, FILL))
    }
}

// parse

fun parseColorItemPart(parameters: Parameters, param: String) = ColorItemPart(
    parseMaterialId(parameters, combine(param, MATERIAL)),
    parse<Color>(parameters, combine(param, COLOR)),
)

fun parseColorSchemeItemPart(parameters: Parameters, param: String) = ColorSchemeItemPart(
    parseMaterialId(parameters, combine(param, MATERIAL)),
    parseColorLookup(parameters, param),
)

fun parseFillItemPart(parameters: Parameters, param: String) = FillItemPart(
    parseMaterialId(parameters, combine(param, MATERIAL)),
    parseOptionalFill(parameters, combine(param, FILL)),
)

fun parseFillLookupItemPart(parameters: Parameters, param: String) = FillLookupItemPart(
    parseMaterialId(parameters, combine(param, MATERIAL)),
    parseFillLookup(parameters, combine(param, FILL)),
)