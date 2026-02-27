package at.orchaldir.gm.core.reducer.realm.population

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.economy.job.AffordableStandardOfLiving
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.realm.SettlementSize
import at.orchaldir.gm.core.model.realm.population.*
import at.orchaldir.gm.core.model.util.NumberDistribution
import at.orchaldir.gm.core.model.util.PercentageDistribution
import at.orchaldir.gm.core.reducer.realm.validatePopulation
import at.orchaldir.gm.core.reducer.realm.validateTotalPopulation
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.HALF
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TotalPopulationTest {
    private val state = State(Storage(SettlementSize(SETTLEMENT_SIZE_ID_0)))

    @Nested
    inner class TotalPopulationAsNumberTest {
        @Test
        fun `A negative number is invalid`() {
            assertTotalPopulation(TotalPopulationAsNumber(-1))
        }

        @Test
        fun `Zero is valid`() {
            validateTotalPopulation(state, TotalPopulationAsNumber(0))
        }

        @Test
        fun `A positive number is valid`() {
            validateTotalPopulation(state, TotalPopulationAsNumber(1))
        }
    }

    @Nested
    inner class TotalPopulationAsSettlementSizeTest {
        @Test
        fun `An unknown size is invalid`() {
            assertTotalPopulation(TotalPopulationAsSettlementSize(UNKNOWN_SETTLEMENT_SIZE_ID), "Requires unknown Settlement Size 99!")
        }

        @Test
        fun `A known size is valid`() {
            validateTotalPopulation(state, TotalPopulationAsSettlementSize(SETTLEMENT_SIZE_ID_0))
        }
    }


    private fun assertTotalPopulation(total: TotalPopulation) =
        assertTotalPopulation(total, "The total population must be >= 0!")

    private fun assertTotalPopulation(total: TotalPopulation, message: String) {
        assertIllegalArgument(message) {
            validateTotalPopulation(state, total)
        }
    }

}