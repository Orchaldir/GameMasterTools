package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.NAME_LIST_ID0
import at.orchaldir.gm.core.action.DeleteNameList
import at.orchaldir.gm.core.action.UpdateNameList
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.culture.name.MononymConvention
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.name.NameList
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class NameListTest {

    private val A = Name.init("A")
    private val B = Name.init("B")
    private val NAME_LIST = NameList(NAME_LIST_ID0, names = listOf(A, B))
    private val STATE = State(Storage(NameList(NAME_LIST_ID0)))

    @Nested
    inner class DeleteTest {

        @Test
        fun `Can delete an existing id`() {
            val action = DeleteNameList(NAME_LIST_ID0)

            assertEquals(0, REDUCER.invoke(STATE, action).first.getNameListStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeleteNameList(NAME_LIST_ID0)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot delete, if used by a culture`() {
            val action = DeleteNameList(NAME_LIST_ID0)
            val state = STATE.updateStorage(
                Storage(
                    Culture(
                        CultureId(0), namingConvention = MononymConvention(NAME_LIST_ID0)
                    )
                )
            )

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateNameList(NameList(NAME_LIST_ID0))

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Update an existing id`() {
            val action = UpdateNameList(NAME_LIST)

            assertAction(action)
        }

        @Test
        fun `Sort the names`() {
            val action = UpdateNameList(NameList(NAME_LIST_ID0, names = listOf(B, A)))

            assertAction(action)
        }

        private fun assertAction(action: UpdateNameList) = assertAction(action, NAME_LIST)

        private fun assertAction(action: UpdateNameList, result: NameList) {
            assertEquals(Storage(result), REDUCER.invoke(STATE, action).first.getNameListStorage())
        }
    }

}