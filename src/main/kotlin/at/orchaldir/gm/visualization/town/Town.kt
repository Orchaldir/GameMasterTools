package at.orchaldir.gm.visualization.town

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.model.world.railway.RailwayTypeId
import at.orchaldir.gm.core.model.world.street.StreetId
import at.orchaldir.gm.core.model.world.terrain.HillTerrain
import at.orchaldir.gm.core.model.world.terrain.MountainTerrain
import at.orchaldir.gm.core.model.world.terrain.PlainTerrain
import at.orchaldir.gm.core.model.world.terrain.RiverTerrain
import at.orchaldir.gm.core.model.world.town.*
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.TileMap2dRenderer
import at.orchaldir.gm.utils.renderer.model.NoBorder
import at.orchaldir.gm.utils.renderer.svg.Svg
import at.orchaldir.gm.utils.renderer.svg.SvgBuilder

const val TILE_SIZE = 20.0f
val RAILWAY_WIDTH = Factor(0.2f)
val STREET_WIDTH = Factor(0.5f)

val SHOW_BUILDING_NAME: (Building) -> String? = { b -> b.name }

private val DEFAULT_BUILDING_COLOR: (Building) -> Color = { _ -> Color.Black }
private val DEFAULT_BUILDING_TEXT: (Building) -> String? = { _ -> null }
private val DEFAULT_RAILWAY_COLOR: (Int, RailwayTypeId) -> Color = { _, _ -> Color.Black }
private val DEFAULT_RAILWAY_TEXT: (Int, RailwayTypeId) -> String? = { _, _ -> null }
private val DEFAULT_STREET_COLOR: (Int, StreetId) -> Color = { _, _ -> Color.Gray }
private val DEFAULT_STREET_TEXT: (Int, StreetId) -> String? = { _, _ -> null }
private val DEFAULT_TILE_COLOR: (Int, TownTile) -> Color = { _, tile ->
    when (tile.terrain) {
        is HillTerrain -> Color.SaddleBrown
        is MountainTerrain -> Color.Gray
        PlainTerrain -> Color.Green
        is RiverTerrain -> Color.Blue
    }
}
private val DEFAULT_TILE_TEXT: (Int, TownTile) -> String? = { _, _ -> null }

data class TownRendererConfig(
    val tileColorLookup: (Int, TownTile) -> Color = DEFAULT_TILE_COLOR,
    val tileLinkLookup: (Int, TownTile) -> String? = DEFAULT_TILE_TEXT,
    val tileTooltipLookup: (Int, TownTile) -> String? = DEFAULT_TILE_TEXT,
    val buildingColorLookup: (Building) -> Color = DEFAULT_BUILDING_COLOR,
    val buildingLinkLookup: (Building) -> String? = DEFAULT_BUILDING_TEXT,
    val buildingTooltipLookup: (Building) -> String? = DEFAULT_BUILDING_TEXT,
    val railwayColorLookup: (Int, RailwayTypeId) -> Color = DEFAULT_RAILWAY_COLOR,
    val railwayLinkLookup: (Int, RailwayTypeId) -> String? = DEFAULT_RAILWAY_TEXT,
    val railwayTooltipLookup: (Int, RailwayTypeId) -> String? = DEFAULT_RAILWAY_TEXT,
    val streetColorLookup: (Int, StreetId) -> Color = DEFAULT_STREET_COLOR,
    val streetLinkLookup: (Int, StreetId) -> String? = DEFAULT_STREET_TEXT,
    val streetTooltipLookup: (Int, StreetId) -> String? = DEFAULT_STREET_TEXT,
) {

    constructor(state: State) : this(
        buildingTooltipLookup = SHOW_BUILDING_NAME,
        railwayColorLookup = getRailwayTypeColor(state),
        railwayTooltipLookup = showRailwayName(state),
        streetColorLookup = getStreetTypeColor(state),
        streetTooltipLookup = showStreetName(state),
        tileTooltipLookup = showTerrainName(state),
    )
}

data class TownRenderer(
    private val config: TownRendererConfig,
    private val tileRenderer: TileMap2dRenderer,
    private val svgBuilder: SvgBuilder,
    private val town: Town,
) {
    constructor(
        tileMapRenderer: TileMap2dRenderer,
        town: Town,
        config: TownRendererConfig = TownRendererConfig(),
    ) : this(
        config,
        tileMapRenderer,
        SvgBuilder(tileMapRenderer.calculateMapSize(town.map)),
        town,
    )

    constructor(town: Town, config: TownRendererConfig = TownRendererConfig()) : this(
        TileMap2dRenderer(Distance(TILE_SIZE), Distance(1.0f)),
        town,
        config,
    )

    fun renderTiles() {
        tileRenderer.renderWithLinksAndTooltips(
            svgBuilder,
            town.map,
            config.tileColorLookup,
            config.tileLinkLookup,
            config.tileTooltipLookup
        )
    }

    fun renderBuildings(buildings: List<Building>) {
        buildings.forEach { building ->
            val color = config.buildingColorLookup(building)
            val link = config.buildingLinkLookup(building)
            val tooltip = config.buildingTooltipLookup(building)

            svgBuilder.optionalLinkAndTooltip(link, tooltip) {
                renderBuilding(it, building, color)
            }
        }
    }

    fun renderRailways() {
        renderRailways { aabb, railwayType, connection, index, x, y ->
            val color = config.railwayColorLookup(index, railwayType)
            val link = config.railwayLinkLookup(index, railwayType)
            val tooltip = config.railwayTooltipLookup(index, railwayType)

            svgBuilder.optionalLinkAndTooltip(link, tooltip) { renderer ->
                renderConnection(renderer, railwayType, connection, x, y, aabb, color, RAILWAY_WIDTH)
            }
        }
    }

    fun renderRailways(
        render: (AABB, RailwayTypeId, TileConnection, Int, Int, Int) -> Unit,
    ) {
        tileRenderer.render(town.map) { index, x, y, aabb, tile ->
            if (tile.construction is RailwayTile) {
                render(aabb, tile.construction.railwayType, tile.construction.connection, index, x, y)
            } else if (tile.construction is CrossingTile) {
                tile.construction.railways.forEach {
                    render(aabb, it.first, it.second, index, x, y)
                }
            }
        }
    }

    fun renderStreets() {
        renderStreets { aabb, street, connection, index, x, y ->
            val color = config.streetColorLookup(index, street)
            val link = config.streetLinkLookup(index, street)
            val tooltip = config.streetTooltipLookup(index, street)

            svgBuilder.optionalLinkAndTooltip(link, tooltip) { renderer ->
                renderConnection(renderer, street, connection, x, y, aabb, color, STREET_WIDTH)
            }
        }
    }

    fun renderStreets(
        render: (AABB, StreetId, TileConnection, Int, Int, Int) -> Unit,
    ) {
        tileRenderer.render(town.map) { index, x, y, aabb, tile ->
            if (tile.construction is StreetTile) {
                render(aabb, tile.construction.street, tile.construction.connection, index, x, y)
            } else if (tile.construction is CrossingTile) {
                tile.construction.streets.forEach {
                    render(aabb, it.first, it.second, index, x, y)
                }
            }
        }
    }

    private fun <ID : Id<ID>> renderConnection(
        renderer: LayerRenderer,
        type: ID,
        connection: TileConnection,
        x: Int,
        y: Int,
        aabb: AABB,
        color: Color,
        width: Factor,
    ) {
        when (connection) {
            TileConnection.Curve -> {
                if (town.checkTile(x + 1, y) { it.construction.contains(type) }) {
                    renderRailwayRight(renderer, aabb, color, width)
                }
                if (town.checkTile(x - 1, y) { it.construction.contains(type) }) {
                    renderRailwayLeft(renderer, aabb, color, width)
                }
                if (town.checkTile(x, y + 1) { it.construction.contains(type) }) {
                    renderRailwayDown(renderer, aabb, color, width)
                }
                if (town.checkTile(x, y - 1) { it.construction.contains(type) }) {
                    renderRailwayUp(renderer, aabb, color, width)
                }
                renderRailwayCenter(renderer, aabb, color, width)
            }

            TileConnection.Horizontal -> renderHorizontalRailway(renderer, aabb, color, width)
            TileConnection.Vertical -> renderVerticalRailway(renderer, aabb, color, width)
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


    fun finish() = svgBuilder.finish()
}

fun renderRailwayCenter(renderer: LayerRenderer, tile: AABB, color: Color, width: Factor) {
    val style = NoBorder(color.toRender())
    renderer.renderRectangle(tile.shrink(FULL - width), style)
}

fun renderRailwayLeft(renderer: LayerRenderer, tile: AABB, color: Color, width: Factor) {
    val style = NoBorder(color.toRender())
    val builder = Polygon2dBuilder()
    val half = width * 0.5f

    builder.addPoint(tile, START, CENTER + half)
    builder.addPoint(tile, START, CENTER - half)
    builder.addPoint(tile, CENTER - half, CENTER - half)
    builder.addPoint(tile, CENTER - half, CENTER + half)

    renderer.renderPolygon(builder.build(), style)
}

fun renderRailwayDown(renderer: LayerRenderer, tile: AABB, color: Color, width: Factor) {
    val style = NoBorder(color.toRender())
    val builder = Polygon2dBuilder()
    val half = width * 0.5f

    builder.addPoint(tile, CENTER + half, CENTER + half)
    builder.addPoint(tile, CENTER - half, CENTER + half)
    builder.addPoint(tile, CENTER - half, FULL)
    builder.addPoint(tile, CENTER + half, FULL)

    renderer.renderPolygon(builder.build(), style)
}

fun renderRailwayRight(renderer: LayerRenderer, tile: AABB, color: Color, width: Factor) {
    val style = NoBorder(color.toRender())
    val builder = Polygon2dBuilder()
    val half = width * 0.5f

    builder.addPoint(tile, CENTER + half, CENTER + half)
    builder.addPoint(tile, CENTER + half, CENTER - half)
    builder.addPoint(tile, FULL, CENTER - half)
    builder.addPoint(tile, FULL, CENTER + half)

    renderer.renderPolygon(builder.build(), style)
}

fun renderRailwayUp(renderer: LayerRenderer, tile: AABB, color: Color, width: Factor) {
    val style = NoBorder(color.toRender())
    val builder = Polygon2dBuilder()
    val half = width * 0.5f

    builder.addPoint(tile, CENTER + half, START)
    builder.addPoint(tile, CENTER - half, START)
    builder.addPoint(tile, CENTER - half, CENTER - half)
    builder.addPoint(tile, CENTER + half, CENTER - half)

    renderer.renderPolygon(builder.build(), style)
}

fun renderHorizontalRailway(renderer: LayerRenderer, tile: AABB, color: Color, width: Factor) {
    val style = NoBorder(color.toRender())
    val builder = Polygon2dBuilder()
    val half = width * 0.5f

    builder.addMirroredPoints(tile, FULL, CENTER - half)
    builder.addMirroredPoints(tile, FULL, CENTER + half)

    renderer.renderPolygon(builder.build(), style)
}

fun renderVerticalRailway(renderer: LayerRenderer, tile: AABB, color: Color, width: Factor) {
    val style = NoBorder(color.toRender())
    val builder = Polygon2dBuilder()

    builder.addMirroredPoints(tile, width, START)
    builder.addMirroredPoints(tile, width, END)

    renderer.renderPolygon(builder.build(), style)
}

fun renderStreet(renderer: LayerRenderer, tile: AABB, color: Color) {
    val style = NoBorder(color.toRender())
    renderer.renderRectangle(tile.shrink(Factor(0.5f)), style)
}

fun visualizeTown(
    town: Town,
    buildings: List<Building> = emptyList(),
    config: TownRendererConfig = TownRendererConfig(),
): Svg {
    val townRenderer = TownRenderer(town, config)

    townRenderer.renderTiles()
    townRenderer.renderBuildings(buildings)
    townRenderer.renderStreets()
    townRenderer.renderRailways()

    return townRenderer.finish()
}

fun getRailwayTypeColor(state: State): (Int, RailwayTypeId) -> Color = { _, id ->
    state
        .getRailwayTypeStorage()
        .get(id)
        ?.color ?: Color.Pink
}

fun getStreetTypeColor(state: State): (Int, StreetId) -> Color = { _, id ->
    state
        .getStreetStorage()
        .get(id)
        ?.type
        ?.let {
            state
                .getStreetTypeStorage()
                .get(it)
                ?.color
        } ?: Color.Pink
}

fun showSelectedBuilding(selected: Building): (Building) -> Color = { building ->
    if (building == selected) {
        Color.Gold
    } else {
        Color.Black
    }
}

fun <ID : Id<ID>> showSelectedElement(selected: ID): (Int, ID) -> Color = { _, id ->
    if (id == selected) {
        Color.Gold
    } else {
        Color.Gray
    }
}

fun showRailwayName(state: State): (Int, RailwayTypeId) -> String? = { _, railway ->
    state.getRailwayTypeStorage().getOrThrow(railway).name
}

fun showStreetName(state: State): (Int, StreetId) -> String? = { _, streetId ->
    state.getStreetStorage().getOrThrow(streetId).name
}

fun showTerrainName(state: State): (Int, TownTile) -> String? = { _, tile ->
    when (tile.terrain) {
        is HillTerrain -> state.getMountainStorage().getOrThrow(tile.terrain.mountain).name
        is MountainTerrain -> state.getMountainStorage().getOrThrow(tile.terrain.mountain).name
        PlainTerrain -> null
        is RiverTerrain -> state.getRiverStorage().getOrThrow(tile.terrain.river).name
    }
}