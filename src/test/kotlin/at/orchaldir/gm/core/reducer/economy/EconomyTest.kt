package at.orchaldir.gm.core.reducer.economy

import at.orchaldir.gm.BUSINESS_TEMPLATE_ID_0
import at.orchaldir.gm.UNKNOWN_BUSINESS_TEMPLATE_ID
import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.Economy
import at.orchaldir.gm.core.model.economy.EconomyWithPercentages
import at.orchaldir.gm.core.model.economy.business.BusinessTemplate
import at.orchaldir.gm.core.model.economy.CommonBusinesses
import at.orchaldir.gm.core.model.economy.EconomyWithNumbers
import at.orchaldir.gm.core.model.util.NumberDistribution
import at.orchaldir.gm.core.model.util.PercentageDistribution
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.HALF
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class EconomyTest {
    private val state = State(
         Storage(BusinessTemplate(BUSINESS_TEMPLATE_ID_0)),
    )

    @Nested
    inner class CommonBusinessesTest {

        @Test
        fun `With an unknown culture`() {
            assertEconomy(
                CommonBusinesses(setOf(UNKNOWN_BUSINESS_TEMPLATE_ID)),
                "Requires unknown Business Template 99!",
            )
        }

        @Test
        fun `A valid population`() {
            validateEconomy(state, CommonBusinesses( setOf(BUSINESS_TEMPLATE_ID_0)))
        }

    }

    @Nested
    inner class EconomyWithNumbersTest {

        @Test
        fun `With an unknown culture`() {
            assertEconomy(
                EconomyWithNumbers(NumberDistribution(mapOf(UNKNOWN_BUSINESS_TEMPLATE_ID to 100))),
                "Requires unknown Business Template 99!",
            )
        }

        @Test
        fun `A valid population`() {
            val businesses = NumberDistribution(mapOf(BUSINESS_TEMPLATE_ID_0 to 100))

            validateEconomy(state, EconomyWithNumbers(businesses))
        }

    }

    @Nested
    inner class EconomyWithPercentagesTest {

        @Test
        fun `The total population must be greater or equal 0`() {
            assertTotalEconomy(EconomyWithPercentages(-1, PercentageDistribution()))
        }

        @Test
        fun `With an unknown culture`() {
            val distribution = PercentageDistribution(mapOf(UNKNOWN_BUSINESS_TEMPLATE_ID to HALF))

            assertEconomy(
                EconomyWithPercentages(100, distribution),
                "Requires unknown Business Template 99!",
            )
        }

        @Test
        fun `A valid population`() {
            val businesses = PercentageDistribution(mapOf(BUSINESS_TEMPLATE_ID_0 to HALF))
            val distribution = EconomyWithPercentages(100, businesses)

            validateEconomy(state, distribution)
        }

    }

    private fun assertTotalEconomy(population: Economy) =
        assertEconomy(population, "The total business number must be >= 0!")

    private fun assertEconomy(population: Economy, message: String) {
        assertIllegalArgument(message) {
            validateEconomy(state, population)
        }
    }

}