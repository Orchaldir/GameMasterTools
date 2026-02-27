package at.orchaldir.gm.core.selector.settlement

import at.orchaldir.gm.SETTLEMENT_ID_0
import at.orchaldir.gm.SETTLEMENT_SIZE_ID_0
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.Settlement
import at.orchaldir.gm.core.model.realm.SettlementSize
import at.orchaldir.gm.core.model.realm.population.PopulationWithSets
import at.orchaldir.gm.core.model.realm.population.TotalPopulationAsSettlementSize
import at.orchaldir.gm.core.selector.realm.canDeleteSettlementSize
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SettlementSizeTest {

    @Nested
    inner class CanDeleteTest {
        private val size = SettlementSize(SETTLEMENT_SIZE_ID_0)
        private val state = State(
            listOf(
                Storage(size),
            )
        )

        @Test
        fun `Cannot delete a settlement size used by a settlement`() {
            val total = TotalPopulationAsSettlementSize(SETTLEMENT_SIZE_ID_0)
            val settlement = Settlement(SETTLEMENT_ID_0, population = PopulationWithSets(total))
            val newState = state.updateStorage(settlement)

            failCanDelete(newState, SETTLEMENT_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(DeleteResult(SETTLEMENT_SIZE_ID_0).addId(blockingId), state.canDeleteSettlementSize(SETTLEMENT_SIZE_ID_0))
        }
    }

}