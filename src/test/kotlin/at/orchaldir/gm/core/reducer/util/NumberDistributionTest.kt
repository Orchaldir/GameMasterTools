package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.util.NumberDistribution
import at.orchaldir.gm.core.reducer.realm.validateNumberDistribution
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.QUARTER
import at.orchaldir.gm.utils.math.THREE_QUARTER
import at.orchaldir.gm.utils.math.ZERO
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class NumberDistributionTest {
    private val state = State(
        Storage(
            listOf(
                Race(RACE_ID_0),
                Race(RACE_ID_1),
                Race(RACE_ID_2),
            )
        )
    )
    private val valid = NumberDistribution(mapOf(RACE_ID_0 to 100, RACE_ID_1 to 300))

    @Test
    fun `With an unknown race`() {
        assertPopulation(
            NumberDistribution(mapOf(UNKNOWN_RACE_ID to 100)),
            "Requires unknown Race 99!",
        )
    }

    @Test
    fun `A race's percentage must be greater than 0`() {
        assertPopulation(
            NumberDistribution(mapOf(RACE_ID_0 to 0)),
            "The population of Race 0 must be > 0!",
        )
    }

    @Test
    fun `A valid population`() {
        validateNumberDistribution(state.getRaceStorage(), valid)
    }

    @Test
    fun `Calculate the total population`() {
        assertEquals(400, valid.calculateTotal())
    }

    @Test
    fun `Calculate the percentage`() {
        assertEquals(QUARTER, valid.getPercentage(RACE_ID_0))
        assertEquals(THREE_QUARTER, valid.getPercentage(RACE_ID_1))
    }

    @Test
    fun `Calculate the percentage if not included`() {
        assertEquals(ZERO, valid.getPercentage(UNKNOWN_RACE_ID))
    }

    private fun assertPopulation(distribution: NumberDistribution<RaceId>, message: String) {
        assertIllegalArgument(message) {
            validateNumberDistribution(state.getRaceStorage(), distribution)
        }
    }

}