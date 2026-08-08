package at.orchaldir.gm.app.html.visualization

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.visualization.*
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showBrickSelection(
    call: ApplicationCall,
    state: State,
    selection: BrickSelection,
    label: String = "Bricks",
) {
    showDetails(label, true) {
        field("Type", selection.getType())

        when (selection) {
            is AlternateRows -> showListWithIndex(selection.rows) { index, row ->
                showShapeGrammar(call, state, row, "${index + 1}.Row")
            }

            is HorizontalAndVerticalBricks -> {
                showShapeGrammar(call, state, selection.horizontal, "Horizontal Brick")
                showShapeGrammar(call, state, selection.vertical, "Vertical Brick")
            }

            is UniformBricks -> showShapeGrammar(call, state, selection.brick, "Brick")
            is BrickSelectionWithCenter -> {
                showShapeGrammar(call, state, selection.center, "Center")
                showBrickSelection(call, state, selection.border, "Border")
            }
        }
    }
}

// edit

fun HtmlBlockTag.editBrickSelection(
    state: State,
    selection: BrickSelection,
    param: String,
    label: String = "Bricks",
    allowed: Collection<BrickSelectionType> = BrickSelectionType.entries,
) {
    val selectionParam = combine(param, BRICK)

    showDetails(label, true) {
        selectValue(
            "Type",
            selectionParam,
            allowed,
            selection.getType(),
        )

        when (selection) {
            is AlternateRows -> showListWithIndex(selection.rows) { index, row ->
                editShapeGrammar(
                    state,
                    row,
                    combine(selectionParam, index),
                    "${index + 1}.Row",
                )
            }

            is HorizontalAndVerticalBricks -> {
                editShapeGrammar(
                    state,
                    selection.horizontal,
                    combine(selectionParam, HORIZONTAL),
                    "Horizontal Brick",
                )
                editShapeGrammar(
                    state,
                    selection.vertical,
                    combine(selectionParam, VERTICAL),
                    "Vertical Brick",
                )
            }

            is UniformBricks -> editShapeGrammar(
                state,
                selection.brick,
                combine(selectionParam, HORIZONTAL),
                "Brick",
            )

            is BrickSelectionWithCenter -> {
                editShapeGrammar(
                    state,
                    selection.center,
                    combine(selectionParam, CENTER),
                    "Center",
                )
                editBrickSelection(
                    state,
                    selection.border,
                    combine(selectionParam, BORDER),
                    "Border",
                    allowed - BrickSelectionType.WithCenter,
                )
            }
        }
    }
}

// parse

fun parseBrickSelection(
    state: State,
    parameters: Parameters,
    param: String,
): BrickSelection {
    val selectionParam = combine(param, BRICK)

    return when (parse(parameters, selectionParam, BrickSelectionType.Uniform)) {
        BrickSelectionType.HorizontalAndVertical -> HorizontalAndVerticalBricks(
            parseShapeGrammar(state, parameters, combine(selectionParam, HORIZONTAL)),
            parseShapeGrammar(state, parameters, combine(selectionParam, VERTICAL)),
        )

        BrickSelectionType.Rows -> AlternateRows(
            parseList(parameters, selectionParam, 2) { _, rowParam ->
                parseShapeGrammar(state, parameters, rowParam)
            },
        )

        BrickSelectionType.Uniform -> UniformBricks(
            parseShapeGrammar(state, parameters, combine(selectionParam, HORIZONTAL)),
        )

        BrickSelectionType.WithCenter -> BrickSelectionWithCenter(
            parseShapeGrammar(state, parameters, combine(selectionParam, CENTER)),
            parseBrickSelection(state, parameters, combine(selectionParam, BORDER)),
        )
    }
}
