package at.orchaldir.gm.app.html.item.equipment.style

import at.orchaldir.gm.app.GRIP
import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.SHAPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.part.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.style.*
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showSwordGrip(
    call: ApplicationCall,
    state: State,
    grip: SwordGrip,
) {
    showDetails("Grip") {
        field("Type", grip.getType())

        when (grip) {
            is SimpleSwordGrip -> {
                field("Shape", grip.shape)
                showFillLookupItemPart(call, state, grip.part)
            }

            is BoundSwordGrip -> {
                field("Rows", grip.rows)
                showColorSchemeItemPart(call, state, grip.part)
            }
        }
    }
}

// edit

fun HtmlBlockTag.editSwordGrip(
    state: State,
    grip: SwordGrip,
    param: String = GRIP,
) {
    showDetails("Grip", true) {
        selectValue("Type", param, SwordGripType.entries, grip.getType())

        when (grip) {
            is SimpleSwordGrip -> {
                selectValue(
                    "Shape",
                    combine(param, SHAPE),
                    SwordGripShape.entries,
                    grip.shape,
                )
                editFillLookupItemPart(state, grip.part, param)
            }

            is BoundSwordGrip -> {
                selectInt(
                    "Rows",
                    grip.rows,
                    MIN_SWORD_GRIP_ROWS,
                    MIN_SWORD_GRIP_ROWS,
                    1,
                    combine(param, NUMBER),
                )
                editColorSchemeItemPart(state, grip.part, param)
            }
        }
    }
}


// parse

fun parseSwordGrip(
    parameters: Parameters,
    param: String = GRIP,
) = when (parse(parameters, param, SwordGripType.Simple)) {
    SwordGripType.Simple -> SimpleSwordGrip(
        parse(parameters, combine(param, SHAPE), SwordGripShape.Straight),
        parseFillLookupItemPart(parameters, param),
    )

    SwordGripType.Bound -> BoundSwordGrip(
        parseInt(parameters, combine(param, NUMBER), DEFAULT_SWORD_GRIP_ROWS),
        parseColorSchemeItemPart(parameters, param),
    )
}
