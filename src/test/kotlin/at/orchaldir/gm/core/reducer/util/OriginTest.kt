package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.UpdateCharacter
import at.orchaldir.gm.core.action.UpdateIllness
import at.orchaldir.gm.core.model.Data
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.illness.Illness
import at.orchaldir.gm.core.model.illness.IllnessId
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.realm.*
import at.orchaldir.gm.core.model.time.Time
import at.orchaldir.gm.core.model.time.date.Day
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class OriginTest {

    private val state = State(
        listOf(
            Storage(CALENDAR0),
            Storage(Character(CHARACTER_ID_0)),
            Storage(listOf(Illness(ILLNESS_ID_0), Illness(ILLNESS_ID_1), Illness(ILLNESS_ID_2))),
        ),
        data = Data(time = Time(currentDate = Day(10))),
    )

    @Nested
    inner class UpdateTest {

        @Test
        fun `Combined origin fails with without parents`() {
            assertIllegalArgument("The combined origin needs at least 2 parents!") {
                CombinedOrigin<IllnessId>(emptySet())
            }
        }

        @Test
        fun `Combined origin fails 1 parent`() {
            assertIllegalArgument("The combined origin needs at least 2 parents!") {
                CombinedOrigin(setOf(ILLNESS_ID_0))
            }
        }

        @Test
        fun `Combined origin fails with unknown parent`() {
            val origin = CombinedOrigin(setOf(UNKNOWN_ILLNESS_ID, ILLNESS_ID_1))
            failOrigin(DAY0, origin, "Requires unknown Illness 99!")
        }

        @Test
        fun `Valid combined origin`() {
            testOrigin(DAY0, CombinedOrigin(setOf(ILLNESS_ID_1, ILLNESS_ID_2)))
        }

        private fun testOrigin(day: Day, origin: Origin<IllnessId>) {
            val illness = Illness(ILLNESS_ID_0, origin = origin)
            val action = UpdateIllness(illness)

            val result = REDUCER.invoke(state, action).first

            assertEquals(
                illness,
                result.getIllnessStorage().getOrThrow(ILLNESS_ID_0)
            )
        }

        private fun failOrigin(day: Day, origin: Origin<IllnessId>, message: String) {
            val illness = Illness(ILLNESS_ID_0, origin = origin)
            val action = UpdateIllness(illness)

            assertIllegalArgument(message) {
                REDUCER.invoke(state, action)
            }
        }
    }
}