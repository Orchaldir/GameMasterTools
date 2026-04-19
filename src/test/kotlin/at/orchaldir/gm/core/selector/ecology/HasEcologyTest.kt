package at.orchaldir.gm.core.selector.ecology

import at.orchaldir.gm.PLANT_ID_0
import at.orchaldir.gm.REGION_ID_0
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.ecology.EcologyWithRarity
import at.orchaldir.gm.core.model.ecology.EcologyWithSets
import at.orchaldir.gm.core.model.util.SomeOf
import at.orchaldir.gm.core.model.world.terrain.Region
import at.orchaldir.gm.utils.Id
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class HasEcologyTest {

    private val sets = EcologyWithSets(setOf(PLANT_ID_0))
    private val rarityMap = EcologyWithRarity(SomeOf(PLANT_ID_0))

    @Nested
    inner class CanDeleteEcologyParticipantTest {

        private val state = State()

        @Test
        fun `Cannot delete a common element`() {
            val district = Region(REGION_ID_0, ecology = sets)
            val newState = state.updateStorage(district)

            failCanDelete(newState, REGION_ID_0)
        }

        @Test
        fun `Cannot delete an element used by a economy with percentages`() {
            val district = Region(REGION_ID_0, ecology = rarityMap)
            val newState = state.updateStorage(district)

            failCanDelete(newState, REGION_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            val result = DeleteResult(PLANT_ID_0)

            state.canDeleteEcologyParticipant(PLANT_ID_0, result)

            assertEquals(DeleteResult(PLANT_ID_0).addId(blockingId), result)
        }

    }

}