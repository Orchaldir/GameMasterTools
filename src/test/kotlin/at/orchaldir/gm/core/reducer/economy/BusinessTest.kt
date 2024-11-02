package at.orchaldir.gm.core.reducer.economy

import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.core.action.DeleteBusiness
import at.orchaldir.gm.core.action.UpdateBuilding
import at.orchaldir.gm.core.action.UpdateBusiness
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.calendar.Calendar
import at.orchaldir.gm.core.model.calendar.CalendarId
import at.orchaldir.gm.core.model.calendar.MonthDefinition
import at.orchaldir.gm.core.model.character.CHARACTER
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.economy.business.BUSINESS
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.economy.business.BusinessId
import at.orchaldir.gm.core.model.util.OwnedByCharacter
import at.orchaldir.gm.core.model.util.Ownership
import at.orchaldir.gm.core.model.world.building.*
import at.orchaldir.gm.core.model.world.street.Street
import at.orchaldir.gm.core.model.world.street.StreetId
import at.orchaldir.gm.core.model.world.town.Town
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.core.reducer.world.*
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.map.MapSize2d
import at.orchaldir.gm.utils.map.TileMap2d
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private val ID0 = BusinessId(0)
private val BUILDING0 = BuildingId(0)
private val CHARACTER0 = CharacterId(0)

class BusinessTest {

    @Nested
    inner class DeleteTest {

        @Test
        fun `Can delete an existing business`() {
            val state = State(Storage(Business(ID0)))
            val action = DeleteBusiness(ID0)

            assertEquals(0, REDUCER.invoke(state, action).first.getBusinessStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeleteBusiness(ID0)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot delete a business used by a building`() {
            val state = State(
                listOf(
                    Storage(Building(BUILDING0, purpose = SingleBusiness(ID0))),
                    Storage(Business(ID0)),
                )
            )
            val action = DeleteBusiness(ID0)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }
    }

    @Nested
    inner class UpdateTest {

        private val CALENDAR = Calendar(CalendarId(0), months = listOf(MonthDefinition("a")))
        private val STATE = State(
            listOf(
                Storage(Business(ID0)),
                Storage(CALENDAR),
                Storage(Character(CHARACTER0)),
            )
        )

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateBusiness(Business(ID0))
            val state = STATE.removeStorage(BUSINESS)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Owner is an unknown character`() {
            val action = UpdateBusiness(Business(ID0, ownership = Ownership(OwnedByCharacter(CHARACTER0))))
            val state = STATE.removeStorage(CHARACTER)

            assertIllegalArgument("Cannot use an unknown character 0 as owner!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Business exists`() {
            val business = Business(ID0, "Test")
            val action = UpdateBusiness(business)

            assertEquals(business, REDUCER.invoke(STATE, action).first.getBusinessStorage().get(ID0))
        }
    }

}