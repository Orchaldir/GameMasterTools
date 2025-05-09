package at.orchaldir.gm.core.model.world.town

import at.orchaldir.gm.core.model.name.Name
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.time.date.Year
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.model.world.building.BuildingId
import at.orchaldir.gm.core.model.world.terrain.Terrain
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.map.MapSize2d
import at.orchaldir.gm.utils.map.MapSize2d.Companion.square
import at.orchaldir.gm.utils.map.TileMap2d
import at.orchaldir.gm.utils.update
import kotlinx.serialization.Serializable

const val TOWN_TYPE = "Town"

@JvmInline
@Serializable
value class TownId(val value: Int) : Id<TownId> {

    override fun next() = TownId(value + 1)
    override fun type() = TOWN_TYPE
    override fun value() = value

}

@Serializable
data class Town(
    val id: TownId,
    val name: Name = Name.init("Town ${id.value}"),
    val map: TileMap2d<TownTile> = TileMap2d(square(10), TownTile()),
    val foundingDate: Date = Year(0),
    val founder: Creator = UndefinedCreator,
) : ElementWithSimpleName<TownId>, Created, HasStartDate {

    override fun id() = id
    override fun name() = name.text

    override fun creator() = founder

    override fun startDate() = foundingDate

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

    fun build(index: Int, construction: Construction): Town {
        val oldTile = map.getRequiredTile(index)

        require(oldTile.canBuild()) { "Tile $index is not empty!" }

        val tile = oldTile.copy(construction = construction)

        return updateTile(index, tile)
    }

    fun build(index: Int, size: MapSize2d, construction: Construction): Town {
        val tiles = mutableMapOf<Int, TownTile>()

        map.size.toIndices(index, size)?.forEach { tileIndex ->
            val oldTile = map.getRequiredTile(tileIndex)

            require(oldTile.canBuild()) { "Tile $tileIndex is not empty!" }

            tiles[tileIndex] = oldTile.copy(construction = construction)
        } ?: error("Lot with index $index & size ${size.format()} is outside the map!")

        return updateTiles(tiles)
    }

    fun removeAbstractBuilding(index: Int): Town {
        val oldTile = map.getRequiredTile(index)

        require(oldTile.construction is AbstractBuildingTile) { "Tile $index is not an abstract building!" }

        val tile = oldTile.copy(construction = NoConstruction)

        return updateTile(index, tile)
    }

    fun removeBuilding(building: BuildingId): Town {
        return copy(map = map.copy(tiles = map.tiles.map { tile ->
            if (tile.construction is BuildingTile && tile.construction.building == building) {
                tile.copy(construction = NoConstruction)
            } else {
                tile
            }
        }))
    }

    fun removeStreet(index: Int): Town {
        val oldTile = map.getRequiredTile(index)

        require(oldTile.construction is StreetTile) { "Tile $index is not a street!" }

        val tile = oldTile.copy(construction = NoConstruction)

        return updateTile(index, tile)
    }

    fun setTerrain(index: Int, terrain: Terrain): Town {
        val oldTile = map.getRequiredTile(index)
        val tile = oldTile.copy(terrain = terrain)

        return updateTile(index, tile)
    }

    private fun updateTile(index: Int, tile: TownTile): Town {
        val tiles = map.tiles.update(index, tile)

        return copy(map = map.copy(tiles = tiles))
    }

    private fun updateTiles(tiles: Map<Int, TownTile>): Town {
        return copy(map = map.copy(tiles = map.tiles.update(tiles)))
    }


}