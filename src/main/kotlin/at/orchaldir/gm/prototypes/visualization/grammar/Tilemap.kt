package at.orchaldir.gm.prototypes.visualization.grammar

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.part.MadeFromWood
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.visualization.BrickPattern
import at.orchaldir.gm.core.model.visualization.BrickPatternGrammar
import at.orchaldir.gm.core.model.visualization.RectangularShapeGrammar
import at.orchaldir.gm.core.model.visualization.RowsAndColumns
import at.orchaldir.gm.utils.map.MapSize2d
import at.orchaldir.gm.utils.map.TileMap2d
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.ZERO_DISTANCE
import at.orchaldir.gm.utils.renderer.TileMap2dRenderer
import at.orchaldir.gm.utils.renderer.model.BorderOnly
import at.orchaldir.gm.utils.renderer.svg.SvgBuilder
import at.orchaldir.gm.visualization.grammar.GrammarRenderState
import at.orchaldir.gm.visualization.grammar.visualizeShapeGrammar
import java.io.File

fun main() {
    val grammar = BrickPatternGrammar(
        RectangularShapeGrammar(
            MadeFromWood(color = Color.Gray),
        ),
        RowsAndColumns(8, 8),
        BrickPattern.PythagoreanTiling,
        2,
    )
    val tileMap = TileMap2d(MapSize2d(3, 2), grammar)
    val tileSize = Distance.fromMeters(1.0f)
    val tileMapRenderer = TileMap2dRenderer(tileSize, ZERO_DISTANCE)
    val tileMapSize = tileMapRenderer.calculateMapSize(tileMap)
    val svgBuilder = SvgBuilder(tileMapSize.plus(tileSize * 2))
    val start = Point2d.square(tileSize)

    tileMapRenderer.render(tileMap, start) { _, aabb, borders, grammar ->
        val renderState = GrammarRenderState(State(), svgBuilder, LINE_OPTIONS)

        visualizeShapeGrammar(renderState, grammar, aabb, borders)
    }

    svgBuilder.getLayer().renderRectangle(AABB(start, tileMapSize), BorderOnly(RED_LINE))

    File("grammar-tilemap.svg").writeText(svgBuilder.finish().export())
}

