package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.language.CombinedLanguage
import at.orchaldir.gm.core.model.language.EvolvedLanguage
import at.orchaldir.gm.core.model.language.Language
import at.orchaldir.gm.core.model.language.LanguageId
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private val ID0 = LanguageId(0)
private val ID1 = LanguageId(1)

class LanguageTest {

    @Nested
    inner class CanDeleteTest {

        @Test
        fun `Can delete unconnected language`() {
            val state = State(languages = Storage(listOf(Language(ID0), Language(ID1))))

            assertTrue(state.canDelete(ID0))
            assertTrue(state.canDelete(ID1))
        }

        @Test
        fun `Cannot delete parent of evolved language`() {
            val state = State(languages = Storage(listOf(Language(ID0), Language(ID1, origin = EvolvedLanguage(ID0)))))

            assertFalse(state.canDelete(ID0))
            assertTrue(state.canDelete(ID1))
        }

        @Test
        fun `Cannot delete parent of combined language`() {
            val state =
                State(languages = Storage(listOf(Language(ID0), Language(ID1, origin = CombinedLanguage(setOf(ID0))))))

            assertFalse(state.canDelete(ID0))
            assertTrue(state.canDelete(ID1))
        }
    }

}