package at.orchaldir.gm.app.html.model.item.equipment

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.selectWeight
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseMaterialId
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.*
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.unit.Weight
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.util.*
import kotlinx.html.*

// show

fun BODY.showBelt(
    call: ApplicationCall,
    state: State,
    belt: Belt,
) {
    showBuckle(call, state, belt.buckle)
    showFill(belt.fill)
    fieldLink("Material", call, state, belt.material)
}

fun BODY.showBuckle(
    call: ApplicationCall,
    state: State,
    buckle: Buckle,
) {
    when (buckle) {
        NoBuckle -> doNothing()
        is SimpleBuckle -> {
            field("Shape", buckle.shape)
            field("Size", buckle.size)
            showFill(buckle.fill)
            fieldLink("Material", call, state, buckle.material)
        }
    }
}

// edit

fun FORM.editBelt(
    state: State,
    belt: Belt,
) {
    editBuckle(state, belt.buckle)
    selectFill(belt.fill)
    selectMaterial(state, belt.material)
}

fun FORM.editBuckle(
    state: State,
    buckle: Buckle,
) {
    selectValue("Buckle Type", combine(BUCKLE, TYPE), BuckleType.entries, buckle.getType(), true)

    when (buckle) {
        NoBuckle -> doNothing()
        is SimpleBuckle -> {
            selectValue("Shape", combine(BUCKLE, SHAPE), BuckleShape.entries, buckle.shape, true)
            selectValue("Size", combine(BUCKLE, SIZE), Size.entries, buckle.size, true)
            selectFill(buckle.fill, combine(BUCKLE, FILL))
            selectMaterial(state, buckle.material, combine(BUCKLE, MATERIAL))
        }
    }
}

// parse

fun parseBelt(parameters: Parameters) = Belt(
    parseBuckle(parameters),
    parseFill(parameters),
    parseMaterialId(parameters, MATERIAL),
)

private fun parseBuckle(parameters: Parameters): Buckle {
    val type = parse(parameters, combine(BUCKLE, TYPE), BuckleType.NoBuckle)

    return when (type) {
        BuckleType.NoBuckle -> NoBuckle
        BuckleType.Simple -> SimpleBuckle(
            parse(parameters, combine(BUCKLE, SHAPE), BuckleShape.Rectangle),
            parse(parameters, combine(BUCKLE, SIZE), Size.Small),
            parseFill(parameters, combine(BUCKLE, FILL)),
            parseMaterialId(parameters, combine(BUCKLE, MATERIAL)),
        )
    }
}
