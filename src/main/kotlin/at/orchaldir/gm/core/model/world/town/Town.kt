package at.orchaldir.gm.core.model.world.town

import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.time.Year
import at.orchaldir.gm.core.model.world.building.BuildingId
import at.orchaldir.gm.core.model.world.railway.RailwayTypeId
import at.orchaldir.gm.core.model.world.street.StreetId
import at.orchaldir.gm.core.model.world.terrain.Terrain
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.map.MapSize2d
import at.orchaldir.gm.utils.map.MapSize2d.Companion.square
import at.orchaldir.gm.utils.map.TileMap2d
import at.orchaldir.gm.utils.update
import kotlinx.serialization.Serializable

const val TOWN = "Town"

@JvmInline
@Serializable
value class TownId(val value: Int) : Id<TownId> {

    override fun next() = TownId(value + 1)
    override fun type() = TOWN
    override fun value() = value

}

@Serializable
data class Town(
    val id: TownId,
    val name: String = "Town ${id.value}",
    val map: TileMap2d<TownTile> = TileMap2d(square(10), TownTile()),
    val foundingDate: Date = Year(0),
) : Element<TownId> {

    override fun id() = id
    override fun name() = name

    fun canBuildBuilding(index: Int, size: MapSize2d) = checkTiles(index, size) { it.canBuildBuilding() }

    fun canBuildStreet(index: Int, street: StreetId, connection: TileConnection): Boolean {
        if (!contains(street)) {
            return true
        }

        val x = map.size.toX(index)
        val y = map.size.toY(index)

        return when (connection) {
            TileConnection.Curve -> {
                val left = map.getTile(x - 1, y)?.construction?.canConnectHorizontal(street) ?: false
                val right = map.getTile(x + 1, y)?.construction?.canConnectHorizontal(street) ?: false
                val bottom = map.getTile(x, y - 1)?.construction?.canConnectVertical(street) ?: false
                val top = map.getTile(x, y + 1)?.construction?.canConnectVertical(street) ?: false

                left xor right xor bottom xor top
            }

            TileConnection.Horizontal -> {
                val left = map.getTile(x - 1, y)?.construction?.canConnectHorizontal(street) ?: false
                val right = map.getTile(x + 1, y)?.construction?.canConnectHorizontal(street) ?: false

                left xor right
            }

            TileConnection.Vertical -> {
                val bottom = map.getTile(x, y - 1)?.construction?.canConnectVertical(street) ?: false
                val top = map.getTile(x, y + 1)?.construction?.canConnectVertical(street) ?: false

                bottom xor top
            }
        }
    }

    fun canResizeBuilding(index: Int, size: MapSize2d, building: BuildingId) =
        checkTiles(index, size) { it.canResizeBuilding(building) }

    fun checkTile(x: Int, y: Int, check: (TownTile) -> Boolean) = map
        .getTile(x, y)
        ?.let(check)
        ?: false

    fun checkTiles(index: Int, size: MapSize2d, check: (TownTile) -> Boolean) = map
        .size.toIndices(index, size)
        ?.all { check(map.getRequiredTile(it)) }
        ?: false

    fun contains(street: StreetId) = map.tiles.any { it.getStreet() == street }

    fun build(index: Int, size: MapSize2d, construction: Construction): Town {
        val tiles = mutableMapOf<Int, TownTile>()

        map.size.toIndices(index, size)?.forEach { tileIndex ->
            val oldTile = map.getRequiredTile(tileIndex)

            require(oldTile.canBuildBuilding()) { "Tile $tileIndex is not empty!" }

            tiles[tileIndex] = oldTile.copy(construction = construction)
        } ?: error("Lot with index $index & size ${size.format()} is outside the map!")

        return updateTiles(tiles)
    }

    fun buildStreet(index: Int, street: StreetId, connection: TileConnection): Town {
        val oldTile = map.getRequiredTile(index)

        require(oldTile.canBuildRailway()) { "Cannot build street on tile $index!" }

        val construction = when (oldTile.construction) {
            NoConstruction -> StreetTile(street, connection)
            is CrossingTile -> CrossingTile(
                oldTile.construction.railways,
                oldTile.construction.streets + Pair(street, connection)
            )

            is RailwayTile -> CrossingTile(
                setOf(Pair(oldTile.construction.railwayType, oldTile.construction.connection)),
                setOf(Pair(street, connection)),
            )

            is StreetTile -> CrossingTile(
                emptySet(),
                setOf(Pair(oldTile.construction.street, oldTile.construction.connection), Pair(street, connection)),
            )

            is BuildingTile -> error("Unreachable!")
        }
        val tile = oldTile.copy(construction = construction)

        return updateTile(index, tile)
    }

    fun buildRailway(index: Int, railwayType: RailwayTypeId, connection: TileConnection): Town {
        val oldTile = map.getRequiredTile(index)

        require(oldTile.canBuildRailway()) { "Cannot build railway on tile $index!" }

        val construction = when (oldTile.construction) {
            NoConstruction -> RailwayTile(railwayType, connection)
            is CrossingTile -> CrossingTile(
                oldTile.construction.railways + Pair(railwayType, connection),
                oldTile.construction.streets
            )
            is RailwayTile -> CrossingTile(
                setOf(
                    Pair(
                        oldTile.construction.railwayType,
                        oldTile.construction.connection,
                    ),
                    Pair(railwayType, connection),
                ),
                emptySet(),
            )

            is StreetTile -> CrossingTile(
                setOf(Pair(railwayType, connection)),
                setOf(Pair(oldTile.construction.street, oldTile.construction.connection)),
            )
            is BuildingTile -> error("Unreachable!")
        }
        val tile = oldTile.copy(construction = construction)

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

    fun removeStreet(index: Int, street: StreetId): Town {
        val oldTile = map.getRequiredTile(index)

        require(oldTile.construction is StreetTile || oldTile.construction is CrossingTile) { "Tile $index is not a railway!" }

        val construction = when (oldTile.construction) {
            is StreetTile -> NoConstruction
            is CrossingTile -> {
                val streets = oldTile.construction.streets
                    .filter { it.first != street }
                    .toSet()

                if (streets.size == 1 && oldTile.construction.railways.isEmpty()) {
                    val pair = streets.first()
                    StreetTile(pair.first, pair.second)
                } else {

                    CrossingTile(oldTile.construction.railways, streets)
                }
            }

            else -> error("Tile $index is not a street!")
        }
        val tile = oldTile.copy(construction = construction)

        return updateTile(index, tile)
    }

    fun removeRailway(index: Int, railwayType: RailwayTypeId): Town {
        val oldTile = map.getRequiredTile(index)

        require(oldTile.construction is RailwayTile || oldTile.construction is CrossingTile) { "Tile $index is not a railway!" }

        val construction = when (oldTile.construction) {
            is RailwayTile -> NoConstruction
            is CrossingTile -> {
                val railways = oldTile.construction.railways
                    .filter { it.first != railwayType }
                    .toSet()

                if (railways.size == 1 && oldTile.construction.streets.isEmpty()) {
                    val pair = railways.first()
                    RailwayTile(pair.first, pair.second)
                } else {

                    CrossingTile(railways, oldTile.construction.streets)
                }
            }

            else -> error("Tile $index is not a railway!")
        }
        val tile = oldTile.copy(construction = construction)

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