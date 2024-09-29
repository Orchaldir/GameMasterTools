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
import at.orchaldir.gm.utils.renderer.Renderer
import at.orchaldir.gm.utils.renderer.model.NoBorder
import at.orchaldir.gm.utils.renderer.TileMap2dRenderer
import at.orchaldir.gm.utils.renderer.svg.Svg
import at.orchaldir.gm.utils.renderer.svg.SvgBuilder

private val DEFAULT_BUILDING_COLOR: (Building) -> Color = { _ -> Color.Black }
private val DEFAULT_STREET_COLOR: (StreetId, Int) -> Color = { _, _ -> Color.Gray }

data class TownRenderer(
    private val tileRenderer: TileMap2dRenderer,
    private val renderer: SvgBuilder,
    private val town: Town,
) {
    constructor(tileMapRenderer: TileMap2dRenderer, town: Town) : this(
        tileMapRenderer,
        SvgBuilder(tileMapRenderer.calculateMapSize(town.map)),
        town,
    )

    constructor(town: Town) : this(
        TileMap2dRenderer(Distance(20.0f), Distance(1.0f)),
        town,
    )

    fun renderTiles(
        colorLookup: (TownTile) -> Color = TownTile::getColor,
    ) {
        tileRenderer.render(renderer, town.map, colorLookup)
    }

    fun renderTilesWithLinks(
        colorLookup: (TownTile) -> Color = TownTile::getColor,
        linkLookup: (Int, TownTile) -> String?,
    ) {
        tileRenderer.renderWithLinks(renderer, town.map, colorLookup, linkLookup)
    }

    fun renderBuildings(
        buildings: List<Building>,
        colorLookup: (Building) -> Color = DEFAULT_BUILDING_COLOR,
    ) {
        val layer = renderer.getLayer()

        buildings.forEach { building ->
            val color = colorLookup(building)

            renderBuilding(layer, building, color)
        }
    }

    fun renderBuildings(
        buildings: List<Building>,
        colorLookup: (Building) -> Color = DEFAULT_BUILDING_COLOR,
        linkLookup: (Building) -> String?,
    ) {
        buildings.forEach { building ->
            val color = colorLookup(building)
            val link = linkLookup(building)

            if (link != null) {
                renderer.link(link) {
                    renderBuilding(it, building, color)
                }
            } else {
                renderBuilding(renderer.getLayer(), building, color)
            }
        }
    }

    fun renderStreets(
        colorLookup: (StreetId, Int) -> Color = DEFAULT_STREET_COLOR,
    ) {
        val layer = renderer.getLayer()

        renderStreets { aabb, streetId, index ->
            val color = colorLookup(streetId, index)
            renderStreet(layer, aabb, color)
        }
    }

    fun renderStreets(
        colorLookup: (StreetId, Int) -> Color = DEFAULT_STREET_COLOR,
        linkLookup: (StreetId, Int) -> String?,
    ) {
        renderStreets { aabb, streetId, index ->
            val color = colorLookup(streetId, index)
            val link = linkLookup(streetId, index)

            if (link != null) {
                renderer.link(link) {
                    renderStreet(it, aabb, color)
                }
            } else {
                renderStreet(renderer.getLayer(), aabb, color)
            }
        }
    }

    fun renderStreets(
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

    private fun renderBuilding(
        renderer: Renderer,
        building: Building,
        color: Color,
    ) {
        val start = tileRenderer.calculateTilePosition(town.map, building.lot.tileIndex)
        val size = tileRenderer.calculateLotSize(building.lot.size)
        val aabb = AABB(start, size).shrink(Factor(0.5f))
        val style = NoBorder(color.toRender())

        renderer.renderRectangle(aabb, style)
    }

    private fun renderStreet(renderer: Renderer, tile: AABB, color: Color) {
        val style = NoBorder(color.toRender())
        renderer.renderRectangle(tile.shrink(Factor(0.5f)), style)
    }

    fun finish() = renderer.finish()
}

fun visualizeTown(
    town: Town,
    buildings: List<Building> = emptyList(),
    tileColorLookup: (TownTile) -> Color = TownTile::getColor,
    tileLinkLookup: (Int, TownTile) -> String? = { _, _ -> null },
    buildingColorLookup: (Building) -> Color = DEFAULT_BUILDING_COLOR,
    buildingLinkLookup: (Building) -> String? = { _ -> null },
    streetColorLookup: (StreetId, Int) -> Color = DEFAULT_STREET_COLOR,
    streetLinkLookup: (StreetId, Int) -> String? = { _, _ -> null },
): Svg {
    val townRenderer = TownRenderer(town)

    townRenderer.renderTilesWithLinks(tileColorLookup, tileLinkLookup)
    townRenderer.renderBuildings(buildings, buildingColorLookup, buildingLinkLookup)
    townRenderer.renderStreets(streetColorLookup, streetLinkLookup)

    return townRenderer.finish()
}

fun TownTile.getColor() = when (terrain) {
    is HillTerrain -> Color.SaddleBrown
    is MountainTerrain -> Color.Gray
    PlainTerrain -> Color.Green
    is RiverTerrain -> Color.Blue
}
