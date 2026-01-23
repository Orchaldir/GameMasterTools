package at.orchaldir.gm.app.html.item.equipment.data

import at.orchaldir.gm.app.LINE
import at.orchaldir.gm.app.MAIN
import at.orchaldir.gm.app.SIZE
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.item.equipment.style.editLineStyle
import at.orchaldir.gm.app.html.item.equipment.style.parseLineStyle
import at.orchaldir.gm.app.html.item.equipment.style.showLineStyle
import at.orchaldir.gm.app.html.parse
import at.orchaldir.gm.app.html.rpg.combat.parseRangedWeaponStats
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.util.part.editFillLookupItemPart
import at.orchaldir.gm.app.html.util.part.parseFillLookupItemPart
import at.orchaldir.gm.app.html.util.part.showFillLookupItemPart
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Sling
import at.orchaldir.gm.core.model.util.Size
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showSling(
    call: ApplicationCall,
    state: State,
    sling: Sling,
) {
    field("Size", sling.size)
    showLineStyle(call, state, sling.cord, "Cord")
    showFillLookupItemPart(call, state, sling.cradle, "Cradle")
}

// edit

fun HtmlBlockTag.editSling(
    state: State,
    sling: Sling,
) {
    selectValue("Size", SIZE, Size.entries, sling.size)
    editLineStyle(state, sling.cord, "Cord", LINE)
    editFillLookupItemPart(state, sling.cradle, MAIN, "Cradle")
}

// parse

fun parseSling(parameters: Parameters) = Sling(
    parse(parameters, SIZE, Size.Medium),
    parseLineStyle(parameters, LINE),
    parseFillLookupItemPart(parameters, MAIN),
    parseRangedWeaponStats(parameters),
)