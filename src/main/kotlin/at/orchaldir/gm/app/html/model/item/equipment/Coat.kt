package at.orchaldir.gm.app.html.model.item.equipment

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.item.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Belt
import at.orchaldir.gm.core.model.item.equipment.Coat
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.BODY
import kotlinx.html.DETAILS
import kotlinx.html.FORM

// show

fun BODY.showCoat(
    call: ApplicationCall,
    state: State,
    data: Coat,
) {
    field("Length", data.length)
    field("Neckline Style", data.necklineStyle)
    field("Sleeve Style", data.sleeveStyle)
    showOpeningStyle(call, state, data.openingStyle)
    showFillItemPart(call, state, data.cloth, "Cloth")
}

private fun BODY.showOpeningStyle(
    call: ApplicationCall,
    state: State,
    openingStyle: OpeningStyle,
) {
    field("Opening Style", openingStyle.javaClass.simpleName)
    when (openingStyle) {
        NoOpening -> doNothing()
        is SingleBreasted -> showButtons(call, state, openingStyle.buttons)
        is DoubleBreasted -> {
            showButtons(call, state, openingStyle.buttons)
            field("Space between Columns", openingStyle.spaceBetweenColumns)
        }

        is Zipper -> showColorItemPart(call, state, openingStyle.part, "Zipper")
    }
}

private fun BODY.showButtons(
    call: ApplicationCall,
    state: State,
    buttonColumn: ButtonColumn,
) {
    field("Button Count", buttonColumn.count.toString())
    showColorItemPart(call, state, buttonColumn.button.part, "Button")
    field("Button Size", buttonColumn.button.size)
}

// edit

fun FORM.editCoat(
    state: State,
    data: Coat,
) {
    selectValue("Length", LENGTH, OuterwearLength.entries, data.length, true)
    selectNecklineStyle(NECKLINES_WITH_SLEEVES, data.necklineStyle)
    selectSleeveStyle(SleeveStyle.entries, data.sleeveStyle)
    selectOpeningStyle(state, data.openingStyle)
    editFillItemPart(state, data.cloth, CLOTH)
}

private fun FORM.selectOpeningStyle(state: State, openingStyle: OpeningStyle) {
    selectValue("Opening Style", OPENING_STYLE, OpeningType.entries, openingStyle.getType(), true)

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
                true
            )
        }

        is Zipper -> editColorItemPart(state, openingStyle.part, ZIPPER, "Zipper")
    }
}

private fun FORM.selectButtons(state: State, buttonColumn: ButtonColumn) {
    selectInt("Button Count", buttonColumn.count.toInt(), 1, 20, 1, combine(BUTTON, NUMBER), true)
    editColorItemPart(state, buttonColumn.button.part, BUTTON, "Button")
    selectValue("Button Size", combine(BUTTON, SIZE), Size.entries, buttonColumn.button.size, true)
}

// parse

fun parseCoat(parameters: Parameters) = Coat(
    parse(parameters, LENGTH, OuterwearLength.Hip),
    parse(parameters, NECKLINE_STYLE, NecklineStyle.DeepV),
    parse(parameters, SLEEVE_STYLE, SleeveStyle.Long),
    parseOpeningStyle(parameters),
    parseFillItemPart(parameters, CLOTH),
)

private fun parseOpeningStyle(parameters: Parameters): OpeningStyle {
    val type = parse(parameters, OPENING_STYLE, OpeningType.NoOpening)

    return when (type) {
        OpeningType.NoOpening -> NoOpening
        OpeningType.SingleBreasted -> SingleBreasted(parseButtonColumn(parameters))
        OpeningType.DoubleBreasted -> DoubleBreasted(
            parseButtonColumn(parameters),
            parse(parameters, SPACE_BETWEEN_COLUMNS, Size.Medium)
        )

        OpeningType.Zipper -> Zipper(parseColorItemPart(parameters, ZIPPER))
    }
}

private fun parseButtonColumn(parameters: Parameters) = ButtonColumn(
    Button(
        parse(parameters, combine(BUTTON, SIZE), Size.Medium),
        parseColorItemPart(parameters, BUTTON),
    ),
    parameters[combine(BUTTON, NUMBER)]?.toUByte() ?: 1u,
)

