package at.orchaldir.gm.core.selector.rpg

import at.orchaldir.gm.ENCOUNTER_ID_0
import at.orchaldir.gm.ENCOUNTER_ID_1
import at.orchaldir.gm.REGION_ID_0
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.encounter.Encounter
import at.orchaldir.gm.core.model.rpg.encounter.EncounterLookup
import at.orchaldir.gm.core.model.world.terrain.Region
import at.orchaldir.gm.core.selector.rpg.encounter.canDeleteEncounter
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class EncounterTest {

    @Nested
    inner class CanDeleteTest {
        private val encounter = Encounter(ENCOUNTER_ID_0)
        private val state = State(Storage(encounter))

        @Test
        fun `Cannot delete an encounter used by another`() {
            val element = Encounter(ENCOUNTER_ID_1, entry = EncounterLookup(ENCOUNTER_ID_0))
            val newState = state.updateStorage(listOf(encounter, element))

            failCanDelete(newState, ENCOUNTER_ID_1)
        }

        @Test
        fun `Cannot delete an encounter that is used by a regional encounter`() {
            val element = Region(REGION_ID_0, encounter = EncounterLookup(ENCOUNTER_ID_0))
            val newState = state.updateStorage(element)

            failCanDelete(newState, REGION_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(DeleteResult(ENCOUNTER_ID_0).addId(blockingId), state.canDeleteEncounter(ENCOUNTER_ID_0))
        }
    }

}