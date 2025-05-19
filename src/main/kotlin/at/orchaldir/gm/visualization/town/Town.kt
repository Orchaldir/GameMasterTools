package at.orchaldir.gm.visualization.town

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.model.world.town.*
import at.orchaldir.gm.utils.map.MapSize2d
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.HALF
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.TileMap2dRenderer
import at.orchaldir.gm.utils.renderer.model.NoBorder
import at.orchaldir.gm.utils.renderer.svg.Svg
import at.orchaldir.gm.utils.renderer.svg.SvgBuilder

val TILE_SIZE = Distance.fromMeters(20)

private val DEFAULT_BUILDING_COLOR: (Building) -> Color = { _ -> Color.Black }
private val DEFAULT_BUILDING_TEXT: (Building) -> String? = { _ -> null }
private val DEFAULT_STREET_COLOR: (StreetTile, Int) -> Color = { _, _ -> Color.Gray }
private val DEFAULT_STREET_TEXT: (StreetTile, Int) -> String? = { _, _ -> null }
private val DEFAULT_TILE_TEXT: (Int, TownTile) -> String? = { _, _ -> null }

data class TownRenderer(
    private val tileRenderer: TileMap2dRenderer,
    private val svgBuilder: SvgBuilder,
    private val town: TownMap,
) {
    constructor(tileMapRenderer: TileMap2dRenderer, town: TownMap) : this(
        tileMapRenderer,
        SvgBuilder(tileMapRenderer.calculateMapSize(town.map)),
        town,
    )

    constructor(town: TownMap) : this(
        TileMap2dRenderer(TILE_SIZE, Distance.fromMeters(1.0f)),
        town,
    )

    fun renderTiles(
        colorLookup: (TownTile) -> Color = TownTile::getColor,
        linkLookup: (Int, TownTile) -> String? = DEFAULT_TILE_TEXT,
        tooltipLookup: (Int, TownTile) -> String? = DEFAULT_TILE_TEXT,
    ) {
        tileRenderer.renderWithLinksAndTooltips(svgBuilder, town.map, colorLookup, linkLookup, tooltipLookup)
    }

    fun renderAbstractBuildings(
        color: Color = Color.DimGray,
    ) {
        val size = MapSize2d.square(1)

        tileRenderer.render(town.map) { index, _, _, _, tile ->
            if (tile.construction is AbstractBuildingTile) {
                renderBuilding(svgBuilder.getLayer(), index, size, color)
            }
        }
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
        colorLookup: (StreetTile, Int) -> Color = DEFAULT_STREET_COLOR,
        linkLookup: (StreetTile, Int) -> String? = DEFAULT_STREET_TEXT,
        tooltipLookup: (StreetTile, Int) -> String? = DEFAULT_STREET_TEXT,
    ) {
        renderStreets { aabb, street, index ->
            val color = colorLookup(street, index)

            svgBuilder.optionalLinkAndTooltip(linkLookup(street, index), tooltipLookup(street, index)) {
                renderStreet(it, aabb, color)
            }
        }
    }

    fun renderStreets(
        render: (AABB, StreetTile, Int) -> Unit,
    ) {
        val right = Point2d.xAxis(tileRenderer.tileSize / 2)
        val down = Point2d.yAxis(tileRenderer.tileSize / 2)

        tileRenderer.render(town.map) { index, x, y, aabb, tile ->
            if (tile.construction is StreetTile) {
                if (town.checkTile(x + 1, y) { it.construction is StreetTile }) {
                    val rightAABB = aabb + right
                    render(rightAABB, tile.construction, index)
                }

                if (town.checkTile(x, y + 1) { it.construction is StreetTile }) {
                    val downAABB = aabb + down
                    render(downAABB, tile.construction, index)
                }

                render(aabb, tile.construction, index)
            }
        }
    }

    private fun renderBuilding(
        layer: LayerRenderer,
        building: Building,
        color: Color,
    ) = renderBuilding(layer, building.lot.tileIndex, building.lot.size, color)

    private fun renderBuilding(
        layer: LayerRenderer,
        tileIndex: Int,
        size: MapSize2d,
        color: Color,
    ) {
        val start = tileRenderer.calculateTilePosition(town.map, tileIndex)
        val size = tileRenderer.calculateLotSize(size)
        val aabb = AABB(start, size).shrink(HALF)
        val style = NoBorder(color.toRender())

        layer.renderRectangle(aabb, style)
    }


    fun finish() = svgBuilder.finish()
}

fun renderStreet(renderer: LayerRenderer, tile: AABB, color: Color) {
    val style = NoBorder(color.toRender())
    renderer.renderRectangle(tile.shrink(HALF), style)
}

fun visualizeTown(
    town: TownMap,
    buildings: List<Building> = emptyList(),
    tileColorLookup: (TownTile) -> Color = TownTile::getColor,
    tileLinkLookup: (Int, TownTile) -> String? = DEFAULT_TILE_TEXT,
    tileTooltipLookup: (Int, TownTile) -> String? = DEFAULT_TILE_TEXT,
    buildingColorLookup: (Building) -> Color = DEFAULT_BUILDING_COLOR,
    buildingLinkLookup: (Building) -> String? = DEFAULT_BUILDING_TEXT,
    buildingTooltipLookup: (Building) -> String? = DEFAULT_BUILDING_TEXT,
    streetColorLookup: (StreetTile, Int) -> Color = DEFAULT_STREET_COLOR,
    streetLinkLookup: (StreetTile, Int) -> String? = DEFAULT_STREET_TEXT,
    streetTooltipLookup: (StreetTile, Int) -> String? = DEFAULT_STREET_TEXT,
): Svg {
    val townRenderer = TownRenderer(town)

    townRenderer.renderTiles(tileColorLookup, tileLinkLookup, tileTooltipLookup)
    townRenderer.renderAbstractBuildings()
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

fun getStreetTemplateFill(state: State): (StreetTile, Int) -> Color = { tile, _ ->
    state
        .getStreetTemplateStorage()
        .get(tile.templateId)
        ?.color ?: Color.Pink
}

fun showSelectedBuilding(selected: Building): (Building) -> Color = { building ->
    if (building.id == selected.id) {
        Color.Gold
    } else {
        Color.Black
    }
}

fun showTerrainName(state: State): (Int, TownTile) -> String? = { _, tile ->
    when (tile.terrain) {
        is HillTerrain -> state.getRegionStorage().getOrThrow(tile.terrain.mountain).name.text
        is MountainTerrain -> state.getRegionStorage().getOrThrow(tile.terrain.mountain).name.text
        PlainTerrain -> null
        is RiverTerrain -> state.getRiverStorage().getOrThrow(tile.terrain.river).name.text
    }
}
