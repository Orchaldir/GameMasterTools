package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.RACE_ID_0
import at.orchaldir.gm.RACE_ID_1
import at.orchaldir.gm.RACE_ID_2
import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.util.population.TotalPopulation
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class PopulationTest {
    private val state = State(
        Storage(
            listOf(
                Race(RACE_ID_0),
                Race(RACE_ID_1),
                Race(RACE_ID_2),
            )
        )
    )

    @Nested
    inner class TotalPopulationTest {

        @Test
        fun `The total population must be greater than 0`() {
            assertIllegalArgument("The total population must be greater than 0!") {
                state.validatePopulation(TotalPopulation(0))
            }
        }

    }

}