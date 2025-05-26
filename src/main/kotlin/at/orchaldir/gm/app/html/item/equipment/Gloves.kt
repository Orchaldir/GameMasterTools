package at.orchaldir.gm.app.html.item.equipment

import at.orchaldir.gm.app.GLOVES
import at.orchaldir.gm.app.MAIN
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.util.part.editFillLookupItemPart
import at.orchaldir.gm.app.html.util.part.parseFillLookupItemPart
import at.orchaldir.gm.app.html.util.part.showFillLookupItemPart
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Gloves
import at.orchaldir.gm.core.model.item.equipment.style.GloveStyle
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showGloves(
    call: ApplicationCall,
    state: State,
    gloves: Gloves,
) {
    field("Style", gloves.style)
    showFillLookupItemPart(call, state, gloves.main, "Main")
}

// edit

fun FORM.editGloves(
    state: State,
    data: Gloves,
) {
    selectValue("Style", GLOVES, GloveStyle.entries, data.style)
    editFillLookupItemPart(state, data.main, MAIN, "Main")
}

// parse

fun parseGloves(parameters: Parameters): Gloves = Gloves(
    parse(parameters, GLOVES, GloveStyle.Hand),
    parseFillLookupItemPart(parameters, MAIN),
)
