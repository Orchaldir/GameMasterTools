package at.orchaldir.gm.app.html.item.equipment.data

import at.orchaldir.gm.app.MAIN
import at.orchaldir.gm.app.SKIRT_STYLE
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.parse
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.util.part.editFillLookupItemPart
import at.orchaldir.gm.app.html.util.part.editItemPart
import at.orchaldir.gm.app.html.util.part.parseFillLookupItemPart
import at.orchaldir.gm.app.html.util.part.parseItemPart
import at.orchaldir.gm.app.html.util.part.showFillLookupItemPart
import at.orchaldir.gm.app.html.util.part.showItemPart
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Skirt
import at.orchaldir.gm.core.model.item.equipment.style.SkirtStyle
import at.orchaldir.gm.core.model.util.part.CLOTHING_MATERIALS
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showSkirt(
    call: ApplicationCall,
    state: State,
    skirt: Skirt,
) {
    field("Style", skirt.style)
    showItemPart(call, state, skirt.main)
}

// edit

fun HtmlBlockTag.editSkirt(
    state: State,
    skirt: Skirt,
) {
    selectValue("Style", SKIRT_STYLE, SkirtStyle.entries, skirt.style)
    editItemPart(state, skirt.main, MAIN, allowedTypes = CLOTHING_MATERIALS)
}

// parse

fun parseSkirt(parameters: Parameters): Skirt = Skirt(
    parseItemPart(parameters, MAIN),
    parse(parameters, SKIRT_STYLE, SkirtStyle.Sheath),
)