package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.WorshipOfGod
import at.orchaldir.gm.core.model.util.WorshipOfPantheon
import at.orchaldir.gm.core.model.religion.God
import at.orchaldir.gm.core.model.religion.Pantheon
import at.orchaldir.gm.core.model.util.BeliefStatus
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.util.HistoryEntry
import at.orchaldir.gm.core.model.util.UndefinedBeliefStatus
import at.orchaldir.gm.core.selector.util.canDeleteHasBelief
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

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

        @Test
        fun `Can delete god without believers`() {
            assertTrue(state.canDeleteHasBelief(GOD_ID_0))
        }

        @Test
        fun `Can delete pantheon without believers`() {
            assertTrue(state.canDeleteHasBelief(PANTHEON_ID_0))
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