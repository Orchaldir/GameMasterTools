package at.orchaldir.gm.core.selector.economy

import at.orchaldir.gm.BUILDING_ID_0
import at.orchaldir.gm.BUSINESS_ID_0
import at.orchaldir.gm.CHARACTER_ID_0
import at.orchaldir.gm.DAY0
import at.orchaldir.gm.DISEASE_ID_0
import at.orchaldir.gm.DISEASE_ID_1
import at.orchaldir.gm.JOB_ID_0
import at.orchaldir.gm.TEXT_ID_0
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.Employed
import at.orchaldir.gm.core.model.character.EmploymentStatus
import at.orchaldir.gm.core.model.character.Unemployed
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.economy.job.JobId
import at.orchaldir.gm.core.model.health.Disease
import at.orchaldir.gm.core.model.item.text.Text
import at.orchaldir.gm.core.model.util.BusinessReference
import at.orchaldir.gm.core.model.util.Dead
import at.orchaldir.gm.core.model.util.DeathByDisease
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.util.HistoryEntry
import at.orchaldir.gm.core.model.util.LongTermCareIn
import at.orchaldir.gm.core.model.util.Position
import at.orchaldir.gm.core.model.util.Reference
import at.orchaldir.gm.core.model.util.UndefinedReference
import at.orchaldir.gm.core.model.util.origin.EvolvedElement
import at.orchaldir.gm.core.model.util.origin.ModifiedElement
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.selector.health.canDeleteDisease
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class BusinessTest {

    @Nested
    inner class CanDeleteTest {
        private val business = Business(BUSINESS_ID_0)
        private val state = State(
            listOf(
                Storage(business),
            )
        )

        @Test
        fun `Cannot delete a business that created another element`() {
            val building = Building(BUILDING_ID_0, builder = BusinessReference(BUSINESS_ID_0))
            val newState = state.updateStorage(Storage(building))

            assertCanDelete(newState, BUILDING_ID_0)
        }

        @Test
        fun `Cannot delete a business that owns another element`() {
            val ownership = History<Reference>(BusinessReference(BUSINESS_ID_0))
            val building = Building(BUILDING_ID_0, ownership = ownership)
            val newState = state.updateStorage(Storage(building))

            assertCanDelete(newState, BUILDING_ID_0)
        }

        @Test
        fun `Cannot delete a business where a character is employed`() {
            val employmentStatus = History<EmploymentStatus>(Employed(BUSINESS_ID_0, JOB_ID_0))
            val character = Character(CHARACTER_ID_0, employmentStatus = employmentStatus)
            val newState = state.updateStorage(Storage(character))

            assertCanDelete(newState, CHARACTER_ID_0)
        }

        @Test
        fun `Cannot delete a business where a character was previously employed`() {
            val entry = HistoryEntry<EmploymentStatus>(Employed(BUSINESS_ID_0, JobId(0)), DAY0)
            val character = Character(CHARACTER_ID_0, employmentStatus = History(Unemployed, listOf(entry)))
            val newState = state.updateStorage(Storage(character))

            assertCanDelete(newState, CHARACTER_ID_0)
        }

        @Test
        fun `Cannot delete a business with a long term patient`() {
            val housingStatus = History<Position>(LongTermCareIn(BUSINESS_ID_0))
            val character = Character(CHARACTER_ID_0, housingStatus = housingStatus)
            val newState = state.updateStorage(Storage(character))

            assertCanDelete(newState, CHARACTER_ID_0)
        }

        @Test
        fun `Cannot delete a business that published a text`() {
            val character = Text(TEXT_ID_0, publisher = BUSINESS_ID_0)
            val newState = state.updateStorage(Storage(character))

            assertCanDelete(newState, TEXT_ID_0)
        }

        private fun <ID : Id<ID>> assertCanDelete(state: State, blockingId: ID) {
            assertEquals(
                DeleteResult(BUSINESS_ID_0).addId(blockingId),
                state.canDeleteBusiness(BUSINESS_ID_0)
            )
        }
    }

}