package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteRegion
import at.orchaldir.gm.core.action.UpdateRegion
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.util.InRegion
import at.orchaldir.gm.core.model.util.InTown
import at.orchaldir.gm.core.model.util.Position
import at.orchaldir.gm.core.model.world.terrain.Battlefield
import at.orchaldir.gm.core.model.world.terrain.Region
import at.orchaldir.gm.core.model.world.terrain.Wasteland
import at.orchaldir.gm.core.model.world.town.MountainTerrain
import at.orchaldir.gm.core.model.world.town.TownMap
import at.orchaldir.gm.core.model.world.town.TownMapId
import at.orchaldir.gm.core.model.world.town.TownTile
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.map.TileMap2d
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class RegionTest {
    val region0 = Region(REGION_ID_0)
    val state = State(
        listOf(
            Storage(region0),
        )
    )
    private val inRegion = InRegion(REGION_ID_0)

    @Nested
    inner class DeleteTest {
        val action = DeleteRegion(REGION_ID_0)

        @Test
        fun `Can delete an existing region`() {
            assertEquals(0, REDUCER.invoke(state, action).first.getRegionStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            assertIllegalArgument("Requires unknown Region 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot delete, if used by a town`() {
            val map = TileMap2d(TownTile(MountainTerrain(REGION_ID_0)))
            val state = state.updateStorage(Storage(TownMap(TownMapId(0), map = map)))

            assertIllegalArgument("Cannot delete Region 0, because it is used!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot delete, if having subregions`() {
            val region1 = Region(REGION_ID_1, position = InRegion(REGION_ID_0))
            val state = state.updateStorage(Storage(listOf(region0, region1)))

            assertIllegalArgument("Cannot delete Region 0, because it is used!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot delete a region used as home`() {
            val housingStatus = History<Position>(inRegion)
            val character = Character(CHARACTER_ID_0, housingStatus = housingStatus)
            val newState = state.updateStorage(Storage(character))

            assertIllegalArgument("Cannot delete Region 0, because it is used!") {
                REDUCER.invoke(newState, action)
            }
        }

        @Test
        fun `Cannot delete a region used as a position`() {
            val plane = Business(BUSINESS_ID_0, position = inRegion)
            val newState = state.updateStorage(Storage(plane))

            assertIllegalArgument("Cannot delete Region 0, because it is used!") {
                REDUCER.invoke(newState, action)
            }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateRegion(region0)

            assertIllegalArgument("Requires unknown Region 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot use an unknown region as position`() {
            val region = Region(REGION_ID_0, position = InRegion(UNKNOWN_REGION_ID))
            val action = UpdateRegion(region)

            assertIllegalArgument("Requires unknown Region 99 as position!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot use an invalid type as position`() {
            val region = Region(REGION_ID_0, position = InTown(TOWN_ID_0))
            val action = UpdateRegion(region)

            assertIllegalArgument("Position has invalid type Town!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot use unknown battle`() {
            val region = Region(REGION_ID_0, data = Battlefield(UNKNOWN_BATTLE_ID))
            val action = UpdateRegion(region)

            assertIllegalArgument("Requires unknown Battle 99!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot use unknown catastrophe`() {
            val region = Region(REGION_ID_0, data = Wasteland(UNKNOWN_CATASTROPHE_ID))
            val action = UpdateRegion(region)

            assertIllegalArgument("Requires unknown Catastrophe 99!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Update is valid`() {
            val region = Region(REGION_ID_0, NAME)
            val action = UpdateRegion(region)

            assertEquals(region, REDUCER.invoke(state, action).first.getRegionStorage().get(REGION_ID_0))
        }
    }

}