package at.orchaldir.gm.app.html.item.equipment

import at.orchaldir.gm.app.MAIN
import at.orchaldir.gm.app.STYLE
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.util.part.editFillLookupItemPart
import at.orchaldir.gm.app.html.util.part.parseFillLookupItemPart
import at.orchaldir.gm.app.html.util.part.showFillLookupItemPart
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Socks
import at.orchaldir.gm.core.model.item.equipment.style.SocksStyle
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showSocks(
    call: ApplicationCall,
    state: State,
    socks: Socks,
) {
    field("Style", socks.style)
    showFillLookupItemPart(call, state, socks.main, "Main")
}

// edit

fun HtmlBlockTag.editSocks(
    state: State,
    socks: Socks,
) {
    selectValue("Style", STYLE, SocksStyle.entries, socks.style)
    editFillLookupItemPart(state, socks.main, MAIN, "Main")
}

// parse

fun parseSocks(parameters: Parameters): Socks = Socks(
    parse(parameters, STYLE, SocksStyle.Quarter),
    parseFillLookupItemPart(parameters, MAIN),
)