package at.orchaldir.gm.core.reducer.world.town

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteTownMap
import at.orchaldir.gm.core.action.UpdateTownMap
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.Town
import at.orchaldir.gm.core.model.world.street.Street
import at.orchaldir.gm.core.model.world.street.StreetTemplate
import at.orchaldir.gm.core.model.world.terrain.HillTerrain
import at.orchaldir.gm.core.model.world.terrain.MountainTerrain
import at.orchaldir.gm.core.model.world.terrain.RiverTerrain
import at.orchaldir.gm.core.model.world.town.BuildingTile
import at.orchaldir.gm.core.model.world.town.StreetTile
import at.orchaldir.gm.core.model.world.town.TownMap
import at.orchaldir.gm.core.model.world.town.TownTile
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.map.TileMap2d
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TownMapTest {

    private val STATE = State(
        listOf(
            Storage(CALENDAR0),
            Storage(Street(STREET_ID_0)),
            Storage(StreetTemplate(STREET_TYPE_ID_0)),
            Storage(Town(TOWN_ID_0)),
            Storage(TownMap(TOWN_MAP_ID_0)),
        )
    )

    @Nested
    inner class DeleteTest {

        private val action = DeleteTownMap(TOWN_MAP_ID_0)

        @Test
        fun `Can delete an existing TownMap`() {
            val state = State(Storage(TownMap(TOWN_MAP_ID_0)))

            assertEquals(0, REDUCER.invoke(state, action).first.getTownMapStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateTownMap(TownMap(TOWN_MAP_ID_0))

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Nested
        inner class MapTest {

            @Test
            fun `Building must exist`() {
                testValid("Building", TownTile(construction = BuildingTile(UNKNOWN_BUILDING_ID)))
            }

            @Test
            fun `Hill must exist`() {
                val terrain = HillTerrain(UNKNOWN_MOUNTAIN_ID)
                testValid("Mountain", TownTile(terrain))
            }

            @Test
            fun `Mountain must exist`() {
                val terrain = MountainTerrain(UNKNOWN_MOUNTAIN_ID)
                testValid("Mountain", TownTile(terrain))
            }

            @Test
            fun `River must exist`() {
                val terrain = RiverTerrain(UNKNOWN_RIVER_ID)
                testValid("River", TownTile(terrain))
            }

            @Test
            fun `Street template must exist`() {
                testValid("Street Template", TownTile(construction = StreetTile(UNKNOWN_STREET_TYPE_ID)))
            }

            @Test
            fun `Street must exist`() {
                val construction = StreetTile(STREET_TYPE_ID_0, UNKNOWN_STREET_ID)
                testValid("Street", TownTile(construction = construction))
            }

            private fun testValid(noun: String, tile: TownTile) {
                val map = TileMap2d(tile)
                val action = UpdateTownMap(TownMap(TOWN_MAP_ID_0, map = map))

                assertIllegalArgument("Requires unknown $noun 99!") { REDUCER.invoke(STATE, action) }
            }
        }

        @Test
        fun `Founder must exist`() {
            val town = TownMap(TOWN_MAP_ID_0, town = UNKNOWN_TOWN_ID)
            val action = UpdateTownMap(town)

            assertIllegalArgument("Cannot use an unknown Town 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Update is valid`() {
            val town = TownMap(TOWN_MAP_ID_0, date = DAY0)
            val action = UpdateTownMap(town)

            assertEquals(town, REDUCER.invoke(STATE, action).first.getTownMapStorage().get(TOWN_MAP_ID_0))
        }
    }

}