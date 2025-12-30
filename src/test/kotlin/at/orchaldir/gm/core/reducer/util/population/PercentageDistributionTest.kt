package at.orchaldir.gm.core.reducer.util.population

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.util.PercentageDistribution
import at.orchaldir.gm.core.reducer.util.validatePercentageDistribution
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PercentageDistributionTest {
    private val state = State(
        Storage(
            listOf(
                Race(RACE_ID_0),
                Race(RACE_ID_1),
                Race(RACE_ID_2),
            )
        )
    )
    private val valid = PercentageDistribution(mapOf(RACE_ID_0 to QUARTER, RACE_ID_1 to HALF))

    @Test
    fun `With an unknown race`() {
        assertPopulation(
            PercentageDistribution(mapOf(UNKNOWN_RACE_ID to HALF)),
            "Requires unknown Race 99!",
        )
    }

    @Test
    fun `A race's percentage must be greater than 0`() {
        assertPopulation(
            PercentageDistribution(mapOf(RACE_ID_0 to ZERO)),
            "The population of Race 0 must be > 0%!",
        )
    }

    @Test
    fun `A race's percentage must be less or equal than 100`() {
        assertPopulation(
            PercentageDistribution(mapOf(RACE_ID_0 to Factor.fromPercentage(101))),
            "The population of Race 0 must be <= 100%!",
        )
    }

    @Test
    fun `The total population of all Races must be less or equal than 100`() {
        assertPopulation(
            PercentageDistribution(mapOf(RACE_ID_0 to HALF, RACE_ID_1 to THREE_QUARTER)),
            "The total population of all Races must be <= 100%!",
        )
    }

    @Test
    fun `A valid population`() {
        validatePercentageDistribution(state.getRaceStorage(), valid)
    }

    @Test
    fun `Calculate the defined percentages`() {
        assertEquals(THREE_QUARTER, valid.getDefinedPercentages())
    }

    @Test
    fun `Calculate the undefined percentages`() {
        assertEquals(QUARTER, valid.getUndefinedPercentages())
    }

    @Test
    fun `Calculate the number`() {
        assertEquals(100, valid.getNumber(400, RACE_ID_0))
        assertEquals(200, valid.getNumber(400, RACE_ID_1))
    }

    @Test
    fun `Calculate the number if not included`() {
        assertEquals(0, valid.getNumber(400, UNKNOWN_RACE_ID))
    }

    private fun assertPopulation(distribution: PercentageDistribution<RaceId>, message: String) {
        assertIllegalArgument(message) {
            validatePercentageDistribution(state.getRaceStorage(), distribution)
        }
    }

}