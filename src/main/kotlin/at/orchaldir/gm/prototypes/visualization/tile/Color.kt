package at.orchaldir.gm.prototypes.visualization.tile

import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.util.render.Color.Red
import at.orchaldir.gm.utils.map.MapSize2d
import at.orchaldir.gm.utils.map.TileMap2d
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.renderer.TileMap2dRenderer
import at.orchaldir.gm.utils.renderer.svg.SvgBuilder
import java.io.File

fun main() {
    val tileMap = TileMap2d(MapSize2d(3, 2), listOf(1, 2, 1, 2, 1, 2))
    val tileMapRenderer = TileMap2dRenderer(Distance.fromMeters(20.0f), Distance.fromMeters(1.0f))
    val svgBuilder = SvgBuilder(tileMapRenderer.calculateMapSize(tileMap))

    tileMapRenderer.render(svgBuilder, tileMap) { tile ->
        if (tile == 1) {
            Red
        } else {
            Color.Green
        }
    }

    File("tileMap-color.svg").writeText(svgBuilder.finish().export())
}

