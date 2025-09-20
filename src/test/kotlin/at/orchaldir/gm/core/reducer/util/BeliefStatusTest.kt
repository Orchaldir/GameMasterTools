package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterTemplate
import at.orchaldir.gm.core.model.organization.Organization
import at.orchaldir.gm.core.model.religion.God
import at.orchaldir.gm.core.model.religion.Pantheon
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.selector.util.canDeleteTargetOfBelief
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class BeliefStatusTest {

    private val state = State(
        listOf(
            Storage(CALENDAR0),
            Storage(God(GOD_ID_0)),
            Storage(Pantheon(PANTHEON_ID_0)),
        )
    )

    @Nested
    inner class DeleteTest {
        private val worshipOfGod = WorshipOfGod(GOD_ID_0)
        private val worshipOfPantheon = WorshipOfPantheon(PANTHEON_ID_0)

        @Test
        fun `Can delete god without believers`() {
            assertTrue(state.canDeleteTargetOfBelief(GOD_ID_0, DeleteResult(GOD_ID_0)).canDelete())
        }

        @Test
        fun `Cannot delete a god with a believer`() {
            assertCharacter(History(worshipOfGod), GOD_ID_0)
        }

        @Test
        fun `Cannot delete a god with a previous believer`() {
            val previousEntry = HistoryEntry<BeliefStatus>(worshipOfGod, DAY0)

            assertCharacter(History(Atheist, previousEntry), GOD_ID_0)
        }

        @Test
        fun `Cannot delete a god with a worshipping character template`() {
            assertCharacterTemplate(worshipOfGod, GOD_ID_0)
        }

        @Test
        fun `Cannot delete a god with a worshipping organization`() {
            assertOrganization(History(worshipOfGod), GOD_ID_0)
        }

        @Test
        fun `Cannot delete a god with a worshipping organization in the past`() {
            val previousEntry = HistoryEntry<BeliefStatus>(worshipOfGod, DAY0)

            assertOrganization(History(Atheist, previousEntry), GOD_ID_0)
        }

        @Test
        fun `Can delete a pantheon without believers`() {
            assertTrue(state.canDeleteTargetOfBelief(PANTHEON_ID_0, DeleteResult(PANTHEON_ID_0)).canDelete())
        }

        @Test
        fun `Cannot delete a pantheon with a believer`() {
            assertCharacter(History(worshipOfPantheon), PANTHEON_ID_0)
        }

        @Test
        fun `Cannot delete a pantheon with a previous believer`() {
            val previousEntry = HistoryEntry<BeliefStatus>(worshipOfPantheon, DAY0)

            assertCharacter(History(Atheist, previousEntry), PANTHEON_ID_0)
        }

        @Test
        fun `Cannot delete a pantheon with a worshipping character template`() {
            assertCharacterTemplate(worshipOfPantheon, PANTHEON_ID_0)
        }

        @Test
        fun `Cannot delete a pantheon with a worshipping organization`() {
            assertOrganization(History(worshipOfPantheon), PANTHEON_ID_0)
        }

        @Test
        fun `Cannot delete a pantheon with a worshipping organization in the past`() {
            val previousEntry = HistoryEntry<BeliefStatus>(worshipOfPantheon, DAY0)

            assertOrganization(History(Atheist, previousEntry), PANTHEON_ID_0)
        }

        private fun <ID : Id<ID>> assertCharacter(beliefStatus: History<BeliefStatus>, id: ID) {
            val character = Character(CHARACTER_ID_0, beliefStatus = beliefStatus)
            val newState = state.updateStorage(Storage(character))

            failCanDelete(newState, id, CHARACTER_ID_0)
        }

        private fun <ID : Id<ID>> assertCharacterTemplate(beliefStatus: BeliefStatus, id: ID) {
            val template = CharacterTemplate(CHARACTER_TEMPLATE_ID_0, race = RACE_ID_0, belief = beliefStatus)
            val newState = state.updateStorage(Storage(template))

            failCanDelete(newState, id, CHARACTER_TEMPLATE_ID_0)
        }

        private fun <ID : Id<ID>> assertOrganization(beliefStatus: History<BeliefStatus>, id: ID) {
            val organization = Organization(ORGANIZATION_ID_0, beliefStatus = beliefStatus)
            val newState = state.updateStorage(Storage(organization))

            failCanDelete(newState, id, ORGANIZATION_ID_0)
        }

        private fun <ID0 : Id<ID0>, ID1 : Id<ID1>> failCanDelete(state: State, id: ID0, blockingId: ID1) {
            assertEquals(DeleteResult(id).addId(blockingId), (state.canDeleteTargetOfBelief(id, DeleteResult(id))))
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot worship an unknown god`() {
            assertIllegalArgument("The belief's God 99 doesn't exist!") {
                checkBeliefStatusHistory(state, History(WorshipOfGod(UNKNOWN_GOD_ID)), DAY0)
            }
        }

        @Test
        fun `Cannot worship an unknown god in the past`() {
            assertIllegalArgument("The 1.previous belief's God 99 doesn't exist!") {
                val previousEntry = HistoryEntry<BeliefStatus>(WorshipOfGod(UNKNOWN_GOD_ID), DAY0)
                checkBeliefStatusHistory(state, History(UndefinedBeliefStatus, previousEntry), DAY0)
            }
        }

        @Test
        fun `Cannot worship an unknown pantheon`() {
            assertIllegalArgument("The belief's Pantheon 99 doesn't exist!") {
                checkBeliefStatusHistory(state, History(WorshipOfPantheon(UNKNOWN_PANTHEON_ID)), DAY0)
            }
        }

        @Test
        fun `Cannot worship an unknown pantheon in the past`() {
            assertIllegalArgument("The 1.previous belief's Pantheon 99 doesn't exist!") {
                val previousEntry = HistoryEntry<BeliefStatus>(WorshipOfPantheon(UNKNOWN_PANTHEON_ID), DAY0)
                checkBeliefStatusHistory(state, History(UndefinedBeliefStatus, previousEntry), DAY0)
            }
        }

        @Test
        fun `Character worships a valid god`() {
            checkBeliefStatusHistory(state, History(WorshipOfGod(GOD_ID_0)), DAY0)
        }

        @Test
        fun `Character worships a valid pantheon`() {
            checkBeliefStatusHistory(state, History(WorshipOfPantheon(PANTHEON_ID_0)), DAY0)
        }
    }

}