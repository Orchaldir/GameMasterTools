package at.orchaldir.gm.core.model.world.settlement

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.SettlementId
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.HasStartDate
import at.orchaldir.gm.core.model.world.building.BuildingId
import at.orchaldir.gm.core.reducer.world.settlement.hasDuplicateSettlementAndDate
import at.orchaldir.gm.core.reducer.world.settlement.validateSettlementTile
import at.orchaldir.gm.core.selector.time.date.display
import at.orchaldir.gm.core.selector.time.getDefaultCalendar
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.map.MapSize2d
import at.orchaldir.gm.utils.map.MapSize2d.Companion.square
import at.orchaldir.gm.utils.map.TileMap2d
import at.orchaldir.gm.utils.update
import kotlinx.serialization.Serializable

const val SETTLEMENT_MAP_TYPE = "Settlement Map"

@JvmInline
@Serializable
value class SettlementMapId(val value: Int) : Id<SettlementMapId> {

    override fun next() = SettlementMapId(value + 1)
    override fun type() = SETTLEMENT_MAP_TYPE
    override fun value() = value

}

@Serializable
data class SettlementMap(
    val id: SettlementMapId,
    val settlement: SettlementId? = null,
    val date: Date? = null,
    val map: TileMap2d<SettlementTile> = TileMap2d(square(10), SettlementTile()),
) : Element<SettlementMapId>, HasStartDate {

    override fun id() = id
    override fun name(state: State) =
        if (settlement == null) {
            "$SETTLEMENT_MAP_TYPE ${id.value}"
        } else {
            val settlement = state.getSettlementStorage().getOrThrow(settlement)

            if (date != null) {
                val calendar = state.getDefaultCalendar()
                val dateText = display(calendar, date)
                "${settlement.name.text} ($dateText)"
            } else {
                settlement.name()
            }
        }

    override fun startDate(state: State) = date

    fun canBuild(index: Int, size: MapSize2d) = checkTiles(index, size) { it.canBuild() }
    fun canResize(index: Int, size: MapSize2d, building: BuildingId) =
        checkTiles(index, size) { it.canResize(building) }

    fun checkTile(x: Int, y: Int, check: (SettlementTile) -> Boolean) = map
        .getTile(x, y)
        ?.let(check)
        ?: false

    fun checkTiles(index: Int, size: MapSize2d, check: (SettlementTile) -> Boolean) = map
        .size.toIndices(index, size)
        ?.all { check(map.getRequiredTile(it)) }
        ?: false

    fun build(index: Int, construction: Construction): SettlementMap {
        val oldTile = map.getRequiredTile(index)

        require(oldTile.canBuild()) { "Tile $index is not empty!" }

        val tile = oldTile.copy(construction = construction)

        return updateTile(index, tile)
    }

    fun build(index: Int, size: MapSize2d, construction: Construction) =
        build(index, size, { construction })

    fun build(index: Int, size: MapSize2d, lookup: (Int) -> Construction): SettlementMap {
        val tiles = mutableMapOf<Int, SettlementTile>()

        map.size.toIndices(index, size)?.forEach { tileIndex ->
            val oldTile = map.getRequiredTile(tileIndex)

            require(oldTile.canBuild()) { "Tile $tileIndex is not empty!" }

            tiles[tileIndex] = oldTile.copy(construction = lookup(tileIndex))
        } ?: error("Lot with index $index & size ${size.format()} is outside the map!")

        return updateTiles(tiles)
    }

    fun buildAbstractBuilding(index: Int, size: MapSize2d) = if (size.tiles() == 1) {
        build(index, AbstractBuildingTile)
    } else {
        build(index, size) { i ->
            if (i == index) {
                AbstractLargeBuildingStart(size)
            } else {
                AbstractLargeBuildingTile
            }
        }
    }

    fun removeAbstractBuilding(index: Int): SettlementMap {
        val tiles = mutableMapOf<Int, SettlementTile>()
        val oldTile = map.getRequiredTile(index)

        when (oldTile.construction) {
            AbstractBuildingTile -> tiles[index] = oldTile.copy(construction = NoConstruction)
            is AbstractLargeBuildingStart -> {
                val size = oldTile.construction.size
                map.size.toIndices(index, size)?.forEach { tileIndex ->
                    val oldTile = map.getRequiredTile(tileIndex)

                    tiles[tileIndex] = oldTile.copy(construction = NoConstruction)
                } ?: error("Lot with index $index & size ${size.format()} is outside the map!")
            }

            else -> error("Tile $index is not an abstract building!")
        }

        return updateTiles(tiles)
    }

    fun removeBuilding(building: BuildingId): SettlementMap {
        return copy(map = map.copy(tiles = map.tiles.map { tile ->
            if (tile.construction is BuildingTile && tile.construction.building == building) {
                tile.copy(construction = NoConstruction)
            } else {
                tile
            }
        }))
    }

    fun removeStreet(index: Int): SettlementMap {
        val oldTile = map.getRequiredTile(index)

        require(oldTile.construction is StreetTile) { "Tile $index is not a street!" }

        val tile = oldTile.copy(construction = NoConstruction)

        return updateTile(index, tile)
    }

    fun setTerrain(index: Int, terrain: Terrain): SettlementMap {
        val oldTile = map.getRequiredTile(index)
        val tile = oldTile.copy(terrain = terrain)

        return updateTile(index, tile)
    }

    private fun updateTile(index: Int, tile: SettlementTile): SettlementMap {
        val tiles = map.tiles.update(index, tile)

        return copy(map = map.copy(tiles = tiles))
    }

    private fun updateTiles(tiles: Map<Int, SettlementTile>): SettlementMap {
        return copy(map = map.copy(tiles = map.tiles.update(tiles)))
    }

    fun updateBuilding(building: BuildingId, tileIndex: Int, size: MapSize2d) = removeBuilding(building)
        .build(tileIndex, size, BuildingTile(building))

    override fun validate(state: State) {
        state.getSettlementStorage().requireOptional(settlement)
        require(!hasDuplicateSettlementAndDate(state, this)) { "Multiple maps have the same town & date combination!" }
        map.tiles.forEach { validateSettlementTile(state, it) }
    }
}