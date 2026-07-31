package at.orchaldir.gm.app.html.visualization

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.math.fieldFactor
import at.orchaldir.gm.app.html.util.math.parseFactor
import at.orchaldir.gm.app.html.util.math.selectFactor
import at.orchaldir.gm.app.html.util.part.editItemPart
import at.orchaldir.gm.app.html.util.part.parseItemPart
import at.orchaldir.gm.app.html.util.part.showItemPart
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.part.ItemPartType
import at.orchaldir.gm.core.model.visualization.*
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showShapeGrammar(
    call: ApplicationCall,
    state: State,
    grammar: ShapeGrammar,
    label: String = "Grammar",
) {
    showDetails(label, true) {
        field("Type", grammar.getType())

        when (grammar) {
            is AshlarGrammar -> {
                showGridSize(grammar.size)
                showShapeGrammar(call, state, grammar.brick, "Brick")
                field("Brick Width", grammar.brickWidth)
                field("Brick Height", grammar.brickHeight)
            }
            is BrickPatternGrammar -> {
                showGridSize(grammar.size)
                field("Pattern", grammar.pattern)
                showBrickSelection(call, state, grammar.bricks)

                if (grammar.pattern != BrickPattern.Grid) {
                    field("Brick Length", grammar.length)
                }
            }

            DoNothingShapeGrammar -> doNothing()

            is RectangularShapeGrammar -> {
                field("Shape", grammar.shape)
                showItemPart(call, state, grammar.part)
            }

            is ShrinkGrammar -> {
                fieldFactor("Shrink Factor", grammar.factor)
                showShapeGrammar(call, state, grammar.grammar, "Shrunken")
            }
        }
    }
}

// edit

fun HtmlBlockTag.editShapeGrammar(
    state: State,
    grammar: ShapeGrammar,
    param: String = GRAMMAR,
    label: String = "Grammar",
) {
    showDetails(label, true) {
        selectValue(
            "Type",
            param,
            ShapeGrammarType.entries,
            grammar.getType(),
        )

        when (grammar) {
            is AshlarGrammar -> {
                editGridSize(
                    grammar.size,
                    combine(param, SIZE),
                    MIN_GRID_SIZE,
                    MAX_GRID_SIZE,
                )
                editShapeGrammar(
                    state,
                    grammar.brick,
                    combine(param, BRICK),
                    "Brick",
                )
                selectInt(
                    "Brick Width",
                    grammar.brickWidth,
                    BRICK_SIZE_RANGE,
                    1,
                    combine(param, WIDTH),
                )
                selectInt(
                    "Brick Height",
                    grammar.brickHeight,
                    BRICK_SIZE_RANGE,
                    1,
                    combine(param, HEIGHT),
                )
            }
            is BrickPatternGrammar -> {
                editGridSize(
                    grammar.size,
                    combine(param, SIZE),
                    MIN_GRID_SIZE,
                    MAX_GRID_SIZE,
                )
                selectValue(
                    "Pattern",
                    combine(param, PATTERN),
                    BrickPattern.entries,
                    grammar.pattern,
                )
                editBrickSelection(
                    state,
                    grammar.bricks,
                    param,
                )

                if (grammar.pattern != BrickPattern.Grid) {
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

            DoNothingShapeGrammar -> doNothing()

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

            is ShrinkGrammar -> {
                selectFactor(
                    "Shrink Factor",
                    combine(param, SHRINK),
                    grammar.factor,
                    MIN_SHRINK_FACTOR,
                    MAX_SHRINK_FACTOR,
                )
                editShapeGrammar(
                    state,
                    grammar.grammar,
                    combine(param, SUB),
                    "Shrunken",
                )
            }
        }
    }
}

// parse

fun parseShapeGrammar(
    state: State,
    parameters: Parameters,
    param: String = GRAMMAR,
): ShapeGrammar {
    return when (parse(parameters, param, ShapeGrammarType.RectangularShape)) {
        ShapeGrammarType.Ashlar -> AshlarGrammar(
            parseGridSize(parameters, combine(param, SIZE)),
            parseShapeGrammar(state, parameters, combine(param, BRICK)),
            parseInt(parameters, combine(param, WIDTH)),
            parseInt(parameters, combine(param, HEIGHT)),
        )
        ShapeGrammarType.BrickPattern -> BrickPatternGrammar(
            parseBrickSelection(state, parameters, param),
            parseGridSize(parameters, combine(param, SIZE)),
            parse(
                parameters,
                combine(param, PATTERN),
                BrickPattern.Running,
            ),
            parseInt(parameters, combine(param, LENGTH), DEFAULT_BRICK_LENGTH),
        )

        ShapeGrammarType.DoNothing -> DoNothingShapeGrammar
        ShapeGrammarType.RectangularShape -> RectangularShapeGrammar(
            parseItemPart(
                state,
                parameters,
                combine(param, MATERIAL),
                ItemPartType.entries,
            ),
            parse(parameters, combine(param, SHAPE), RectangularShape.Rectangle),
        )

        ShapeGrammarType.Shrink -> ShrinkGrammar(
            parseShapeGrammar(state, parameters, combine(param, SUB)),
            parseFactor(parameters, combine(param, SHRINK), DEFAULT_SHRINK_FACTOR),
        )
    }
}
