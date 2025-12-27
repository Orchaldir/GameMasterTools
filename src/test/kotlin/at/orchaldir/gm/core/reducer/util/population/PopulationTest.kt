package at.orchaldir.gm.core.reducer.util.population

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.util.population.*
import at.orchaldir.gm.core.reducer.util.validatePopulation
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.HALF
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class PopulationTest {
    private val state = State(
        listOf(
            Storage(Culture(CULTURE_ID_0)),
            Storage(
                listOf(
                    Race(RACE_ID_0),
                    Race(RACE_ID_1),
                    Race(RACE_ID_2),
                )
            )
        )
    )

    @Nested
    inner class AbstractPopulationTest {

        @Test
        fun `With an unknown race`() {
            assertPopulation(
                AbstractPopulation(races = setOf(UNKNOWN_RACE_ID)),
                "Requires unknown Race 99!",
            )
        }

        @Test
        fun `With an unknown culture`() {
            assertPopulation(
                AbstractPopulation(cultures = setOf(UNKNOWN_CULTURE_ID)),
                "Requires unknown Culture 99!",
            )
        }

        @Test
        fun `A valid population`() {
            validatePopulation(state, AbstractPopulation(races = setOf(RACE_ID_0)))
        }

    }

    @Nested
    inner class PopulationPerRaceTest {

        @Test
        fun `The total population must be greater or equal 0`() {
            assertTotalPopulation(PopulationDistribution(-1, ElementDistribution()))
        }

        @Test
        fun `With an unknown culture`() {
            assertPopulation(
                PopulationDistribution(100, cultures = ElementDistribution(mapOf(UNKNOWN_CULTURE_ID to HALF))),
                "Requires unknown Culture 99!",
            )
        }

        @Test
        fun `With an unknown race`() {
            assertPopulation(
                PopulationDistribution(100, ElementDistribution(mapOf(UNKNOWN_RACE_ID to HALF))),
                "Requires unknown Race 99!",
            )
        }

        @Test
        fun `A valid population`() {
            val cultures = ElementDistribution(mapOf(CULTURE_ID_0 to HALF))
            val races = ElementDistribution(mapOf(RACE_ID_0 to HALF, RACE_ID_1 to HALF))
            val distribution = PopulationDistribution(100, races, cultures)

            validatePopulation(state, distribution)
        }

    }

    @Nested
    inner class TotalPopulationTest {

        @Test
        fun `The total population must be greater or equal 0`() {
            assertTotalPopulation(TotalPopulation(-1))
        }

        @Test
        fun `With an unknown race`() {
            assertPopulation(
                TotalPopulation(100, setOf(UNKNOWN_RACE_ID)),
                "Requires unknown Race 99!",
            )
        }

        @Test
        fun `With an unknown culture`() {
            assertPopulation(
                TotalPopulation(100, cultures = setOf(UNKNOWN_CULTURE_ID)),
                "Requires unknown Culture 99!",
            )
        }

        @Test
        fun `A valid population`() {
            validatePopulation(state, TotalPopulation(100, races = setOf(RACE_ID_0)))
        }

    }

    private fun assertTotalPopulation(population: Population) =
        assertPopulation(population, "The total population must be >= 0!")

    private fun assertPopulation(population: Population, message: String) {
        assertIllegalArgument(message) {
            validatePopulation(state, population)
        }
    }

}