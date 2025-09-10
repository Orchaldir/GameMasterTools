package at.orchaldir.gm.core.selector.item

import at.orchaldir.gm.CHARACTER_ID_0
import at.orchaldir.gm.TEXT_ID_0
import at.orchaldir.gm.TEXT_ID_1
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.text.Text
import at.orchaldir.gm.core.model.util.UndefinedReference
import at.orchaldir.gm.core.model.util.origin.TranslatedElement
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TextTest {

    @Nested
    inner class CanDeleteTest {
        private val text = Text(TEXT_ID_0)
        private val state = State(
            listOf(
                Storage(text),
            )
        )

        @Test
        fun `Cannot delete a translated text`() {
            val origin = TranslatedElement(TEXT_ID_0, UndefinedReference)
            val text1 = Text(TEXT_ID_1, origin = origin)
            val newState = state.updateStorage(Storage(listOf(text, text1)))

            assertCanDelete(newState, TEXT_ID_1)
        }

        private fun <ID : Id<ID>> assertCanDelete(state: State, blockingId: ID) {
            assertEquals(
                DeleteResult(TEXT_ID_0).addId(blockingId),
                state.canDeleteText(TEXT_ID_0)
            )
        }
    }

}