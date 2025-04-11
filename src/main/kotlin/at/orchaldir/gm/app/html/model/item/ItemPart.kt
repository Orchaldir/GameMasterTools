package at.orchaldir.gm.app.html.model.item

import at.orchaldir.gm.app.COLOR
import at.orchaldir.gm.app.FILL
import at.orchaldir.gm.app.MATERIAL
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.item.equipment.selectMaterial
import at.orchaldir.gm.app.html.model.parseMaterialId
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.ItemPart
import at.orchaldir.gm.core.model.util.Fill
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showItemPart(
    call: ApplicationCall,
    state: State,
    part: ItemPart,
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

fun HtmlBlockTag.editItemPart(
    state: State,
    part: ItemPart,
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

fun parseItemPart(parameters: Parameters, param: String) = ItemPart(
    parseOptionalFill(parameters, combine(param, FILL)),
    parseMaterialId(parameters, combine(param, MATERIAL))
)