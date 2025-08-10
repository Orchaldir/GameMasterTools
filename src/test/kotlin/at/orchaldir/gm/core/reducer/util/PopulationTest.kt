package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.RACE_ID_0
import at.orchaldir.gm.RACE_ID_1
import at.orchaldir.gm.RACE_ID_2
import at.orchaldir.gm.UNKNOWN_RACE_ID
import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.util.population.Population
import at.orchaldir.gm.core.model.util.population.PopulationPerRace
import at.orchaldir.gm.core.model.util.population.TotalPopulation
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.HALF
import at.orchaldir.gm.utils.math.ZERO
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
            assertTotalPopulation(TotalPopulation(0))
        }

    }

    @Nested
    inner class PopulationPerRaceTest {

        @Test
        fun `The total population must be greater than 0`() {
            assertTotalPopulation(PopulationPerRace(0, emptyMap()))
        }

        @Test
        fun `With an unknown race`() {
            assertIllegalArgument("Requires unknown Race 99!") {
                state.validatePopulation(PopulationPerRace(100, mapOf(UNKNOWN_RACE_ID to HALF)))
            }
        }

        @Test
        fun `A race's percentage must be greater than 0`() {
            assertIllegalArgument("The population of Race 0 must be > 0%!") {
                state.validatePopulation(PopulationPerRace(100, mapOf(RACE_ID_0 to ZERO)))
            }
        }

        @Test
        fun `A race's percentage must be less or equal than 100`() {
            assertIllegalArgument("The population of Race 0 must be <= 100%!") {
                state.validatePopulation(PopulationPerRace(100, mapOf(RACE_ID_0 to Factor.fromPercentage(101))))
            }
        }

    }

    private fun assertTotalPopulation(population: Population) {
        assertIllegalArgument("The total population must be greater than 0!") {
            state.validatePopulation(population)
        }
    }

}