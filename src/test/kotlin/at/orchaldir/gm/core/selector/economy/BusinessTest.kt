package at.orchaldir.gm.core.selector.economy

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.Employed
import at.orchaldir.gm.core.model.character.EmploymentStatus
import at.orchaldir.gm.core.model.character.Unemployed
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.economy.job.JobId
import at.orchaldir.gm.core.model.item.text.Text
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.model.world.building.Building
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
            val newState = state.updateStorage(building)

            failCanDelete(newState, BUILDING_ID_0)
        }

        @Test
        fun `Cannot delete a business that owns another element`() {
            val ownership = History<Reference>(BusinessReference(BUSINESS_ID_0))
            val building = Building(BUILDING_ID_0, ownership = ownership)
            val newState = state.updateStorage(building)

            failCanDelete(newState, BUILDING_ID_0)
        }

        @Test
        fun `Cannot delete a business where a character is employed`() {
            val employmentStatus = History<EmploymentStatus>(Employed(BUSINESS_ID_0, JOB_ID_0))
            val character = Character(CHARACTER_ID_0, employmentStatus = employmentStatus)
            val newState = state.updateStorage(character)

            failCanDelete(newState, CHARACTER_ID_0)
        }

        @Test
        fun `Cannot delete a business where a character was previously employed`() {
            val entry = HistoryEntry<EmploymentStatus>(Employed(BUSINESS_ID_0, JobId(0)), DAY0)
            val character = Character(CHARACTER_ID_0, employmentStatus = History(Unemployed, listOf(entry)))
            val newState = state.updateStorage(character)

            failCanDelete(newState, CHARACTER_ID_0)
        }

        @Test
        fun `Cannot delete a business with a long term patient`() {
            val housingStatus = History<Position>(LongTermCareIn(BUSINESS_ID_0))
            val character = Character(CHARACTER_ID_0, housingStatus = housingStatus)
            val newState = state.updateStorage(character)

            failCanDelete(newState, CHARACTER_ID_0)
        }

        @Test
        fun `Cannot delete a business that published a text`() {
            val character = Text(TEXT_ID_0, publisher = BUSINESS_ID_0)
            val newState = state.updateStorage(character)

            failCanDelete(newState, TEXT_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(
                DeleteResult(BUSINESS_ID_0).addId(blockingId),
                state.canDeleteBusiness(BUSINESS_ID_0)
            )
        }
    }

}