package at.orchaldir.gm.app.html.model.item.equipment

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.item.editColorItemPart
import at.orchaldir.gm.app.html.model.item.parseColorItemPart
import at.orchaldir.gm.app.html.model.item.showColorItemPart
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.selector.util.sortMaterial
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
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

fun FORM.selectOpeningStyle(state: State, openingStyle: OpeningStyle) {
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

            is Zipper -> editColorItemPart(state, openingStyle.part, ZIPPER, "Zipper")
        }
    }
}

private fun HtmlBlockTag.selectButtons(state: State, buttonColumn: ButtonColumn) {
    selectInt("Button Count", buttonColumn.count.toInt(), 1, 20, 1, combine(BUTTON, NUMBER), true)
    selectValue("Button Size", combine(BUTTON, SIZE), Size.entries, buttonColumn.button.size)
    editColorItemPart(state, buttonColumn.button.part, BUTTON, "Button")
}


fun FORM.selectNecklineStyle(options: Collection<NecklineStyle>, current: NecklineStyle) {
    selectValue("Neckline Style", combine(NECKLINE, STYLE), options, current)
}

fun FORM.selectSleeveStyle(options: Collection<SleeveStyle>, current: SleeveStyle) {
    selectValue("Sleeve Style", combine(SLEEVE, STYLE), options, current)
}

fun FORM.selectPocketStyle(options: Collection<PocketStyle>, current: PocketStyle) {
    selectValue("Pocket Style", combine(POCKET, STYLE), options, current)
}

fun HtmlBlockTag.selectMaterial(
    state: State,
    materialId: MaterialId,
    param: String = MATERIAL,
    label: String = "Material",
) {
    selectElement(state, label, param, state.sortMaterial(), materialId)
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

fun parseSleeveStyle(
    parameters: Parameters,
    neckline: NecklineStyle,
) = if (neckline.supportsSleeves()) {
    parse(parameters, combine(SLEEVE, STYLE), SleeveStyle.Long)
} else {
    SleeveStyle.None
}

