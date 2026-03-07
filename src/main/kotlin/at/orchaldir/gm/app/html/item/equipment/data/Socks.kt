package at.orchaldir.gm.app.html.item.equipment.data

import at.orchaldir.gm.app.MAIN
import at.orchaldir.gm.app.STYLE
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
import at.orchaldir.gm.core.model.item.equipment.Socks
import at.orchaldir.gm.core.model.item.equipment.style.SocksStyle
import at.orchaldir.gm.core.model.util.part.CLOTHING_MATERIALS
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showSocks(
    call: ApplicationCall,
    state: State,
    socks: Socks,
) {
    field("Style", socks.style)
    showItemPart(call, state, socks.main)
}

// edit

fun HtmlBlockTag.editSocks(
    state: State,
    socks: Socks,
) {
    selectValue("Style", STYLE, SocksStyle.entries, socks.style)
    editItemPart(state, socks.main, MAIN, allowedTypes = CLOTHING_MATERIALS)
}

// parse

fun parseSocks(parameters: Parameters): Socks = Socks(
    parse(parameters, STYLE, SocksStyle.Quarter),
    parseItemPart(parameters, MAIN, CLOTHING_MATERIALS),
)