package at.orchaldir.gm.app.html.item.equipment.style

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.part.editItemPart
import at.orchaldir.gm.app.html.util.part.parseItemPart
import at.orchaldir.gm.app.html.util.part.showItemPart
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.BUCKLE_MATERIALS
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.part.SOLID_MATERIALS
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showBuckle(
    call: ApplicationCall,
    state: State,
    buckle: Buckle,
) {
    showDetails("Buckle", true) {
        when (buckle) {
            NoBuckle -> doNothing()
            is SimpleBuckle -> {
                field("Shape", buckle.shape)
                field("Size", buckle.size)
                showItemPart(call, state, buckle.part)
            }
        }
    }
}

// edit

fun HtmlBlockTag.editBuckle(
    state: State,
    buckle: Buckle,
) {
    showDetails("Buckle", true) {
        selectValue("Type", combine(BUCKLE, TYPE), BuckleType.entries, buckle.getType())

        when (buckle) {
            NoBuckle -> doNothing()
            is SimpleBuckle -> {
                selectValue("Shape", combine(BUCKLE, SHAPE), BuckleShape.entries, buckle.shape)
                selectValue("Size", combine(BUCKLE, SIZE), Size.entries, buckle.size)
                editItemPart(
                    state,
                    buckle.part,
                    combine(BUCKLE, MAIN),
                    allowedTypes = BUCKLE_MATERIALS,
                )
            }
        }
    }
}

// parse

fun parseBuckle(parameters: Parameters): Buckle {
    val type = parse(parameters, combine(BUCKLE, TYPE), BuckleType.NoBuckle)

    return when (type) {
        BuckleType.NoBuckle -> NoBuckle
        BuckleType.Simple -> SimpleBuckle(
            parse(parameters, combine(BUCKLE, SHAPE), BuckleShape.Rectangle),
            parse(parameters, combine(BUCKLE, SIZE), Size.Small),
            parseItemPart(parameters, combine(BUCKLE, MAIN), BUCKLE_MATERIALS),
        )
    }
}
