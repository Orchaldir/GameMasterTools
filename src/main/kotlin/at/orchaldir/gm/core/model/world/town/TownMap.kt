package at.orchaldir.gm.core.model.world.town

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.TownId
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.HasStartDate
import at.orchaldir.gm.core.model.world.building.BuildingId
import at.orchaldir.gm.core.selector.time.calendar.getDefaultCalendar
import at.orchaldir.gm.core.selector.time.date.display
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.map.MapSize2d
import at.orchaldir.gm.utils.map.MapSize2d.Companion.square
import at.orchaldir.gm.utils.map.TileMap2d
import at.orchaldir.gm.utils.update
import kotlinx.serialization.Serializable

const val TOWN_MAP_TYPE = "Town Map"

@JvmInline
@Serializable
value class TownMapId(val value: Int) : Id<TownMapId> {

    override fun next() = TownMapId(value + 1)
    override fun type() = TOWN_MAP_TYPE
    override fun value() = value

}

@Serializable
data class TownMap(
    val id: TownMapId,
    val town: TownId? = null,
    val date: Date? = null,
    val map: TileMap2d<TownTile> = TileMap2d(square(10), TownTile()),
) : Element<TownMapId>, HasStartDate {

    override fun id() = id
    override fun name(state: State) =
        if (town == null) {
            "Town Map ${id.value}"
        } else {
            val town = state.getTownStorage().getOrThrow(town)

            if (date != null) {
                val calendar = state.getDefaultCalendar()
                val dateText = display(calendar, date)
                "${town.name.text} ($dateText)"
            } else {
                town.name()
            }
        }

    override fun startDate() = date

    fun canBuild(index: Int, size: MapSize2d) = checkTiles(index, size) { it.canBuild() }
    fun canResize(index: Int, size: MapSize2d, building: BuildingId) =
        checkTiles(index, size) { it.canResize(building) }

    fun checkTile(x: Int, y: Int, check: (TownTile) -> Boolean) = map
        .getTile(x, y)
        ?.let(check)
        ?: false

    fun checkTiles(index: Int, size: MapSize2d, check: (TownTile) -> Boolean) = map
        .size.toIndices(index, size)
        ?.all { check(map.getRequiredTile(it)) }
        ?: false

    fun build(index: Int, construction: Construction): TownMap {
        val oldTile = map.getRequiredTile(index)

        require(oldTile.canBuild()) { "Tile $index is not empty!" }

        val tile = oldTile.copy(construction = construction)

        return updateTile(index, tile)
    }

    fun build(index: Int, size: MapSize2d, construction: Construction) =
        build(index, size, { construction })

    fun build(index: Int, size: MapSize2d, lookup: (Int) -> Construction): TownMap {
        val tiles = mutableMapOf<Int, TownTile>()

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

    fun removeAbstractBuilding(index: Int): TownMap {
        val oldTile = map.getRequiredTile(index)

        require(oldTile.construction is AbstractBuildingTile) { "Tile $index is not an abstract building!" }

        val tile = oldTile.copy(construction = NoConstruction)

        return updateTile(index, tile)
    }

    fun removeBuilding(building: BuildingId): TownMap {
        return copy(map = map.copy(tiles = map.tiles.map { tile ->
            if (tile.construction is BuildingTile && tile.construction.building == building) {
                tile.copy(construction = NoConstruction)
            } else {
                tile
            }
        }))
    }

    fun removeStreet(index: Int): TownMap {
        val oldTile = map.getRequiredTile(index)

        require(oldTile.construction is StreetTile) { "Tile $index is not a street!" }

        val tile = oldTile.copy(construction = NoConstruction)

        return updateTile(index, tile)
    }

    fun setTerrain(index: Int, terrain: Terrain): TownMap {
        val oldTile = map.getRequiredTile(index)
        val tile = oldTile.copy(terrain = terrain)

        return updateTile(index, tile)
    }

    private fun updateTile(index: Int, tile: TownTile): TownMap {
        val tiles = map.tiles.update(index, tile)

        return copy(map = map.copy(tiles = tiles))
    }

    private fun updateTiles(tiles: Map<Int, TownTile>): TownMap {
        return copy(map = map.copy(tiles = map.tiles.update(tiles)))
    }

}