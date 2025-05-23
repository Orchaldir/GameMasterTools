package at.orchaldir.gm.app.html.item.equipment

import at.orchaldir.gm.app.MAIN
import at.orchaldir.gm.app.NECKLINE
import at.orchaldir.gm.app.SKIRT_STYLE
import at.orchaldir.gm.app.STYLE
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.item.editFillLookupItemPart
import at.orchaldir.gm.app.html.item.parseFillLookupItemPart
import at.orchaldir.gm.app.html.item.showFillLookupItemPart
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Shirt
import at.orchaldir.gm.core.model.item.equipment.Skirt
import at.orchaldir.gm.core.model.item.equipment.Socks
import at.orchaldir.gm.core.model.item.equipment.style.NECKLINES_WITH_SLEEVES
import at.orchaldir.gm.core.model.item.equipment.style.NecklineStyle
import at.orchaldir.gm.core.model.item.equipment.style.SkirtStyle
import at.orchaldir.gm.core.model.item.equipment.style.SleeveStyle
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

fun FORM.editSocks(
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