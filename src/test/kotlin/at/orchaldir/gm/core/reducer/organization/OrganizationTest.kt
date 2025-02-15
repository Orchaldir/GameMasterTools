package at.orchaldir.gm.core.reducer.organization

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteOrganization
import at.orchaldir.gm.core.action.UpdateOrganization
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.organization.Organization
import at.orchaldir.gm.core.model.util.CreatedByCharacter
import at.orchaldir.gm.core.model.util.CreatedByOrganization
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class OrganizationTest {

    private val organization0 = Organization(ORGANIZATION_ID_0)
    private val STATE = State(
        listOf(
            Storage(CALENDAR0),
            Storage(organization0),
        )
    )

    @Nested
    inner class DeleteTest {
        val action = DeleteOrganization(ORGANIZATION_ID_0)

        @Test
        fun `Can delete an existing organization`() {
            assertEquals(0, REDUCER.invoke(STATE, action).first.getOrganizationStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            assertIllegalArgument("Requires unknown Organization 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot delete a organization that build a building`() {
            val building = Building(BUILDING_ID_0, builder = CreatedByOrganization(ORGANIZATION_ID_0))
            val state = STATE.updateStorage(Storage(building))

            assertIllegalArgument("Cannot delete organization 0, because of built buildings!") {
                REDUCER.invoke(state, action)
            }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Successfully update a organization`() {
            val organization = Organization(ORGANIZATION_ID_0, date = DAY0)
            val action = UpdateOrganization(organization)

            assertEquals(
                organization,
                REDUCER.invoke(STATE, action).first.getOrganizationStorage().get(ORGANIZATION_ID_0)
            )
        }

        @Test
        fun `Cannot update unknown organization`() {
            val action = UpdateOrganization(Organization(ORGANIZATION_ID_0))

            assertIllegalArgument("Requires unknown Organization 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Date is in the future`() {
            val action = UpdateOrganization(Organization(ORGANIZATION_ID_0, date = FUTURE_DAY_0))

            assertIllegalArgument("Date (Organization) is in the future!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Founder must exist`() {
            val state = State(listOf(Storage(Organization(ORGANIZATION_ID_0))))
            val action =
                UpdateOrganization(Organization(ORGANIZATION_ID_0, founder = CreatedByCharacter(CHARACTER_ID_0)))

            assertIllegalArgument("Cannot use an unknown character 0 as founder!") { REDUCER.invoke(state, action) }
        }

    }

}