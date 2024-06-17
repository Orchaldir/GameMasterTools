package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.DeleteNameList
import at.orchaldir.gm.core.action.UpdateNameList
import at.orchaldir.gm.core.model.NameList
import at.orchaldir.gm.core.model.NameListId
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.appearance.GenderMap
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.culture.name.MononymConvention
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private val ID0 = NameListId(0)
private val NAME_LIST = NameList(ID0, "Test", listOf("A", "B"))
private val STATE = State(nameLists = Storage(listOf(NameList(ID0))))

class NameListTest {

    @Nested
    inner class DeleteTest {

        @Test
        fun `Can delete an existing language`() {
            val action = DeleteNameList(ID0)

            assertEquals(0, REDUCER.invoke(STATE, action).first.languages.getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeleteNameList(ID0)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot delete, if used by a culture`() {
            val action = DeleteNameList(ID0)
            val state = STATE.copy(
                cultures = Storage(
                    listOf(
                        Culture(
                            CultureId(0), namingConvention = MononymConvention(
                                GenderMap(ID0, ID0, ID0)
                            )
                        )
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
            val action = UpdateNameList(NameList(ID0))

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Update an existing id`() {
            val action = UpdateNameList(NAME_LIST)

            assertAction(action)
        }

        @Test
        fun `Sort the names`() {
            val action = UpdateNameList(NameList(ID0, "Test", listOf("B", "A")))

            assertAction(action)
        }

        @Test
        fun `Trim the names`() {
            val action = UpdateNameList(NameList(ID0, "Test", listOf("  A", "B  ")))

            assertAction(action)
        }

        @Test
        fun `Filter empty names`() {
            val action = UpdateNameList(NameList(ID0, "Test", listOf("A", "  ", "B")))

            assertAction(action)
        }

        private fun assertAction(action: UpdateNameList) {
            assertEquals(Storage(listOf(NAME_LIST)), REDUCER.invoke(STATE, action).first.nameLists)
        }
    }

}