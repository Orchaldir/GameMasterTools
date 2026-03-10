package at.orchaldir.gm.app.html.item.equipment.style

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.part.editItemPart
import at.orchaldir.gm.app.html.util.part.parseItemPart
import at.orchaldir.gm.app.html.util.part.showItemPart
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.BUTTON_MATERIALS
import at.orchaldir.gm.core.model.item.equipment.ZIPPER_MATERIALS
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.part.ItemPartType
import at.orchaldir.gm.core.model.util.part.SOLID_MATERIALS
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showOpeningStyle(
    call: ApplicationCall,
    state: State,
    openingStyle: OpeningStyle,
) {
    showDetails("Opening Style") {
        field("Type", openingStyle.javaClass.simpleName)

        when (openingStyle) {
            NoOpening -> doNothing()
            is SingleBreasted -> showButtons(call, state, openingStyle.buttons)
            is DoubleBreasted -> {
                showButtons(call, state, openingStyle.buttons)
                field("Space between Columns", openingStyle.spaceBetweenColumns)
            }

            is Zipper -> showItemPart(call, state, openingStyle.main, "Zipper")
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

fun HtmlBlockTag.selectOpeningStyle(state: State, openingStyle: OpeningStyle) {
    showDetails("Opening Style", true) {
        selectValue("Type", combine(OPENING, STYLE), OpeningType.entries, openingStyle.getType())

        when (openingStyle) {
            NoOpening -> doNothing()
            is SingleBreasted -> selectButtons(state, openingStyle.buttons)
            is DoubleBreasted -> {
                selectButtons(state, openingStyle.buttons)
                selectValue(
                    "Space between Columns",
                    SPACE_BETWEEN_COLUMNS,
                    Size.entries,
                    openingStyle.spaceBetweenColumns,
                )
            }

            is Zipper -> editItemPart(
                state,
                openingStyle.main,
                ZIPPER,
                "Zipper",
                ZIPPER_MATERIALS,
            )
        }
    }
}

private fun HtmlBlockTag.selectButtons(state: State, buttonColumn: ButtonColumn) {
    selectInt("Button Count", buttonColumn.count.toInt(), 1, 20, 1, combine(BUTTON, NUMBER))
    selectValue("Button Size", combine(BUTTON, SIZE), Size.entries, buttonColumn.button.size)
    editItemPart(
        state,
        buttonColumn.button.main,
        BUTTON,
        "Button",
        BUTTON_MATERIALS,
    )
}


fun HtmlBlockTag.selectNecklineStyle(options: Collection<NecklineStyle>, current: NecklineStyle) {
    selectValue("Neckline Style", combine(NECKLINE, STYLE), options, current)
}

fun HtmlBlockTag.selectSleeveStyle(options: Collection<SleeveStyle>, current: SleeveStyle) {
    selectValue("Sleeve Style", combine(SLEEVE, STYLE), options, current)
}

fun HtmlBlockTag.selectPocketStyle(options: Collection<PocketStyle>, current: PocketStyle) {
    selectValue("Pocket Style", combine(POCKET, STYLE), options, current)
}

// parse

fun parseOpeningStyle(
    state: State,
    parameters: Parameters,
): OpeningStyle {
    val type = parse(parameters, combine(OPENING, STYLE), OpeningType.NoOpening)

    return when (type) {
        OpeningType.NoOpening -> NoOpening
        OpeningType.SingleBreasted -> SingleBreasted(parseButtonColumn(state, parameters))
        OpeningType.DoubleBreasted -> DoubleBreasted(
            parseButtonColumn(state, parameters),
            parse(parameters, SPACE_BETWEEN_COLUMNS, Size.Medium)
        )

        OpeningType.Zipper -> Zipper(
            parseItemPart(state, parameters, ZIPPER, ZIPPER_MATERIALS),
        )
    }
}

private fun parseButtonColumn(
    state: State,
    parameters: Parameters,
) = ButtonColumn(
    Button(
        parse(parameters, combine(BUTTON, SIZE), Size.Medium),
        parseItemPart(state, parameters, BUTTON, BUTTON_MATERIALS),
    ),
    parameters[combine(BUTTON, NUMBER)]?.toUByte() ?: 1u,
)

fun parseSleeveStyle(
    parameters: Parameters,
    neckline: NecklineStyle,
) = if (neckline.supportsSleeves()) {
    parse(parameters, combine(SLEEVE, STYLE), SleeveStyle.Long)
} else {
    SleeveStyle.None
}

