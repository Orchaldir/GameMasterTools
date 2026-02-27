package at.orchaldir.gm.core.reducer.realm.population

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.SettlementSize
import at.orchaldir.gm.core.model.realm.population.*
import at.orchaldir.gm.core.reducer.realm.validateTotalPopulation
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

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
            assertValid(TotalPopulationAsNumber(0))
        }

        @Test
        fun `A positive number is valid`() {
            assertValid(TotalPopulationAsNumber(1))
        }
    }

    @Nested
    inner class TotalPopulationAsSettlementSizeTest {
        @Test
        fun `An unknown size is invalid`() {
            assertInvalid(TotalPopulationAsSettlementSize(UNKNOWN_SETTLEMENT_SIZE_ID), "Requires unknown Settlement Size 99!")
        }

        @Test
        fun `A known size is valid`() {
            assertValid(TotalPopulationAsSettlementSize(SETTLEMENT_SIZE_ID_0))
        }
    }


    private fun assertTotalPopulation(total: TotalPopulation) =
        assertInvalid(total, "The total population must be >= 0!")

    private fun assertInvalid(total: TotalPopulation, message: String) {
        assertIllegalArgument(message) {
            assertValid(total)
        }
    }

    private fun assertValid(total: TotalPopulation) {
        validateTotalPopulation(state, TotalPopulationType.entries, total)
    }

}