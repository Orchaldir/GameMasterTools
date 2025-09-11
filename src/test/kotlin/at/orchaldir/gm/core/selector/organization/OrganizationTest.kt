package at.orchaldir.gm.core.selector.organization

import at.orchaldir.gm.BUILDING_ID_0
import at.orchaldir.gm.CHARACTER_ID_0
import at.orchaldir.gm.DAY0
import at.orchaldir.gm.ORGANIZATION_ID_0
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.organization.Organization
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class OrganizationTest {

    @Nested
    inner class CanDeleteTest {
        private val appearance = Organization(ORGANIZATION_ID_0)
        private val state = State(
            listOf(
                Storage(appearance),
            )
        )

        @Test
        fun `Cannot delete a organization that killed a character`() {
            val dead = Dead(DAY0, KilledBy(OrganizationReference(ORGANIZATION_ID_0)))
            val character = Character(CHARACTER_ID_0, vitalStatus = dead)
            val newState = state.updateStorage(Storage(character))

            failCanDelete(newState, CHARACTER_ID_0)
        }

        @Test
        fun `Cannot delete an organization that created another element`() {
            val building = Building(BUILDING_ID_0, builder = OrganizationReference(ORGANIZATION_ID_0))
            val newState = state.updateStorage(Storage(building))

            failCanDelete(newState, BUILDING_ID_0)
        }

        @Test
        fun `Cannot delete an organization that owns another element`() {
            val ownership = History<Reference>(OrganizationReference(ORGANIZATION_ID_0))
            val building = Building(BUILDING_ID_0, ownership = ownership)
            val newState = state.updateStorage(Storage(building))

            failCanDelete(newState, BUILDING_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(
                DeleteResult(ORGANIZATION_ID_0).addId(blockingId),
                state.canDeleteOrganization(ORGANIZATION_ID_0)
            )
        }
    }

}