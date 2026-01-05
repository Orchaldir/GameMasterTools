package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.CULTURE_ID_0
import at.orchaldir.gm.NAME_LIST_ID0
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.name.MononymConvention
import at.orchaldir.gm.core.model.util.name.NameList
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class NameListTest {

    @Nested
    inner class CanDeleteTest {
        private val list = NameList(NAME_LIST_ID0)
        private val state = State(
            listOf(
                Storage(list),
            )
        )

        @Test
        fun `Cannot delete a name list, if used by a culture`() {
            val culture = Culture(CULTURE_ID_0, namingConvention = MononymConvention(NAME_LIST_ID0))
            val newState = state.updateStorage(culture)

            failCanDelete(newState, CULTURE_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(DeleteResult(NAME_LIST_ID0).addId(blockingId), state.canDeleteNameList(NAME_LIST_ID0))
        }
    }

}