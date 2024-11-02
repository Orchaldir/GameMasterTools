package at.orchaldir.gm.core.reducer.economy

import at.orchaldir.gm.core.action.DeleteBusiness
import at.orchaldir.gm.core.action.UpdateBusiness
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.economy.business.BusinessId
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.model.world.building.BuildingId
import at.orchaldir.gm.core.model.world.building.SingleBusiness
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private val ID0 = BusinessId(0)
private val BUILDING0 = BuildingId(0)

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

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateBusiness(Business(ID0))

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Business exists`() {
            val state = State(Storage(Business(ID0)))
            val business = Business(ID0, "Test")
            val action = UpdateBusiness(business)

            assertEquals(business, REDUCER.invoke(state, action).first.getBusinessStorage().get(ID0))
        }
    }

}