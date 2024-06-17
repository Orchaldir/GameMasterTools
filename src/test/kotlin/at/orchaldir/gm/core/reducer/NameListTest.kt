package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.DeleteLanguage
import at.orchaldir.gm.core.action.UpdateNameList
import at.orchaldir.gm.core.model.NameList
import at.orchaldir.gm.core.model.NameListId
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.language.Language
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private val ID0 = NameListId(0)
private val NAME_LIST = NameList(ID0, "Test", listOf("A", "B"))

class NameListTest {

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateNameList(NameList(ID0))

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Update an existing id`() {
            val state = State(nameLists = Storage(listOf(NameList(ID0))))
            val action = UpdateNameList(NAME_LIST)

            assertAction(state, action)
        }

        @Test
        fun `Sort the names`() {
            val state = State(nameLists = Storage(listOf(NameList(ID0))))
            val action = UpdateNameList(NameList(ID0, "Test", listOf("B", "A")))

            assertAction(state, action)
        }

        @Test
        fun `Trim the names`() {
            val state = State(nameLists = Storage(listOf(NameList(ID0))))
            val action = UpdateNameList(NameList(ID0, "Test", listOf("  A", "B  ")))

            assertAction(state, action)
        }

        @Test
        fun `Filter empty names`() {
            val state = State(nameLists = Storage(listOf(NameList(ID0))))
            val action = UpdateNameList(NameList(ID0, "Test", listOf("A", "  ", "B")))

            assertAction(state, action)
        }

        private fun assertAction(state: State, action: UpdateNameList) {
            assertEquals(Storage(listOf(NAME_LIST)), REDUCER.invoke(state, action).first.nameLists)
        }
    }

}