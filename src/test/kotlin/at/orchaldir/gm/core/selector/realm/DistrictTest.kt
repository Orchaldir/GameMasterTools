package at.orchaldir.gm.core.selector.realm

import at.orchaldir.gm.BUSINESS_ID_0
import at.orchaldir.gm.CHARACTER_ID_0
import at.orchaldir.gm.DISTRICT_ID_0
import at.orchaldir.gm.DISTRICT_ID_1
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.realm.District
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.util.InDistrict
import at.orchaldir.gm.core.model.util.Position
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DistrictTest {

    @Nested
    inner class CanDeleteTest {
        private val district = District(DISTRICT_ID_0)
        private val state = State(
            listOf(
                Storage(district),
            )
        )
        private val position = InDistrict(DISTRICT_ID_0)

        @Test
        fun `Cannot delete an element used as home`() {
            val housingStatus = History<Position>(position)
            val character = Character(CHARACTER_ID_0, housingStatus = housingStatus)
            val newState = state.updateStorage(character)

            failCanDelete(newState, CHARACTER_ID_0)
        }

        @Test
        fun `Cannot delete an element used as a position`() {
            val business = Business(BUSINESS_ID_0, position = position)
            val newState = state.updateStorage(business)

            failCanDelete(newState, BUSINESS_ID_0)
        }

        @Test
        fun `Cannot delete a district that has neighborhoods`() {
            val district = District(DISTRICT_ID_1, position = InDistrict(DISTRICT_ID_0))
            val newState = state.updateStorage(district)

            failCanDelete(newState, DISTRICT_ID_1)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(DeleteResult(DISTRICT_ID_0).addId(blockingId), state.canDeleteDistrict(DISTRICT_ID_0))
        }
    }

}