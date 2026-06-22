package at.orchaldir.gm.app.html.visualization

import at.orchaldir.gm.app.BRICK
import at.orchaldir.gm.app.HORIZONTAL
import at.orchaldir.gm.app.VERTICAL
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.visualization.BrickSelection
import at.orchaldir.gm.core.model.visualization.BrickSelectionType
import at.orchaldir.gm.core.model.visualization.HorizontalAndVerticalBricks
import at.orchaldir.gm.core.model.visualization.UniformBricks
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
            is UniformBricks -> showShapeGrammar(call, state, selection.brick, "Brick")
            is HorizontalAndVerticalBricks -> {
                showShapeGrammar(call, state, selection.horizontal, "Horizontal Brick")
                showShapeGrammar(call, state, selection.vertical, "Vertical Brick")
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
) {
    val selectionParam = combine(param, BRICK)

    showDetails(label, true) {
        selectValue(
            "Type",
            selectionParam,
            BrickSelectionType.entries,
            selection.getType(),
        )

        when (selection) {
            is UniformBricks -> editShapeGrammar(
                state,
                selection.brick,
                combine(selectionParam, HORIZONTAL),
                "Brick",
            )
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

    return when (parse(parameters, param, BrickSelectionType.Uniform)) {
        BrickSelectionType.Uniform -> UniformBricks(
            parseShapeGrammar(state, parameters, combine(param, HORIZONTAL)),
        )
        BrickSelectionType.HorizontalAndVertical -> HorizontalAndVerticalBricks(
            parseShapeGrammar(state, parameters, combine(param, HORIZONTAL)),
            parseShapeGrammar(state, parameters, combine(param, VERTICAL)),
        )
    }
}
