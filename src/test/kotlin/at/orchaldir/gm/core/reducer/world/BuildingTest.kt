package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.core.action.AddBuilding
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.building.BuildingId
import at.orchaldir.gm.core.model.world.street.Street
import at.orchaldir.gm.core.model.world.street.StreetId
import at.orchaldir.gm.core.model.world.town.*
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.map.MapSize2d.Companion.square
import at.orchaldir.gm.utils.map.TileMap2d
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

private val ID0 = BuildingId(0)
private val TOWN0 = TownId(0)
private val STREET0 = StreetId(0)
private val BUILDING_TILE = TownTile(construction = BuildingTile(ID0))
private val STREET_TILE = TownTile(construction = StreetTile(STREET0))

class BuildingTest {

    @Nested
    inner class AddBuildingTest {

        @Test
        fun `Cannot update unknown town`() {
            val action = AddBuilding(TOWN0, 0, square(1))

            assertIllegalArgument("Unknown Town 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Tile is outside the map`() {
            val town = Town(TOWN0)
            val state = State(listOf(Storage(Street(STREET0)), Storage(town)))
            val action = AddBuilding(TOWN0, 100, square(1))

            assertIllegalArgument("Tile 100 is outside the map!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Tile is already a street`() {
            val map = TileMap2d(STREET_TILE)
            val town = Town(TOWN0, map = map)
            val state = State(listOf(Storage(listOf(Street(STREET0))), Storage(town)))
            val action = AddBuilding(TOWN0, 0, square(1))

            assertIllegalArgument("Tile 0 is not empty!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Successfully added a building`() {
            val map = TileMap2d(TownTile())
            val town = Town(TOWN0, map = map)
            val state = State(listOf(Storage(Street(STREET0)), Storage(town)))
            val action = AddBuilding(TOWN0, 0, square(1))

            assertEquals(
                BUILDING_TILE,
                REDUCER.invoke(state, action).first.getTownStorage().get(TOWN0)?.map?.getTile(0)
            )
        }
    }
}