package at.orchaldir.gm.app.html.item.equipment.style

import at.orchaldir.gm.app.GRIP
import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.SHAPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.part.editItemPart
import at.orchaldir.gm.app.html.util.part.parseItemPart
import at.orchaldir.gm.app.html.util.part.showItemPart
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.GRIP_MATERIALS
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.part.ItemPartType
import at.orchaldir.gm.core.model.util.part.LINE_MATERIALS
import at.orchaldir.gm.core.model.util.part.SOLID_MATERIALS
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showGrip(
    call: ApplicationCall,
    state: State,
    grip: Grip,
) {
    showDetails("Grip") {
        field("Type", grip.getType())

        when (grip) {
            is SimpleGrip -> {
                field("Shape", grip.shape)
                showItemPart(call, state, grip.part)
            }

            is BoundGrip -> {
                field("Rows", grip.rows)
                showItemPart(call, state, grip.part)
            }
        }
    }
}

// edit

fun HtmlBlockTag.editGrip(
    state: State,
    grip: Grip,
    param: String = GRIP,
) {
    showDetails("Grip", true) {
        selectValue("Type", param, GripType.entries, grip.getType())

        when (grip) {
            is SimpleGrip -> {
                selectValue(
                    "Shape",
                    combine(param, SHAPE),
                    GripShape.entries,
                    grip.shape,
                )
                editItemPart(state, grip.part, param, allowedTypes = GRIP_MATERIALS)
            }

            is BoundGrip -> {
                selectInt(
                    "Rows",
                    grip.rows,
                    MIN_GRIP_ROWS,
                    MIN_GRIP_ROWS,
                    1,
                    combine(param, NUMBER),
                )
                editItemPart(state, grip.part, param, allowedTypes = LINE_MATERIALS)
            }
        }
    }
}


// parse

fun parseGrip(
    parameters: Parameters,
    param: String = GRIP,
) = when (parse(parameters, param, GripType.Simple)) {
    GripType.Simple -> SimpleGrip(
        parse(parameters, combine(param, SHAPE), GripShape.Straight),
        parseItemPart(parameters, param, GRIP_MATERIALS),
    )

    GripType.Bound -> BoundGrip(
        parseInt(parameters, combine(param, NUMBER), DEFAULT_GRIP_ROWS),
        parseItemPart(parameters, param, LINE_MATERIALS),
    )
}
