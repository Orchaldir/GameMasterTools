package at.orchaldir.gm.visualization.town

import at.orchaldir.gm.core.model.State
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
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.NoBorder
import at.orchaldir.gm.utils.renderer.TileMap2dRenderer
import at.orchaldir.gm.utils.renderer.svg.Svg
import at.orchaldir.gm.utils.renderer.svg.SvgBuilder

private val DEFAULT_BUILDING_COLOR: (Building) -> Color = { _ -> Color.Black }
private val DEFAULT_BUILDING_TEXT: (Building) -> String? = { _ -> null }
private val DEFAULT_STREET_COLOR: (StreetId, Int) -> Color = { _, _ -> Color.Gray }
private val DEFAULT_STREET_TEXT: (StreetId, Int) -> String? = { _, _ -> null }
private val DEFAULT_TILE_TEXT: (Int, TownTile) -> String? = { _, _ -> null }

data class TownRenderer(
    private val tileRenderer: TileMap2dRenderer,
    private val svgBuilder: SvgBuilder,
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
        linkLookup: (Int, TownTile) -> String?,
        tooltipLookup: (Int, TownTile) -> String? = DEFAULT_TILE_TEXT,
    ) {
        tileRenderer.renderWithLinksAndTooltips(svgBuilder, town.map, colorLookup, linkLookup, tooltipLookup)
    }

    fun renderBuildings(
        buildings: List<Building>,
        colorLookup: (Building) -> Color = DEFAULT_BUILDING_COLOR,
        linkLookup: (Building) -> String? = DEFAULT_BUILDING_TEXT,
        tooltipLookup: (Building) -> String? = DEFAULT_BUILDING_TEXT,
    ) {
        buildings.forEach { building ->
            val color = colorLookup(building)

            svgBuilder.optionalLinkAndTooltip(linkLookup(building), tooltipLookup(building)) {
                renderBuilding(it, building, color)
            }
        }
    }

    fun renderStreets(
        colorLookup: (StreetId, Int) -> Color = DEFAULT_STREET_COLOR,
        linkLookup: (StreetId, Int) -> String? = DEFAULT_STREET_TEXT,
        tooltipLookup: (StreetId, Int) -> String? = DEFAULT_STREET_TEXT,
    ) {
        renderStreets { aabb, streetId, index ->
            val color = colorLookup(streetId, index)

            svgBuilder.optionalLinkAndTooltip(linkLookup(streetId, index), tooltipLookup(streetId, index)) {
                renderStreet(it, aabb, color)
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
        layer: LayerRenderer,
        building: Building,
        color: Color,
    ) {
        val start = tileRenderer.calculateTilePosition(town.map, building.lot.tileIndex)
        val size = tileRenderer.calculateLotSize(building.lot.size)
        val aabb = AABB(start, size).shrink(Factor(0.5f))
        val style = NoBorder(color.toRender())

        layer.renderRectangle(aabb, style)
    }

    private fun renderStreet(renderer: LayerRenderer, tile: AABB, color: Color) {
        val style = NoBorder(color.toRender())
        renderer.renderRectangle(tile.shrink(Factor(0.5f)), style)
    }

    fun finish() = svgBuilder.finish()
}

fun visualizeTown(
    town: Town,
    buildings: List<Building> = emptyList(),
    tileColorLookup: (TownTile) -> Color = TownTile::getColor,
    tileLinkLookup: (Int, TownTile) -> String? = DEFAULT_TILE_TEXT,
    tileTooltipLookup: (Int, TownTile) -> String? = DEFAULT_TILE_TEXT,
    buildingColorLookup: (Building) -> Color = DEFAULT_BUILDING_COLOR,
    buildingLinkLookup: (Building) -> String? = DEFAULT_BUILDING_TEXT,
    buildingTooltipLookup: (Building) -> String? = DEFAULT_BUILDING_TEXT,
    streetColorLookup: (StreetId, Int) -> Color = DEFAULT_STREET_COLOR,
    streetLinkLookup: (StreetId, Int) -> String? = DEFAULT_STREET_TEXT,
    streetTooltipLookup: (StreetId, Int) -> String? = DEFAULT_STREET_TEXT,
): Svg {
    val townRenderer = TownRenderer(town)

    townRenderer.renderTiles(tileColorLookup, tileLinkLookup, tileTooltipLookup)
    townRenderer.renderBuildings(buildings, buildingColorLookup, buildingLinkLookup, buildingTooltipLookup)
    townRenderer.renderStreets(streetColorLookup, streetLinkLookup, streetTooltipLookup)

    return townRenderer.finish()
}

fun TownTile.getColor() = when (terrain) {
    is HillTerrain -> Color.SaddleBrown
    is MountainTerrain -> Color.Gray
    PlainTerrain -> Color.Green
    is RiverTerrain -> Color.Blue
}

fun showTerrainName(state: State): (Int, TownTile) -> String? = { _, tile ->
    when (tile.terrain) {
        is HillTerrain -> state.getMountainStorage().getOrThrow(tile.terrain.mountain).name
        is MountainTerrain -> state.getMountainStorage().getOrThrow(tile.terrain.mountain).name
        PlainTerrain -> null
        is RiverTerrain -> state.getRiverStorage().getOrThrow(tile.terrain.river).name
    }
}
