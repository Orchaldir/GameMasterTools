package at.orchaldir.gm.app.html.item.equipment.style

import at.orchaldir.gm.app.GRIP
import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.SHAPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.part.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.style.*
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
                showFillLookupItemPart(call, state, grip.part)
            }

            is BoundGrip -> {
                field("Rows", grip.rows)
                showColorSchemeItemPart(call, state, grip.part)
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
                editFillLookupItemPart(state, grip.part, param)
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
                editColorSchemeItemPart(state, grip.part, param)
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
        parseFillLookupItemPart(parameters, param),
    )

    GripType.Bound -> BoundGrip(
        parseInt(parameters, combine(param, NUMBER), DEFAULT_GRIP_ROWS),
        parseColorSchemeItemPart(parameters, param),
    )
}
