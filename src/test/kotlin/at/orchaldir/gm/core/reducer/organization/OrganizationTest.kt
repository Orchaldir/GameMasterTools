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
import at.orchaldir.gm.core.model.util.HistoryEntry
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


class OrganizationTest {

    private val unknownRank = 42
    private val organization0 = Organization(ORGANIZATION_ID_0)
    private val state = State(
        listOf(
            Storage(CALENDAR0),
            Storage(Character(CHARACTER_ID_0, birthDate = DAY1)),
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
        fun `Holiday must exist`() {
            val organization = Organization(ORGANIZATION_ID_0, holidays = setOf(UNKNOWN_HOLIDAY_ID))
            val action = UpdateOrganization(organization)

            assertIllegalArgument("Requires unknown Holiday 99!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Organization must have at least 1 rank`() {
            val organization = Organization(ORGANIZATION_ID_0, memberRanks = emptyList())
            val action = UpdateOrganization(organization)

            assertIllegalArgument("Organization must have at least 1 rank!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Member must exist`() {
            val organization = Organization(ORGANIZATION_ID_0, members = mapOf(UNKNOWN_CHARACTER_ID to History(0)))
            val action = UpdateOrganization(organization)

            assertIllegalArgument("Cannot use an unknown character 99 as member!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Rank must exist`() {
            val organization = Organization(ORGANIZATION_ID_0, members = mapOf(CHARACTER_ID_0 to History(unknownRank)))
            val action = UpdateOrganization(organization)

            assertIllegalArgument("Cannot use an unknown rank 42!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Previous rank must exist`() {
            val history: History<Int?> = History(0, HistoryEntry(unknownRank, DAY1))
            val organization = Organization(ORGANIZATION_ID_0, members = mapOf(CHARACTER_ID_0 to history))
            val action = UpdateOrganization(organization)

            assertIllegalArgument("Cannot use an unknown 1.previous rank 42!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `A member without history must have a rank now`() {
            val organization = Organization(ORGANIZATION_ID_0, members = mapOf(CHARACTER_ID_0 to History(null)))
            val action = UpdateOrganization(organization)

            assertIllegalArgument("Member 0 was never a member!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `A member history entry cannot be before the organization or character existed`() {
            val history: History<Int?> = History(0, HistoryEntry(null, DAY0))
            val organization = Organization(ORGANIZATION_ID_0, members = mapOf(CHARACTER_ID_0 to history))
            val action = UpdateOrganization(organization)

            assertIllegalArgument("1.previous rank's until is too early!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `A member cannot have the same rank twice after each other`() {
            val history: History<Int?> = History(0, HistoryEntry(0, DAY2))
            val organization = Organization(ORGANIZATION_ID_0, members = mapOf(CHARACTER_ID_0 to history))
            val action = UpdateOrganization(organization)

            assertIllegalArgument("The rank is the same as the previous one for member 0!") {
                REDUCER.invoke(
                    state,
                    action
                )
            }
        }

    }

}