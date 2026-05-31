package at.orchaldir.gm.app.html.visualization

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.part.editItemPart
import at.orchaldir.gm.app.html.util.part.parseItemPart
import at.orchaldir.gm.app.html.util.part.showItemPart
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.part.ItemPartType
import at.orchaldir.gm.core.model.visualization.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.shape.RectangularShape
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showGrammar(
    call: ApplicationCall,
    state: State,
    grammar: Grammar,
    label: String = "Grammar",
) {
    showDetails(label, true) {
        field("Type", grammar.getType())

        when (grammar) {
            DoNothingGrammar -> doNothing()
            is RectangularShapeGrammar -> {
                field("Shape", grammar.shape)
                showItemPart(call, state, grammar.part)
            }
            is SingleBrickGrammar -> {
                showGridSize(grammar.size)
                field("Pattern", grammar.pattern)
                showGrammar(call, state, grammar.brick, "Brick")
                
                if (grammar.pattern != SingleBrickPattern.Grid) {
                    field("Brick Length", grammar.length)
                }
            }
        }
    }
}

// edit

fun HtmlBlockTag.editGrammar(
    state: State,
    grammar: Grammar,
    param: String = GRAMMAR,
    label: String = "Grammar",
) {
    showDetails(label, true) {
        selectValue(
            "Type",
            param,
            GrammarType.entries,
            grammar.getType(),
        )

        when (grammar) {
            DoNothingGrammar -> doNothing()
            is RectangularShapeGrammar -> {
                selectValue(
                    "Shape",
                    combine(param, SHAPE),
                    RectangularShape.entries,
                    grammar.shape,
                )
                editItemPart(
                    state,
                    grammar.part,
                    combine(param, MATERIAL),
                )
            }
            is SingleBrickGrammar -> {
                editGridSize(
                    grammar.size,
                    combine(param, SIZE),
                    MIN_GRID_SIZE,
                    MAX_GRID_SIZE,
                )
                field("Pattern", grammar.pattern)
                selectValue(
                    "Pattern",
                    combine(param, PATTERN),
                    SingleBrickPattern.entries,
                    grammar.pattern,
                )
                editGrammar(
                    state,
                    grammar.brick,
                    combine(param, SUB),
                    "Brick",
                )

                if (grammar.pattern != SingleBrickPattern.Grid) {
                    field("Brick Length", grammar.length)
                    selectInt(
                        "Brick Length",
                        grammar.length,
                        MIN_BRICK_LENGTH,
                        MAX_BRICK_LENGTH,
                        1,
                        combine(param, LENGTH),
                    )
                }
            }
        }
    }
}

// parse

fun parseGrammar(
    state: State,
    parameters: Parameters,
    param: String = GRAMMAR,
): Grammar {
    return when (parse(parameters, param, GrammarType.RectangularShape)) {
        GrammarType.RectangularShape -> RectangularShapeGrammar(
            parse(parameters, combine(param, SHAPE), RectangularShape.Rectangle),
            parseItemPart(
                state,
                parameters,
                combine(param, MATERIAL),
                ItemPartType.Wood,
            ),
        )
        GrammarType.SingleBrick -> SingleBrickGrammar(
            parseGrammar(state, parameters, combine(param, SUB)),
            parseGridSize(parameters, combine(param, SIZE)),
            parse(
                parameters,
                combine(param, PATTERN),
                SingleBrickPattern.Running,
            ),
            parseInt(parameters, combine(param, LENGTH), DEFAULT_BRICK_LENGTH),
        )
        GrammarType.DoNothing -> DoNothingGrammar
    }
}
