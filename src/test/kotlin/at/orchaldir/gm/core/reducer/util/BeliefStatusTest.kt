package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.WorshipsGod
import at.orchaldir.gm.core.model.character.WorshipsPantheon
import at.orchaldir.gm.core.model.religion.God
import at.orchaldir.gm.core.model.religion.Pantheon
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Test

class BeliefStatusTest {

    private val state = State(
        listOf(
            Storage(CALENDAR0),
            Storage(God(GOD_ID_0)),
            Storage(Pantheon(PANTHEON_ID_0)),
        )
    )

    @Test
    fun `Cannot use unknown god`() {
        assertIllegalArgument("The belief's god 99 doesn't exist!") {
            checkBeliefStatusHistory(state, History(WorshipsGod(UNKNOWN_GOD_ID)), DAY0)
        }
    }

    @Test
    fun `Cannot use unknown pantheon`() {
        assertIllegalArgument("The belief's pantheon 99 doesn't exist!") {
            checkBeliefStatusHistory(state, History(WorshipsPantheon(UNKNOWN_PANTHEON_ID)), DAY0)
        }
    }

    @Test
    fun `Character worships a valid god`() {
        checkBeliefStatusHistory(state, History(WorshipsGod(GOD_ID_0)), DAY0)
    }

    @Test
    fun `Character worships a valid pantheon`() {
        checkBeliefStatusHistory(state, History(WorshipsPantheon(PANTHEON_ID_0)), DAY0)
    }

}