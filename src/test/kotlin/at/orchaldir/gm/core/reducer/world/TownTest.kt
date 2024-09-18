package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.core.action.DeleteTown
import at.orchaldir.gm.core.action.UpdateTerrain
import at.orchaldir.gm.core.action.UpdateTown
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.terrain.River
import at.orchaldir.gm.core.model.world.terrain.RiverId
import at.orchaldir.gm.core.model.world.terrain.RiverTerrain
import at.orchaldir.gm.core.model.world.terrain.TerrainType
import at.orchaldir.gm.core.model.world.town.Town
import at.orchaldir.gm.core.model.world.town.TownId
import at.orchaldir.gm.core.model.world.town.TownTile
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.map.MapSize2d
import at.orchaldir.gm.utils.map.TileMap2d
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private val ID0 = TownId(0)
private val RIVER0 = RiverId(0)

class TownTest {

    @Nested
    inner class DeleteTest {

        @Test
        fun `Can delete an existing Town`() {
            val state = State(Storage(Town(ID0)))
            val action = DeleteTown(ID0)

            assertEquals(0, REDUCER.invoke(state, action).first.getTownStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeleteTown(ID0)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateTown(Town(ID0))

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Update is valid`() {
            val state = State(Storage(Town(ID0)))
            val town = Town(ID0, "Test")
            val action = UpdateTown(town)

            assertEquals(town, REDUCER.invoke(state, action).first.getTownStorage().get(ID0))
        }
    }

    @Nested
    inner class UpdateTerrainTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateTerrain(ID0, TerrainType.Plain, 0, 0)
            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Set to river`() {
            val oldMap = TileMap2d(MapSize2d(2, 1), listOf(TownTile(), TownTile()))
            val newMap = TileMap2d(MapSize2d(2, 1), listOf(TownTile(), TownTile(RiverTerrain(RIVER0))))
            val oldTown = Town(ID0, map = oldMap)
            val newTown = Town(ID0, map = newMap)
            val state = State(listOf(Storage(River(RIVER0)), Storage(oldTown)))
            val action = UpdateTerrain(ID0, TerrainType.River, 0, 1)

            assertEquals(newTown, REDUCER.invoke(state, action).first.getTownStorage().get(ID0))
        }
    }

}