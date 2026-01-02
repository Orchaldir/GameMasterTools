package at.orchaldir.gm.app.html.item.equipment.style

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.part.editColorSchemeItemPart
import at.orchaldir.gm.app.html.util.part.parseColorSchemeItemPart
import at.orchaldir.gm.app.html.util.part.showColorSchemeItemPart
import at.orchaldir.gm.app.html.combine
import at.orchaldir.gm.app.html.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.Size
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

            is Zipper -> showColorSchemeItemPart(call, state, openingStyle.part, "Zipper")
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
    showColorSchemeItemPart(call, state, buttonColumn.button.part, "Button")
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

            is Zipper -> editColorSchemeItemPart(state, openingStyle.part, ZIPPER, "Zipper")
        }
    }
}

private fun HtmlBlockTag.selectButtons(state: State, buttonColumn: ButtonColumn) {
    selectInt("Button Count", buttonColumn.count.toInt(), 1, 20, 1, combine(BUTTON, NUMBER))
    selectValue("Button Size", combine(BUTTON, SIZE), Size.entries, buttonColumn.button.size)
    editColorSchemeItemPart(state, buttonColumn.button.part, BUTTON, "Button")
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

fun HtmlBlockTag.selectMaterial(
    state: State,
    materialId: MaterialId,
    param: String = MATERIAL,
    label: String = "Material",
) {
    selectElement(state, label, param, state.getMaterialStorage().getAll(), materialId)
}

// parse

fun parseOpeningStyle(parameters: Parameters): OpeningStyle {
    val type = parse(parameters, combine(OPENING, STYLE), OpeningType.NoOpening)

    return when (type) {
        OpeningType.NoOpening -> NoOpening
        OpeningType.SingleBreasted -> SingleBreasted(parseButtonColumn(parameters))
        OpeningType.DoubleBreasted -> DoubleBreasted(
            parseButtonColumn(parameters),
            parse(parameters, SPACE_BETWEEN_COLUMNS, Size.Medium)
        )

        OpeningType.Zipper -> Zipper(parseColorSchemeItemPart(parameters, ZIPPER))
    }
}

private fun parseButtonColumn(parameters: Parameters) = ButtonColumn(
    Button(
        parse(parameters, combine(BUTTON, SIZE), Size.Medium),
        parseColorSchemeItemPart(parameters, BUTTON),
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

