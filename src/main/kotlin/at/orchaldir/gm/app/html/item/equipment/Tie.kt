package at.orchaldir.gm.app.html.item.equipment

import at.orchaldir.gm.app.KNOT
import at.orchaldir.gm.app.MAIN
import at.orchaldir.gm.app.SIZE
import at.orchaldir.gm.app.STYLE
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.util.part.editFillLookupItemPart
import at.orchaldir.gm.app.html.util.part.parseFillLookupItemPart
import at.orchaldir.gm.app.html.util.part.showFillLookupItemPart
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Tie
import at.orchaldir.gm.core.model.item.equipment.style.TieStyle
import at.orchaldir.gm.core.model.util.Size
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showTie(
    call: ApplicationCall,
    state: State,
    tie: Tie,
) {
    field("Style", tie.style)
    field("Size", tie.size)
    showFillLookupItemPart(call, state, tie.main, "Main")
    showFillLookupItemPart(call, state, tie.knot, "Knot")
}

// edit

fun FORM.editTie(
    state: State,
    tie: Tie,
) {
    selectValue("Style", STYLE, TieStyle.entries, tie.style)
    selectValue("Size", SIZE, Size.entries, tie.size)
    editFillLookupItemPart(state, tie.main, MAIN, "Main")
    editFillLookupItemPart(state, tie.knot, KNOT, "Knot")
}

// parse

fun parseTie(parameters: Parameters) = Tie(
    parse(parameters, STYLE, TieStyle.Tie),
    parse(parameters, SIZE, Size.Medium),
    parseFillLookupItemPart(parameters, MAIN),
    parseFillLookupItemPart(parameters, KNOT),
)