package at.orchaldir.gm.visualization.town

import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.model.world.street.StreetId
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
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.renderer.LinkRenderer
import at.orchaldir.gm.utils.renderer.NoBorder
import at.orchaldir.gm.utils.renderer.Renderer
import at.orchaldir.gm.utils.renderer.TileMap2dRenderer
import at.orchaldir.gm.utils.renderer.svg.Svg
import at.orchaldir.gm.utils.renderer.svg.SvgBuilder

fun visualizeTerrain(
    town: Town,
    linkLookup: (Int, TownTile) -> String? = { _, _ -> null },
): Svg {
    val tileMapRenderer = TileMap2dRenderer(Distance(20.0f), Distance(1.0f))
    val svgBuilder = SvgBuilder(tileMapRenderer.calculateMapSize(town.map))

    tileMapRenderer.renderWithLinks(svgBuilder, town.map, TownTile::getColor, linkLookup)

    return svgBuilder.finish()
}

fun visualizeTown(
    town: Town,
    buildings: List<Building> = emptyList(),
    buildingLinkLookup: (Building) -> String? = { _ -> null },
): Svg {
    val tileMapRenderer = TileMap2dRenderer(Distance(20.0f), Distance(1.0f))
    val svgBuilder = SvgBuilder(tileMapRenderer.calculateMapSize(town.map))

    tileMapRenderer.render(svgBuilder, town.map, TownTile::getColor)

    visualizeBuildings(svgBuilder, tileMapRenderer, town, buildings, buildingLinkLookup)

    visualizeStreetsComplex(svgBuilder, tileMapRenderer, town)

    return svgBuilder.finish()
}

fun visualizeBuildings(
    renderer: LinkRenderer,
    tileRenderer: TileMap2dRenderer,
    town: Town,
    buildings: List<Building>,
    linkLookup: (Building) -> String? = { _ -> null },
) {
    buildings.forEach { building ->
        val link = linkLookup(building)

        if (link != null) {
            renderer.link(link)
            renderBuilding(renderer, tileRenderer, town, building, Color.Black)
            renderer.closeLink()
        } else {
            renderBuilding(renderer, tileRenderer, town, building, Color.Black)
        }
    }
}

fun visualizeStreetsComplex(
    renderer: Renderer,
    tileRenderer: TileMap2dRenderer,
    town: Town,
) {
    visualizeStreetsComplex(tileRenderer, town) { aabb, _, _ -> renderStreet(renderer, aabb) }
}

fun visualizeStreetsComplex(
    tileRenderer: TileMap2dRenderer,
    town: Town,
    render: (AABB, StreetId, Int) -> Unit,
) {
    val right = Point2d(tileRenderer.tileSize.value / 2, 0.0f)
    val down = Point2d(0.0f, tileRenderer.tileSize.value / 2)

    tileRenderer.render(town.map) { index, x, y, aabb, tile ->
        if (tile.construction is StreetTile) {
            if (town.checkTile(x + 1, y) { it.construction is StreetTile }) {
                val rightAABB = aabb + right
                render(rightAABB, tile.construction.street, index)
            }

            if (town.checkTile(x, y + 1) { it.construction is StreetTile }) {
                val downAABB = aabb + down
                render(downAABB, tile.construction.street, index)
            }

            render(aabb, tile.construction.street, index)
        }
    }
}

// render

fun renderBuilding(
    renderer: Renderer,
    tileRenderer: TileMap2dRenderer,
    town: Town,
    building: Building,
    color: Color = Color.Black,
) {
    val start = tileRenderer.calculateTilePosition(town.map, building.lot.tileIndex)
    val size = tileRenderer.calculateLotSize(building.lot.size)
    val aabb = AABB(start, size).shrink(Factor(0.5f))
    val style = NoBorder(color.toRender())

    renderer.renderRectangle(aabb, style)
}

fun renderStreet(renderer: Renderer, tile: AABB, color: Color = Color.Gray) {
    val style = NoBorder(color.toRender())
    renderer.renderRectangle(tile.shrink(Factor(0.5f)), style)
}

fun TownTile.getColor() = when (terrain) {
    is HillTerrain -> Color.SaddleBrown
    is MountainTerrain -> Color.Gray
    PlainTerrain -> Color.Green
    is RiverTerrain -> Color.Blue
}