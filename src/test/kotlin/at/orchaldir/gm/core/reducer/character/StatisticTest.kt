package at.orchaldir.gm.core.reducer.character

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.UpdateStatistic
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.statistic.Attribute
import at.orchaldir.gm.core.model.character.statistic.BasedOnStatistic
import at.orchaldir.gm.core.model.character.statistic.Statistic
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class StatisticTest {

    private val STATE = State(
        listOf(
            Storage(listOf(Statistic(STATISTIC_ID_0), Statistic(STATISTIC_ID_1))),
        )
    )

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateStatistic(Statistic(UNKNOWN_STATISTIC_ID))

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot be based on a unknown statistic`() {
            val statistic = Statistic(STATISTIC_ID_0, data = Attribute(BasedOnStatistic(UNKNOWN_STATISTIC_ID)))
            val action = UpdateStatistic(statistic)

            assertIllegalArgument("Requires unknown Statistic 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Update with all values set`() {
            val statistic = Statistic(STATISTIC_ID_0, data = Attribute(BasedOnStatistic(STATISTIC_ID_1)))
            val action = UpdateStatistic(statistic)

            assertEquals(
                statistic,
                REDUCER.invoke(STATE, action).first.getStatisticStorage().get(STATISTIC_ID_0)
            )
        }
    }

}