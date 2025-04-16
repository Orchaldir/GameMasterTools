package at.orchaldir.gm.app.html.model.item.equipment

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.model.item.*
import at.orchaldir.gm.app.html.selectInt
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Coat
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.BODY
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

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
    field("Pocket Style", data.pocketStyle)
    showFillItemPart(call, state, data.main, "Main")
}

private fun BODY.showOpeningStyle(
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

            is Zipper -> showColorItemPart(call, state, openingStyle.part, "Zipper")
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
    showColorItemPart(call, state, buttonColumn.button.part, "Button")
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
    selectPocketStyle(JacketPocketStyle.entries, data.pocketStyle)
    editFillItemPart(state, data.main, MAIN)
}

private fun FORM.selectOpeningStyle(state: State, openingStyle: OpeningStyle) {
    showDetails("Opening Style", true) {
        selectValue("Type", combine(OPENING, STYLE), OpeningType.entries, openingStyle.getType(), true)

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
}

private fun HtmlBlockTag.selectButtons(state: State, buttonColumn: ButtonColumn) {
    selectInt("Button Count", buttonColumn.count.toInt(), 1, 20, 1, combine(BUTTON, NUMBER), true)
    selectValue("Button Size", combine(BUTTON, SIZE), Size.entries, buttonColumn.button.size, true)
    editColorItemPart(state, buttonColumn.button.part, BUTTON, "Button")
}

// parse

fun parseCoat(parameters: Parameters) = Coat(
    parse(parameters, LENGTH, OuterwearLength.Hip),
    parse(parameters, combine(NECKLINE, STYLE), NecklineStyle.DeepV),
    parse(parameters, combine(SLEEVE, STYLE), SleeveStyle.Long),
    parseOpeningStyle(parameters),
    parse(parameters, combine(POCKET, STYLE), JacketPocketStyle.None),
    parseFillItemPart(parameters, MAIN),
)

private fun parseOpeningStyle(parameters: Parameters): OpeningStyle {
    val type = parse(parameters, combine(OPENING, STYLE), OpeningType.NoOpening)

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

