package at.orchaldir.gm.app.html.item.equipment

import at.orchaldir.gm.app.HEIGHT
import at.orchaldir.gm.app.LINE
import at.orchaldir.gm.app.MAIN
import at.orchaldir.gm.app.html.item.equipment.style.editLineStyle
import at.orchaldir.gm.app.html.item.equipment.style.parseLineStyle
import at.orchaldir.gm.app.html.item.equipment.style.showLineStyle
import at.orchaldir.gm.app.html.rpg.combat.parseRangedWeaponStats
import at.orchaldir.gm.app.html.util.math.fieldFactor
import at.orchaldir.gm.app.html.util.math.parseFactor
import at.orchaldir.gm.app.html.util.math.selectFactor
import at.orchaldir.gm.app.html.util.part.editFillLookupItemPart
import at.orchaldir.gm.app.html.util.part.parseFillLookupItemPart
import at.orchaldir.gm.app.html.util.part.showFillLookupItemPart
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Sling
import at.orchaldir.gm.utils.math.QUARTER
import at.orchaldir.gm.utils.math.THREE_QUARTER
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showSling(
    call: ApplicationCall,
    state: State,
    sling: Sling,
) {
    fieldFactor("Height", sling.length)
    showLineStyle(call, state, sling.cord, "Cord")
    showFillLookupItemPart(call, state, sling.cradle, "Cradle")
}

// edit

fun HtmlBlockTag.editSling(
    state: State,
    sling: Sling,
) {
    selectFactor(
        "LENGTH",
        HEIGHT,
        sling.length,
        QUARTER,
        THREE_QUARTER,
    )
    editLineStyle(state, sling.cord, "Cord", LINE)
    editFillLookupItemPart(state, sling.cradle, MAIN, "Cradle")
}

// parse

fun parseSling(parameters: Parameters) = Sling(
    parseFactor(parameters, HEIGHT),
    parseLineStyle(parameters, LINE),
    parseFillLookupItemPart(parameters, MAIN),
    parseRangedWeaponStats(parameters),
)