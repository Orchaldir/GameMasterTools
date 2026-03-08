package at.orchaldir.gm.app.html.item.equipment.data

import at.orchaldir.gm.app.HEIGHT
import at.orchaldir.gm.app.MAIN
import at.orchaldir.gm.app.SHAPE
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.item.equipment.style.editBowGrip
import at.orchaldir.gm.app.html.item.equipment.style.parseBowGrip
import at.orchaldir.gm.app.html.item.equipment.style.showBowGrip
import at.orchaldir.gm.app.html.parse
import at.orchaldir.gm.app.html.rpg.combat.parseRangedWeaponStats
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.util.math.fieldFactor
import at.orchaldir.gm.app.html.util.math.parseFactor
import at.orchaldir.gm.app.html.util.math.selectFactor
import at.orchaldir.gm.app.html.util.part.editFillLookupItemPart
import at.orchaldir.gm.app.html.util.part.editItemPart
import at.orchaldir.gm.app.html.util.part.parseFillLookupItemPart
import at.orchaldir.gm.app.html.util.part.parseItemPart
import at.orchaldir.gm.app.html.util.part.showFillLookupItemPart
import at.orchaldir.gm.app.html.util.part.showItemPart
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Bow
import at.orchaldir.gm.core.model.item.equipment.style.BowShape
import at.orchaldir.gm.core.model.util.part.SOLID_MATERIALS
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.THIRD
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showBow(
    call: ApplicationCall,
    state: State,
    bow: Bow,
) {
    field("Shape", bow.shape)
    fieldFactor("Height", bow.height)
    showBowGrip(call, state, bow.grip)
    showItemPart(call, state, bow.main)
}

// edit

fun HtmlBlockTag.editBow(
    state: State,
    bow: Bow,
) {
    selectValue(
        "Shape",
        SHAPE,
        BowShape.entries,
        bow.shape,
    )
    selectFactor(
        "Height",
        HEIGHT,
        bow.height,
        THIRD,
        FULL,
    )
    editBowGrip(state, bow.grip)
    editItemPart(state, bow.main, MAIN, allowedTypes = SOLID_MATERIALS)
}

// parse

fun parseBow(parameters: Parameters) = Bow(
    parse(parameters, SHAPE, BowShape.Straight),
    parseFactor(parameters, HEIGHT),
    parseBowGrip(parameters),
    parseItemPart(parameters, MAIN),
    parseRangedWeaponStats(parameters),
)