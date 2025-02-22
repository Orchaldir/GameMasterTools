package at.orchaldir.gm.core.reducer.organization

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteOrganization
import at.orchaldir.gm.core.action.UpdateOrganization
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.organization.Organization
import at.orchaldir.gm.core.model.util.CreatedByCharacter
import at.orchaldir.gm.core.model.util.CreatedByOrganization
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class OrganizationTest {

    private val organization0 = Organization(ORGANIZATION_ID_0)
    private val state = State(
        listOf(
            Storage(CALENDAR0),
            Storage(Character(CHARACTER_ID_0)),
            Storage(organization0),
        )
    )

    @Nested
    inner class DeleteTest {
        val action = DeleteOrganization(ORGANIZATION_ID_0)

        @Test
        fun `Can delete an existing organization`() {
            assertEquals(0, REDUCER.invoke(state, action).first.getOrganizationStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            assertIllegalArgument("Requires unknown Organization 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot delete a organization that build a building`() {
            val building = Building(BUILDING_ID_0, builder = CreatedByOrganization(ORGANIZATION_ID_0))
            val newState = state.updateStorage(Storage(building))

            assertIllegalArgument("Cannot delete organization 0, because of built buildings!") {
                REDUCER.invoke(newState, action)
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
                REDUCER.invoke(state, action).first.getOrganizationStorage().get(ORGANIZATION_ID_0)
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

            assertIllegalArgument("Date (Organization) is in the future!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Founder must exist`() {
            val organization = Organization(ORGANIZATION_ID_0, founder = CreatedByCharacter(UNKNOWN_CHARACTER_ID))
            val action = UpdateOrganization(organization)

            assertIllegalArgument("Cannot use an unknown character 99 as founder!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Member must exist`() {
            val organization = Organization(ORGANIZATION_ID_0, members = mapOf(UNKNOWN_CHARACTER_ID to History(0)))
            val action = UpdateOrganization(organization)

            assertIllegalArgument("Cannot use an unknown character 99 as member!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Rank must exist`() {
            val organization = Organization(ORGANIZATION_ID_0, members = mapOf(CHARACTER_ID_0 to History(42)))
            val action = UpdateOrganization(organization)

            assertIllegalArgument("Cannot use an unknown rank 42!") { REDUCER.invoke(state, action) }
        }

    }

}