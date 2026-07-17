package at.orchaldir.gm.prototypes.visualization.grammar

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.core.model.util.part.MadeFromStone
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.util.render.RandomColor
import at.orchaldir.gm.core.model.visualization.*
import at.orchaldir.gm.prototypes.visualization.addNames

fun main() {
    val randomColors = MadeFromStone(color = RandomColor(OneOf(listOf(Color.Green, Color.Red))))
    val blue = RectangularShapeGrammar(Color.Blue)
    val grey = RectangularShapeGrammar(Color.Gray)
    val random = RectangularShapeGrammar(randomColors)
    val yellow = RectangularShapeGrammar(Color.Yellow)
    val horizontalAndVertical = HorizontalAndVerticalBricks(blue, yellow)
    val selections = listOf(
        Pair("Uniform", UniformBricks(grey)),
        Pair("Random", UniformBricks(random)),
        Pair("H and V", horizontalAndVertical),
        Pair("Rows", AlternateRows(listOf(grey, blue, yellow))),
        Pair("With Center", BrickSelectionWithCenter(grey, horizontalAndVertical)),
    )
    renderGrammarTable(
        State(),
        "grammar-brick-selections.svg",
        GRAMMAR_SIZE,
        selections,
        addNames(BrickPattern.entries),
        ::createGrammar,
    )
}

private fun createGrammar(pattern: BrickPattern, bricks: BrickSelection) = BrickPatternGrammar(
    bricks,
    SquareGrid(20),
    pattern,
    3,
)