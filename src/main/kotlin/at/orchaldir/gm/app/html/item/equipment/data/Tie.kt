package at.orchaldir.gm.app.html.item.equipment.data

import at.orchaldir.gm.app.KNOT
import at.orchaldir.gm.app.MAIN
import at.orchaldir.gm.app.SIZE
import at.orchaldir.gm.app.STYLE
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.parse
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.util.part.editItemPart
import at.orchaldir.gm.app.html.util.part.parseItemPart
import at.orchaldir.gm.app.html.util.part.showItemPart
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Tie
import at.orchaldir.gm.core.model.item.equipment.style.TieStyle
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.part.CLOTHING_MATERIALS
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showTie(
    call: ApplicationCall,
    state: State,
    tie: Tie,
) {
    field("Style", tie.style)
    field("Size", tie.size)
    showItemPart(call, state, tie.main, "Main")
    showItemPart(call, state, tie.knot, "Knot")
}

// edit

fun HtmlBlockTag.editTie(
    state: State,
    tie: Tie,
) {
    selectValue("Style", STYLE, TieStyle.entries, tie.style)
    selectValue("Size", SIZE, Size.entries, tie.size)
    editItemPart(state, tie.main, MAIN, "Main", CLOTHING_MATERIALS)
    editItemPart(state, tie.knot, KNOT, "Knot", CLOTHING_MATERIALS)
}

// parse

fun parseTie(parameters: Parameters) = Tie(
    parse(parameters, STYLE, TieStyle.Tie),
    parse(parameters, SIZE, Size.Medium),
    parseItemPart(parameters, MAIN),
    parseItemPart(parameters, KNOT),
)