package at.orchaldir.gm.core.reducer.util.population

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.util.population.ElementDistribution
import at.orchaldir.gm.core.reducer.util.validateElementDistribution
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.HALF
import at.orchaldir.gm.utils.math.THREE_QUARTER
import at.orchaldir.gm.utils.math.ZERO
import org.junit.jupiter.api.Test

class ElementDistributionTest {
    private val state = State(
        Storage(
            listOf(
                Race(RACE_ID_0),
                Race(RACE_ID_1),
                Race(RACE_ID_2),
            )
        )
    )

    @Test
    fun `With an unknown race`() {
        assertPopulation(
            ElementDistribution(mapOf(UNKNOWN_RACE_ID to HALF)),
            "Requires unknown Race 99!",
        )
    }

    @Test
    fun `A race's percentage must be greater than 0`() {
        assertPopulation(
            ElementDistribution(mapOf(RACE_ID_0 to ZERO)),
            "The population of Race 0 must be > 0%!",
        )
    }

    @Test
    fun `A race's percentage must be less or equal than 100`() {
        assertPopulation(
            ElementDistribution(mapOf(RACE_ID_0 to Factor.fromPercentage(101))),
            "The population of Race 0 must be <= 100%!",
        )
    }

    @Test
    fun `The total population of all Races must be less or equal than 100`() {
        assertPopulation(
            ElementDistribution(mapOf(RACE_ID_0 to HALF, RACE_ID_1 to THREE_QUARTER)),
            "The total population of all Races must be <= 100%!",
        )
    }

    @Test
    fun `A valid population`() {
        validateElementDistribution(
            state.getRaceStorage(),
            ElementDistribution(mapOf(RACE_ID_0 to HALF, RACE_ID_1 to HALF))
        )
    }

    private fun assertPopulation(distribution: ElementDistribution<RaceId>, message: String) {
        assertIllegalArgument(message) {
            validateElementDistribution(state.getRaceStorage(), distribution)
        }
    }

}