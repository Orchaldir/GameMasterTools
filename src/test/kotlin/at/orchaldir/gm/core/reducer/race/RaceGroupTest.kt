package at.orchaldir.gm.core.reducer.race

import at.orchaldir.gm.RACE_GROUP_ID_0
import at.orchaldir.gm.RACE_ID_0
import at.orchaldir.gm.UNKNOWN_RACE_ID
import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.core.action.UpdateAction
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.RaceGroup
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test


class RaceGroupTest {

    private val state = State(
        listOf(
            Storage(listOf(Race(RACE_ID_0))),
            Storage(RaceGroup(RACE_GROUP_ID_0)),
        )
    )

    @Nested
    inner class UpdateTest {
        val action = UpdateAction(RaceGroup(RACE_GROUP_ID_0, races = setOf(RACE_ID_0)))

        @Test
        fun `Cannot update unknown id`() {

            assertIllegalArgument("Requires unknown Race Group 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Race must exist`() {
            val action = UpdateAction(RaceGroup(RACE_GROUP_ID_0, races = setOf(UNKNOWN_RACE_ID)))

            assertIllegalArgument("Requires unknown Race 99!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Successful update`() {
            REDUCER.invoke(state, action)
        }
    }

}