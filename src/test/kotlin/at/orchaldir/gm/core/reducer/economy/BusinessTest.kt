package at.orchaldir.gm.core.reducer.economy

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteBusiness
import at.orchaldir.gm.core.action.UpdateBusiness
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.Employed
import at.orchaldir.gm.core.model.character.Unemployed
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.economy.job.JobId
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


class BusinessTest {

    @Nested
    inner class UpdateTest {

        private val STATE = State(
            listOf(
                Storage(Business(BUSINESS_ID_0)),
                Storage(CALENDAR0),
                Storage(Character(CHARACTER_ID_0)),
            )
        )

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateBusiness(Business(UNKNOWN_BUSINESS_ID))

            assertIllegalArgument("Requires unknown Business 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Owner is an unknown character`() {
            val action =
                UpdateBusiness(Business(BUSINESS_ID_0, ownership = History(CharacterReference(CHARACTER_ID_0))))
            val state = STATE.removeStorage(CHARACTER_ID_0)

            assertIllegalArgument("Requires unknown owner (Character 0)!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Founder is an unknown character`() {
            val action = UpdateBusiness(Business(BUSINESS_ID_0, founder = CharacterReference(CHARACTER_ID_0)))
            val state = STATE.removeStorage(CHARACTER_ID_0)

            assertIllegalArgument("Requires unknown Founder (Character 0)!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `In unknown building`() {
            val action = UpdateBusiness(Business(BUSINESS_ID_0, position = InBuilding(UNKNOWN_BUILDING_ID)))

            assertIllegalArgument("Requires unknown position!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Date is in the future`() {
            val action = UpdateBusiness(Business(BUSINESS_ID_0, startDate = FUTURE_DAY_0))

            assertIllegalArgument("Date (Business Founding) is in the future!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Test Success`() {
            val business = Business(BUSINESS_ID_0, Name.init("Test"))
            val action = UpdateBusiness(business)

            assertEquals(business, REDUCER.invoke(STATE, action).first.getBusinessStorage().get(BUSINESS_ID_0))
        }
    }

}