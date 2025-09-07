package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.ARCHITECTURAL_ID_0
import at.orchaldir.gm.ARCHITECTURAL_ID_1
import at.orchaldir.gm.BUILDING_ID_0
import at.orchaldir.gm.BUSINESS_ID_0
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.building.ArchitecturalStyle
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.model.world.building.BuildingId
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ArchitecturalStyleTest {

    @Nested
    inner class CanDeleteTest {
        private val style = ArchitecturalStyle(ARCHITECTURAL_ID_0)
        private val state = State(
            listOf(
                Storage(style),
            )
        )

        @Test
        fun `Cannot delete a style used by building`() {
            val building = Building(BUILDING_ID_0, style = ARCHITECTURAL_ID_0)
            val newState = state.updateStorage(Storage(building))

            assertCanDelete(newState, BUILDING_ID_0)
        }

        @Test
        fun `Cannot delete a revived style`() {
            val style1 = ArchitecturalStyle(ARCHITECTURAL_ID_1, revival = ARCHITECTURAL_ID_0)
            val newState = state.updateStorage(Storage(listOf(style, style1)))

            assertCanDelete(newState, ARCHITECTURAL_ID_1)
        }

        private fun <ID : Id<ID>> assertCanDelete(state: State, blockingId: ID) {
            assertEquals(
                DeleteResult(ARCHITECTURAL_ID_0).addId(blockingId),
                state.canDeleteArchitecturalStyle(ARCHITECTURAL_ID_0)
            )
        }
    }

}