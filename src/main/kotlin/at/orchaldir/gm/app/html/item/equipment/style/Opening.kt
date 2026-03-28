package at.orchaldir.gm.app.html.item.equipment.style

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.item.common.editSewingPattern
import at.orchaldir.gm.app.html.item.common.parseSewing
import at.orchaldir.gm.app.html.item.common.showSewingPattern
import at.orchaldir.gm.app.html.util.part.editItemPart
import at.orchaldir.gm.app.html.util.part.parseItemPart
import at.orchaldir.gm.app.html.util.part.showItemPart
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.BUTTON_MATERIALS
import at.orchaldir.gm.core.model.item.equipment.ZIPPER_MATERIALS
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.DETAILS
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showOpening(
    call: ApplicationCall,
    state: State,
    opening: Opening,
) {
    showDetails("Opening Style") {
        field("Type", opening.javaClass.simpleName)

        when (opening) {
            NoOpening -> doNothing()
            is SingleBreasted -> showButtons(call, state, opening.buttons)
            is DoubleBreasted -> {
                showButtons(call, state, opening.buttons)
                field("Space between Columns", opening.width)
            }

            is LaceUp -> {
                showSewingPattern(call, state, opening.pattern, "Pattern")
                field("Width", opening.width)
            }
            is Zipper -> showItemPart(call, state, opening.main, "Zipper")
        }
    }
}

private fun HtmlBlockTag.showButtons(
    call: ApplicationCall,
    state: State,
    buttonColumn: ButtonColumn,
) {
    field("Button Count", buttonColumn.count.toString())
    field("Button Size", buttonColumn.button.size)
    showItemPart(call, state, buttonColumn.button.main, "Button")
}

// edit

fun HtmlBlockTag.editOpening(
    state: State,
    opening: Opening,
    allowedTypes: Collection<OpeningType> = OpeningType.entries,
    param: String = OPENING,
) {
    showDetails("Opening Style", true) {
        selectValue(
            "Type", 
            combine(param, STYLE),
            allowedTypes,
            opening.getType(),
        )

        when (opening) {
            NoOpening -> doNothing()
            is SingleBreasted -> selectButtons(state, opening.buttons, param)
            is DoubleBreasted -> {
                selectButtons(state, opening.buttons, param)
                selectWidth(param, opening.width, "Space between Columns")
            }

            is LaceUp -> {
                editSewingPattern(
                    state,
                    opening.pattern,
                    combine(param, SEWING),
                    "Pattern",
                )
                selectWidth(param, opening.width, "Width")
            }

            is Zipper -> editItemPart(
                state,
                opening.main,
                combine(param, ZIPPER),
                "Zipper",
                ZIPPER_MATERIALS,
            )
        }
    }
}

private fun DETAILS.selectWidth(
    param: String,
    width: Size,
    label: String,
) {
    selectValue(
        label,
        combine(param, SPACE_BETWEEN_COLUMNS),
        Size.entries,
        width,
    )
}

private fun HtmlBlockTag.selectButtons(
    state: State, 
    buttonColumn: ButtonColumn,
    param: String,
) {
    selectInt(
        "Button Count", 
        buttonColumn.count.toInt(), 
        2,
        20, 
        1, 
        combine(param, BUTTON, NUMBER),
    )
    selectValue(
        "Button Size", 
        combine(param, BUTTON, SIZE), 
        Size.entries, 
        buttonColumn.button.size,
    )
    editItemPart(
        state,
        buttonColumn.button.main,
        combine(param, BUTTON),
        "Button",
        BUTTON_MATERIALS,
    )
}


fun HtmlBlockTag.selectSleeveStyle(options: Collection<SleeveStyle>, current: SleeveStyle) {
    selectValue("Sleeve Style", combine(SLEEVE, STYLE), options, current)
}

fun HtmlBlockTag.selectPocketStyle(options: Collection<PocketStyle>, current: PocketStyle) {
    selectValue("Pocket Style", combine(POCKET, STYLE), options, current)
}

// parse

fun parseOpening(
    state: State,
    parameters: Parameters,
    param: String = OPENING,
    default: OpeningType = OpeningType.NoOpening,
): Opening {
    val type = parse(parameters, combine(param, STYLE), default)

    return when (type) {
        OpeningType.NoOpening -> NoOpening
        OpeningType.SingleBreasted -> SingleBreasted(
            parseButtonColumn(state, parameters, param),
        )
        OpeningType.DoubleBreasted -> DoubleBreasted(
            parseButtonColumn(state, parameters, param),
            parseWidth(parameters, param),
        )
        OpeningType.LaceUp -> LaceUp(
            parseSewing(state, parameters, combine(param, SEWING)),
            parseWidth(parameters, param),
        )
        OpeningType.Zipper -> Zipper(
            parseItemPart(state, parameters, combine(param, ZIPPER), ZIPPER_MATERIALS),
        )
    }
}

private fun parseWidth(parameters: Parameters, param: String) =
    parse(parameters, combine(param, SPACE_BETWEEN_COLUMNS), Size.Medium)

private fun parseButtonColumn(
    state: State,
    parameters: Parameters,
    param: String,
) = ButtonColumn(
    Button(
        parse(parameters, combine(param, BUTTON, SIZE), Size.Medium),
        parseItemPart(state, parameters, combine(param, BUTTON), BUTTON_MATERIALS),
    ),
    parameters[combine(param, BUTTON, NUMBER)]?.toUByte() ?: 1u,
)

fun parseSleeveStyle(
    parameters: Parameters,
    neckline: Neckline,
) = if (neckline.supportsSleeves()) {
    parse(parameters, combine(SLEEVE, STYLE), SleeveStyle.Long)
} else {
    SleeveStyle.None
}

