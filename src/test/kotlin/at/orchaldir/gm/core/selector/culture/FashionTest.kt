package at.orchaldir.gm.core.selector.culture

import at.orchaldir.gm.CULTURE_ID_0
import at.orchaldir.gm.FASHION_ID_0
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.fashion.Fashion
import at.orchaldir.gm.core.model.util.GenderMap
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class FashionTest {

    @Nested
    inner class CanDeleteTest {
        private val fashion = Fashion(FASHION_ID_0)
        private val state = State(
            listOf(
                Storage(fashion),
            )
        )

        @Test
        fun `Cannot delete a fashion used by a culture`() {
            val culture = Culture(CULTURE_ID_0, fashion = GenderMap(FASHION_ID_0))
            val newState = state.updateStorage(Storage(culture))

            assertCanDelete(newState, CULTURE_ID_0)
        }

        private fun <ID : Id<ID>> assertCanDelete(state: State, blockingId: ID) {
            assertEquals(DeleteResult(FASHION_ID_0).addId(blockingId), state.canDeleteFashion(FASHION_ID_0))
        }
    }
  
}