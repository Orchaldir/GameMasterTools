package at.orchaldir.gm.app.html.item.equipment

import at.orchaldir.gm.app.COLOR
import at.orchaldir.gm.app.MATERIAL
import at.orchaldir.gm.app.html.fieldColor
import at.orchaldir.gm.app.html.fieldLink
import at.orchaldir.gm.app.html.selectColor
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.util.Color
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showLook(
    call: ApplicationCall,
    state: State,
    color: Color,
    material: MaterialId,
    label: String? = null,
) {
    if (label != null) {
        fieldColor(color, "$label Color")
        fieldLink("$label Material", call, state, material)
    } else {
        fieldColor(color)
        fieldLink("Material", call, state, material)
    }
}

// edit

fun HtmlBlockTag.editLook(
    state: State,
    color: Color,
    material: MaterialId,
    param: String,
    label: String? = null,
) {
    if (label != null) {
        selectColor(color, combine(param, COLOR), "$label Color")
        selectMaterial(state, material, combine(param, MATERIAL), "$label Material")
    } else {
        selectColor(color, combine(param, COLOR))
        selectMaterial(state, material, combine(param, MATERIAL))
    }
}
