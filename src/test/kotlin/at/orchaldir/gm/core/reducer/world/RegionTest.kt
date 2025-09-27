package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.UpdateAction
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.InRegion
import at.orchaldir.gm.core.model.util.InTown
import at.orchaldir.gm.core.model.world.terrain.Battlefield
import at.orchaldir.gm.core.model.world.terrain.Region
import at.orchaldir.gm.core.model.world.terrain.Wasteland
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
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

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateAction(region0)

            assertIllegalArgument("Requires unknown Region 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot use an unknown region as position`() {
            val region = Region(REGION_ID_0, position = InRegion(UNKNOWN_REGION_ID))
            val action = UpdateAction(region)

            assertIllegalArgument("Requires unknown Region 99 as position!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot use an invalid type as position`() {
            val region = Region(REGION_ID_0, position = InTown(TOWN_ID_0))
            val action = UpdateAction(region)

            assertIllegalArgument("Position has invalid type Town!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot use unknown battle`() {
            val region = Region(REGION_ID_0, data = Battlefield(UNKNOWN_BATTLE_ID))
            val action = UpdateAction(region)

            assertIllegalArgument("Requires unknown Battle 99!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot use unknown catastrophe`() {
            val region = Region(REGION_ID_0, data = Wasteland(UNKNOWN_CATASTROPHE_ID))
            val action = UpdateAction(region)

            assertIllegalArgument("Requires unknown Catastrophe 99!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Update is valid`() {
            val region = Region(REGION_ID_0, NAME)
            val action = UpdateAction(region)

            assertEquals(region, REDUCER.invoke(state, action).first.getRegionStorage().get(REGION_ID_0))
        }
    }

}