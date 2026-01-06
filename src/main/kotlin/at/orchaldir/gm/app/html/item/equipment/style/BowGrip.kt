package at.orchaldir.gm.app.html.item.equipment.style

import at.orchaldir.gm.app.GRIP
import at.orchaldir.gm.app.SIZE
import at.orchaldir.gm.app.html.combine
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.parse
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.style.SimpleBowGrip
import at.orchaldir.gm.core.model.item.equipment.style.BowGrip
import at.orchaldir.gm.core.model.item.equipment.style.BowGripType
import at.orchaldir.gm.core.model.item.equipment.style.NoBowGrip
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showBowGrip(
    call: ApplicationCall,
    state: State,
    grip: BowGrip,
) {
    showDetails("Bow Grip") {
        field("Type", grip.getType())

        when (grip) {
            NoBowGrip -> doNothing()
            is SimpleBowGrip -> {
                field("Size", grip.size)
                showGrip(call, state, grip.grip)
            }
        }
    }
}

// edit

fun HtmlBlockTag.editBowGrip(
    state: State,
    grip: BowGrip,
) {
    showDetails("Bow Grip", true) {
        selectValue("Type", GRIP, BowGripType.entries, grip.getType())

        when (grip) {
            NoBowGrip -> doNothing()
            is SimpleBowGrip -> {
                selectValue(
                    "Size",
                    combine(GRIP, SIZE),
                    Size.entries,
                    grip.size,
                )
                editGrip(state, grip.grip, combine(GRIP, GRIP))
            }
        }
    }
}


// parse

fun parseBowGrip(
    parameters: Parameters,
) = when (parse(parameters, GRIP, BowGripType.Simple)) {
    BowGripType.None -> NoBowGrip
    BowGripType.Simple -> SimpleBowGrip(
        parse(parameters, combine(GRIP, SIZE), Size.Medium),
        parseGrip(parameters, combine(GRIP, GRIP)),
    )
}
