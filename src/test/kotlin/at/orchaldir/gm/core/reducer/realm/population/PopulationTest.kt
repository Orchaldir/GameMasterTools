package at.orchaldir.gm.core.reducer.realm.population

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.economy.job.AffordableStandardOfLiving
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.realm.population.*
import at.orchaldir.gm.core.model.util.NumberDistribution
import at.orchaldir.gm.core.model.util.PercentageDistribution
import at.orchaldir.gm.core.reducer.realm.validatePopulation
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.HALF
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PopulationTest {
    private val state = State(
        listOf(
            Storage(listOf(Culture(CULTURE_ID_0), Culture(CULTURE_ID_1))),
            Storage(
                listOf(
                    Race(RACE_ID_0),
                    Race(RACE_ID_1),
                    Race(RACE_ID_2),
                )
            )
        )
    )
    private val invalidIncome = AffordableStandardOfLiving(UNKNOWN_STANDARD_ID)
    private val total = TotalPopulationAsNumber(100)
    private val invalidTotal = TotalPopulationAsNumber(-1)

    @Nested
    inner class PopulationWithNumbersTest {
        private val cultures = NumberDistribution(mapOf(CULTURE_ID_0 to 100))
        private val races = NumberDistribution(mapOf(RACE_ID_0 to 100, RACE_ID_1 to 100))
        private val valid = PopulationWithNumbers(races, cultures)

        @Test
        fun `With an unknown culture`() {
            assertInvalid(
                PopulationWithNumbers(cultures = NumberDistribution(mapOf(UNKNOWN_CULTURE_ID to 100))),
                "Requires unknown Culture 99!",
            )
        }

        @Test
        fun `With an unknown race`() {
            assertInvalid(
                PopulationWithNumbers(NumberDistribution(mapOf(UNKNOWN_RACE_ID to 100))),
                "Requires unknown Race 99!",
            )
        }

        @Test
        fun `With an unknown standard of living`() {
            assertInvalid(
                PopulationWithNumbers(income = invalidIncome),
                "Requires unknown Standard Of Living 99!",
            )
        }

        @Test
        fun `A valid population`() {
            assertValid(valid)
        }

        @Test
        fun `Calculate the total population`() {
            assertEquals(200, valid.calculateTotal())
        }

    }

    @Nested
    inner class PopulationWithPercentagesTest {

        @Test
        fun `The total population must be greater or equal 0`() {
            assertTotalPopulation(PopulationWithPercentages(invalidTotal, PercentageDistribution()))
        }

        @Test
        fun `With an unknown culture`() {
            assertInvalid(
                PopulationWithPercentages(total, cultures = PercentageDistribution(mapOf(UNKNOWN_CULTURE_ID to HALF))),
                "Requires unknown Culture 99!",
            )
        }

        @Test
        fun `With an unknown race`() {
            assertInvalid(
                PopulationWithPercentages(total, PercentageDistribution(mapOf(UNKNOWN_RACE_ID to HALF))),
                "Requires unknown Race 99!",
            )
        }

        @Test
        fun `With an unknown standard of living`() {
            assertInvalid(
                PopulationWithPercentages(total, income = invalidIncome),
                "Requires unknown Standard Of Living 99!",
            )
        }

        @Test
        fun `A valid population`() {
            val cultures = PercentageDistribution(mapOf(CULTURE_ID_0 to HALF))
            val races = PercentageDistribution(mapOf(RACE_ID_0 to HALF, RACE_ID_1 to HALF))

            assertValid(PopulationWithPercentages(total, races, cultures))
        }

    }

    @Nested
    inner class PopulationWithSetsTest {

        @Test
        fun `The total population must be greater or equal 0`() {
            assertTotalPopulation(PopulationWithSets(invalidTotal))
        }

        @Test
        fun `With an unknown culture`() {
            assertInvalid(
                PopulationWithSets(total, cultures = setOf(UNKNOWN_CULTURE_ID)),
                "Requires unknown Culture 99!",
            )
        }

        @Test
        fun `With an unknown race`() {
            assertInvalid(
                PopulationWithSets(total, setOf(UNKNOWN_RACE_ID)),
                "Requires unknown Race 99!",
            )
        }

        @Test
        fun `With an unknown standard of living`() {
            assertInvalid(
                PopulationWithSets(total, income = invalidIncome),
                "Requires unknown Standard Of Living 99!",
            )
        }

        @Test
        fun `A valid population`() {
            assertValid(PopulationWithSets(total, races = setOf(RACE_ID_0)))
        }

    }

    @Nested
    inner class PopulationUnitsWithNumbersTest {
        private val validUnit0 = PopulationUnit(100, RACE_ID_0, CULTURE_ID_0)
        private val validUnit1 = PopulationUnit(200, RACE_ID_1, CULTURE_ID_1)
        private val valid = PopulationUnitsWithNumbers(listOf(validUnit0, validUnit1), 20)

        @Test
        fun `With an unknown culture`() {
            assertInvalid(
                PopulationUnitsWithNumbers(PopulationUnit(100, RACE_ID_0, UNKNOWN_CULTURE_ID)),
                "1.unit requires unknown Culture 99!",
            )
        }

        @Test
        fun `With an unknown race`() {
            assertInvalid(
                PopulationUnit(100, UNKNOWN_RACE_ID, CULTURE_ID_0),
                "1.unit requires unknown Race 99!",
            )
        }

        @Test
        fun `With an unknown standard of living`() {
            assertInvalid(
                PopulationUnit(100, RACE_ID_0, CULTURE_ID_0, invalidIncome),
                "Requires unknown Standard Of Living 99!"
            )
        }

        @Test
        fun `With an negative undefined`() {
            assertInvalid(
                PopulationUnitsWithNumbers(validUnit0, -10),
                "Undefined population must not be negative!",
            )
        }

        @Test
        fun `A valid population`() {
            assertValid(valid)
        }

        @Test
        fun `Calculate the total population`() {
            assertEquals(320, valid.getTotal())
        }

        private fun assertInvalid(unit: PopulationUnit<Int>, text: String) {
            assertInvalid(PopulationUnitsWithNumbers(unit), text,)
        }

    }

    fun assertTotalPopulation(population: Population) =
        assertInvalid(population, "The total population must be >= 0!")

    private fun assertInvalid(population: Population, message: String) {
        assertIllegalArgument(message) {
            assertValid(population)
        }
    }

    private fun assertValid(population: Population) {
        validatePopulation(state, TotalPopulationType.entries, population)
    }

}