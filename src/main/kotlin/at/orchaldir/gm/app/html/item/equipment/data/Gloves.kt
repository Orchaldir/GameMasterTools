package at.orchaldir.gm.app.html.item.equipment.data

import at.orchaldir.gm.app.GLOVES
import at.orchaldir.gm.app.MAIN
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.parse
import at.orchaldir.gm.app.html.rpg.combat.parseArmorStats
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.util.part.editItemPart
import at.orchaldir.gm.app.html.util.part.parseItemPart
import at.orchaldir.gm.app.html.util.part.showItemPart
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.GLOVES_MATERIALS
import at.orchaldir.gm.core.model.item.equipment.Gloves
import at.orchaldir.gm.core.model.item.equipment.style.GloveStyle
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showGloves(
    call: ApplicationCall,
    state: State,
    gloves: Gloves,
) {
    field("Style", gloves.style)
    showItemPart(call, state, gloves.main)
}

// edit

fun HtmlBlockTag.editGloves(
    state: State,
    data: Gloves,
) {
    selectValue("Style", GLOVES, GloveStyle.entries, data.style)
    editItemPart(state, data.main, MAIN, allowedTypes = GLOVES_MATERIALS)
}

// parse

fun parseGloves(
    state: State,
    parameters: Parameters,
): Gloves = Gloves(
    parse(parameters, GLOVES, GloveStyle.Hand),
    parseItemPart(state, parameters, MAIN, GLOVES_MATERIALS),
    parseArmorStats(parameters),
)
