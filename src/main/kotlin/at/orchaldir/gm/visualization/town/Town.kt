package at.orchaldir.gm.visualization.town

import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.world.terrain.HillTerrain
import at.orchaldir.gm.core.model.world.terrain.MountainTerrain
import at.orchaldir.gm.core.model.world.terrain.PlainTerrain
import at.orchaldir.gm.core.model.world.terrain.RiverTerrain
import at.orchaldir.gm.core.model.world.town.StreetTile
import at.orchaldir.gm.core.model.world.town.Town
import at.orchaldir.gm.core.model.world.town.TownTile
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.renderer.LinkRenderer
import at.orchaldir.gm.utils.renderer.NoBorder
import at.orchaldir.gm.utils.renderer.TileMap2dRenderer
import at.orchaldir.gm.utils.renderer.svg.Svg
import at.orchaldir.gm.utils.renderer.svg.SvgBuilder

fun visualizeTown(
    town: Town,
    streets: Boolean = true,
    linkLookup: (Int, TownTile) -> String? = { _, _ -> null },
): Svg {
    val tileMapRenderer = TileMap2dRenderer(Distance(20.0f), Distance(1.0f))
    val svgBuilder = SvgBuilder(tileMapRenderer.calculateMapSize(town.map))

    tileMapRenderer.renderWithLinks(svgBuilder, town.map, TownTile::getColor, linkLookup)

    if (streets) {
        tileMapRenderer.render(town.map) { _, _, _, aabb, tile ->
            if (tile.construction is StreetTile) {
                renderStreet(svgBuilder, aabb)
            }
        }
    }

    return svgBuilder.finish()
}

fun TownTile.getColor() = when (terrain) {
    is HillTerrain -> Color.SaddleBrown
    is MountainTerrain -> Color.Gray
    PlainTerrain -> Color.Green
    is RiverTerrain -> Color.Blue
}

fun renderStreet(renderer: LinkRenderer, tile: AABB, color: Color = Color.Gray) {
    val style = NoBorder(color.toRender())
    renderer.renderRectangle(tile.shrink(Factor(0.5f)), style)
}