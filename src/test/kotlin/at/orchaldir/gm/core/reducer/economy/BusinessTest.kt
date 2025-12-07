package at.orchaldir.gm.core.reducer.economy

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.UpdateAction
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.util.Abandoned
import at.orchaldir.gm.core.model.util.Alive
import at.orchaldir.gm.core.model.util.CharacterReference
import at.orchaldir.gm.core.model.util.Closed
import at.orchaldir.gm.core.model.util.Dead
import at.orchaldir.gm.core.model.util.Destroyed
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.util.InBuilding
import at.orchaldir.gm.core.model.util.Vanished
import at.orchaldir.gm.core.model.util.VitalStatus
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.reducer.REDUCER
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

        @Nested
        inner class VitalStatusTest {

            @Test
            fun `A business can be alive`() {
                testValidStatus(Alive)
            }

            @Test
            fun `A business cannot die`() {
                testInvalidStatus(Dead(DAY2))
            }

            @Test
            fun `A business cannot vanish`() {
                testInvalidStatus(Vanished(DAY2))
            }

            @Test
            fun `A business cannot be abandoned`() {
                testInvalidStatus(Abandoned(DAY2))
            }

            @Test
            fun `A business can be closed`() {
                testValidStatus(Closed(DAY2))
            }

            @Test
            fun `A business can be destroyed`() {
                testValidStatus(Destroyed(DAY2))
            }

            private fun testValidStatus(status: VitalStatus) = testValidStatus(status, true)
            private fun testInvalidStatus(status: VitalStatus) = testValidStatus(status, false)

            private fun testValidStatus(status: VitalStatus, isValid: Boolean) {
                val element = Business(BUSINESS_ID_0, startDate = DAY0, status = status)
                val action = UpdateAction(element)

                if (isValid) {
                    REDUCER.invoke(STATE, action)
                }
                else {
                    assertIllegalArgument("Invalid vital status ${status.getType()}!") { REDUCER.invoke(STATE, action) }
                }
            }

        }

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateAction(Business(UNKNOWN_BUSINESS_ID))

            assertIllegalArgument("Requires unknown Business 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Owner is an unknown character`() {
            val action =
                UpdateAction(Business(BUSINESS_ID_0, ownership = History(CharacterReference(CHARACTER_ID_0))))
            val state = STATE.removeStorage(CHARACTER_ID_0)

            assertIllegalArgument("Requires unknown owner (Character 0)!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Founder is an unknown character`() {
            val action = UpdateAction(Business(BUSINESS_ID_0, founder = CharacterReference(CHARACTER_ID_0)))
            val state = STATE.removeStorage(CHARACTER_ID_0)

            assertIllegalArgument("Requires unknown Founder (Character 0)!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `In unknown building`() {
            val action = UpdateAction(Business(BUSINESS_ID_0, position = InBuilding(UNKNOWN_BUILDING_ID)))

            assertIllegalArgument("Requires unknown position!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Date is in the future`() {
            val action = UpdateAction(Business(BUSINESS_ID_0, startDate = FUTURE_DAY_0))

            assertIllegalArgument("Date (Start Date) is in the future!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Test Success`() {
            val business = Business(BUSINESS_ID_0, Name.init("Test"))
            val action = UpdateAction(business)

            assertEquals(business, REDUCER.invoke(STATE, action).first.getBusinessStorage().get(BUSINESS_ID_0))
        }
    }

}