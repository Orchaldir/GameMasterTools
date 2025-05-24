package at.orchaldir.gm.app.html.item.equipment

import at.orchaldir.gm.app.MAIN
import at.orchaldir.gm.app.SKIRT_STYLE
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.item.editFillLookupItemPart
import at.orchaldir.gm.app.html.item.parseFillLookupItemPart
import at.orchaldir.gm.app.html.item.showFillLookupItemPart
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Skirt
import at.orchaldir.gm.core.model.item.equipment.style.SkirtStyle
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showSkirt(
    call: ApplicationCall,
    state: State,
    skirt: Skirt,
) {
    field("Style", skirt.style)
    showFillLookupItemPart(call, state, skirt.main, "Main")
}

// edit

fun FORM.editSkirt(
    state: State,
    skirt: Skirt,
) {
    selectValue("Style", SKIRT_STYLE, SkirtStyle.entries, skirt.style)
    editFillLookupItemPart(state, skirt.main, MAIN, "Main")
}

// parse

fun parseSkirt(parameters: Parameters): Skirt = Skirt(
    parse(parameters, SKIRT_STYLE, SkirtStyle.Sheath),
    parseFillLookupItemPart(parameters, MAIN),
)