package at.orchaldir.gm.app.html.model.item

import at.orchaldir.gm.app.COLOR
import at.orchaldir.gm.app.MATERIAL
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.item.equipment.selectMaterial
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.Part
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showPart(
    call: ApplicationCall,
    state: State,
    part: Part,
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

// edit

fun HtmlBlockTag.editPart(
    state: State,
    part: Part,
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
