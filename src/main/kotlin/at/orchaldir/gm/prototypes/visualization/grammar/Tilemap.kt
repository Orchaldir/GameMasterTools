package at.orchaldir.gm.prototypes.visualization.grammar

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.part.MadeFromWood
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.visualization.BrickPattern
import at.orchaldir.gm.core.model.visualization.BrickPatternGrammar
import at.orchaldir.gm.core.model.visualization.RectangularShapeGrammar
import at.orchaldir.gm.core.model.visualization.SquareGrid
import at.orchaldir.gm.utils.map.MapSize2d
import at.orchaldir.gm.utils.map.TileMap2d
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.ZERO_DISTANCE
import at.orchaldir.gm.utils.renderer.TileMap2dRenderer
import at.orchaldir.gm.utils.renderer.svg.SvgBuilder
import at.orchaldir.gm.visualization.grammar.Borders
import at.orchaldir.gm.visualization.grammar.GrammarRenderState
import at.orchaldir.gm.visualization.grammar.visualizeShapeGrammar
import java.io.File

fun main() {
    val grammar = BrickPatternGrammar(
        RectangularShapeGrammar(
            MadeFromWood(color = Color.Gray),
        ),
        SquareGrid(10),
        BrickPattern.BasketWeaveSingle,
        2,
    )
    val tileMap = TileMap2d(MapSize2d(4, 3), grammar)
    val tileMapRenderer = TileMap2dRenderer(Distance.fromMeters(1.0f), ZERO_DISTANCE)
    val svgBuilder = SvgBuilder(tileMapRenderer.calculateMapSize(tileMap))

    tileMapRenderer.render(tileMap) { index, x, y, aabb, grammar ->
        val renderState = GrammarRenderState(State(), svgBuilder, LINE_OPTIONS)

        visualizeShapeGrammar(renderState, grammar, aabb, Borders(false, x, y))
    }

    File("grammar-tilemap.svg").writeText(svgBuilder.finish().export())
}

