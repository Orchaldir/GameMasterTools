package at.orchaldir.gm.core.selector.ecology.plant

import at.orchaldir.gm.PLANT_ID_0
import at.orchaldir.gm.REGION_ID_0
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.ecology.EcologyWithSets
import at.orchaldir.gm.core.model.ecology.plant.Plant
import at.orchaldir.gm.core.model.world.terrain.Region
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PlantTest {

    @Nested
    inner class CanDeleteTest {
        private val state = State(
            listOf(
                Storage( Plant(PLANT_ID_0)),
            )
        )

        @Test
        fun `Cannot delete a plant that is part of an economy`() {
            val region = Region(REGION_ID_0, ecology = EcologyWithSets(PLANT_ID_0))
            val newState = state.updateStorage(region)

            failCanDelete(newState, REGION_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(
                DeleteResult(PLANT_ID_0).addId(blockingId),
                state.canDeletePlant(PLANT_ID_0)
            )
        }
    }

}