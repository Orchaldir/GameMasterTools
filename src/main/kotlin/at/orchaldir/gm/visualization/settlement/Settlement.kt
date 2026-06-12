package at.orchaldir.gm.visualization.settlement

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.util.InSettlementMap
import at.orchaldir.gm.core.model.util.part.MadeFromWood
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.visualization.RectangularShapeGrammar
import at.orchaldir.gm.core.model.visualization.ShapeGrammar
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.model.world.settlement.*
import at.orchaldir.gm.core.selector.util.getBuildingsIn
import at.orchaldir.gm.prototypes.visualization.grammar.LINE_OPTIONS
import at.orchaldir.gm.utils.doNothing
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
import at.orchaldir.gm.visualization.grammar.Borders
import at.orchaldir.gm.visualization.grammar.GrammarRenderState
import at.orchaldir.gm.visualization.grammar.visualizeShapeGrammar

val TILE_SIZE = Distance.fromMeters(20)

fun createStreetGrammar(color: Color, material: MaterialId = MaterialId(0)) =
    RectangularShapeGrammar(MadeFromWood(material, color))

private val DEFAULT_BUILDING_COLOR: (Building) -> Color = { _ -> Color.Black }
private val DEFAULT_BUILDING_TEXT: (Building) -> String? = { _ -> null }
private val DEFAULT_STREET_TYPE_GRAMMAR = createStreetGrammar(Color.Gray)
private val DEFAULT_STREET_GRAMMAR: (StreetTile, Int) -> ShapeGrammar = { _, _ ->
    DEFAULT_STREET_TYPE_GRAMMAR
}
private val DEFAULT_STREET_TEXT: (StreetTile, Int) -> String? = { _, _ -> null }
private val DEFAULT_TILE_TEXT: (Int, SettlementTile) -> String? = { _, _ -> null }

data class SettlementRenderer(
    private val state: State,
    private val tileRenderer: TileMap2dRenderer,
    private val svgBuilder: SvgBuilder,
    private val settlement: SettlementMap,
) {
    constructor(
        state: State,
        tileMapRenderer: TileMap2dRenderer,
        settlement: SettlementMap,
    ) : this(
        state,
        tileMapRenderer,
        SvgBuilder(tileMapRenderer.calculateMapSize(settlement.map)),
        settlement,
    )

    constructor(state: State, settlement: SettlementMap) : this(
        state,
        TileMap2dRenderer(TILE_SIZE, Distance.fromMeters(1.0f)),
        settlement,
    )

    fun renderTiles(
        colorLookup: (SettlementTile) -> Color = SettlementTile::getColor,
        linkLookup: (Int, SettlementTile) -> String? = DEFAULT_TILE_TEXT,
        tooltipLookup: (Int, SettlementTile) -> String? = DEFAULT_TILE_TEXT,
    ) {
        tileRenderer.renderWithLinksAndTooltips(svgBuilder, settlement.map, colorLookup, linkLookup, tooltipLookup)
    }

    fun renderAbstractBuildings(
        color: Color = Color.DimGray,
    ) {
        val size = MapSize2d.square(1)

        tileRenderer.render(settlement.map) { index, _, _, _, tile ->
            when (tile.construction) {
                AbstractBuildingTile -> renderBuilding(svgBuilder.getLayer(), index, size, color)
                is AbstractLargeBuildingStart -> renderBuilding(
                    svgBuilder.getLayer(),
                    index,
                    tile.construction.size,
                    color
                )

                else -> doNothing()
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

    fun renderSimplifiedStreets(
        colorLookup: (StreetTile, Int) -> ShapeGrammar = DEFAULT_STREET_GRAMMAR,
        linkLookup: (StreetTile, Int) -> String? = DEFAULT_STREET_TEXT,
        tooltipLookup: (StreetTile, Int) -> String? = DEFAULT_STREET_TEXT,
    ) {
        val renderState = GrammarRenderState(state, svgBuilder, LINE_OPTIONS)

        renderStreets { aabb, borders, street, index ->
            val grammar = colorLookup(street, index)

            svgBuilder.optionalLinkAndTooltip(linkLookup(street, index), tooltipLookup(street, index)) {
                visualizeShapeGrammar(renderState.addSeed(index), grammar, aabb, borders)
            }
        }
    }

    fun renderStreets(
        render: (AABB, Borders, StreetTile, Int) -> Unit,
    ) {
        tileRenderer.render(settlement.map, Point2d()) { index, aabb, borders, tile ->
            if (tile.construction is StreetTile) {
                render(aabb, borders, tile.construction, index)
            }
        }
    }

    fun renderStreetsWithConnections(
        render: (AABB, StreetTile, Int) -> Unit,
    ) {
        val right = Point2d.xAxis(tileRenderer.tileSize / 2)
        val down = Point2d.yAxis(tileRenderer.tileSize / 2)

        tileRenderer.render(settlement.map) { index, x, y, aabb, tile ->
            if (tile.construction is StreetTile) {
                if (settlement.checkTile(x + 1, y) { it.construction is StreetTile }) {
                    val rightAABB = aabb + right
                    render(rightAABB, tile.construction, index)
                }

                if (settlement.checkTile(x, y + 1) { it.construction is StreetTile }) {
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
    ) {
        if (building.position is InSettlementMap) {
            renderBuilding(layer, building.position.tileIndex, building.size, color)
        }
    }

    private fun renderBuilding(
        layer: LayerRenderer,
        tileIndex: Int,
        size: MapSize2d,
        color: Color,
    ) {
        val start = tileRenderer.calculateTilePosition(settlement.map, tileIndex)
        val size = tileRenderer.calculateLotSize(size)
        val aabb = AABB(start, size).shrink(HALF)
        val style = NoBorder(color.toRender())

        layer.renderRectangle(aabb, style)
    }


    fun finish() = svgBuilder.finish()
}

fun renderSimplifiedStreet(renderer: LayerRenderer, tile: AABB, color: Color) {
    val style = NoBorder(color.toRender())
    renderer.renderRectangle(tile.shrink(HALF), style)
}

fun visualizeSettlementMap(
    state: State,
    settlement: SettlementMap,
    tileColorLookup: (SettlementTile) -> Color = SettlementTile::getColor,
    tileLinkLookup: (Int, SettlementTile) -> String? = DEFAULT_TILE_TEXT,
    tileTooltipLookup: (Int, SettlementTile) -> String? = DEFAULT_TILE_TEXT,
    buildingColorLookup: (Building) -> Color = DEFAULT_BUILDING_COLOR,
    buildingLinkLookup: (Building) -> String? = DEFAULT_BUILDING_TEXT,
    buildingTooltipLookup: (Building) -> String? = DEFAULT_BUILDING_TEXT,
    streetGrammarLookup: (StreetTile, Int) -> ShapeGrammar = DEFAULT_STREET_GRAMMAR,
    streetLinkLookup: (StreetTile, Int) -> String? = DEFAULT_STREET_TEXT,
    streetTooltipLookup: (StreetTile, Int) -> String? = DEFAULT_STREET_TEXT,
): Svg {
    val settlementRenderer = SettlementRenderer(state, settlement)
    val buildings = state.getBuildingsIn(settlement.id)

    settlementRenderer.renderTiles(tileColorLookup, tileLinkLookup, tileTooltipLookup)
    settlementRenderer.renderAbstractBuildings()
    settlementRenderer.renderBuildings(buildings, buildingColorLookup, buildingLinkLookup, buildingTooltipLookup)
    settlementRenderer.renderSimplifiedStreets(streetGrammarLookup, streetLinkLookup, streetTooltipLookup)

    return settlementRenderer.finish()
}

fun SettlementTile.getColor() = when (terrain) {
    is HillTerrain -> Color.SaddleBrown
    is MountainTerrain -> Color.Gray
    PlainTerrain -> Color.Green
    is RiverTerrain -> Color.Blue
}

fun getStreetTemplateGrammar(state: State): (StreetTile, Int) -> ShapeGrammar = { tile, _ ->
    state
        .getStreetTemplateStorage()
        .get(tile.templateId)
        ?.grammar ?: DEFAULT_STREET_TYPE_GRAMMAR
}

fun showSelectedBuilding(selected: Building): (Building) -> Color = { building ->
    if (building.id == selected.id) {
        Color.Gold
    } else {
        Color.Black
    }
}

fun showTerrainName(state: State): (Int, SettlementTile) -> String? = { _, tile ->
    when (tile.terrain) {
        is HillTerrain -> state.getRegionStorage().getOrThrow(tile.terrain.mountain).name.text
        is MountainTerrain -> state.getRegionStorage().getOrThrow(tile.terrain.mountain).name.text
        PlainTerrain -> null
        is RiverTerrain -> state.getRiverStorage().getOrThrow(tile.terrain.river).name.text
    }
}
