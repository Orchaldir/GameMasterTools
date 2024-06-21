package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.language.*
import at.orchaldir.gm.core.model.language.ComprehensionLevel.Native
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private val ID0 = LanguageId(0)
private val ID1 = LanguageId(1)
private val CHARACTER0 = CharacterId(0)

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

        @Test
        fun `Cannot delete language used by character`() {
            val state = State(
                characters = Storage(listOf(Character(CHARACTER0, languages = mapOf(ID0 to Native)))),
                languages = Storage(listOf(Language(ID0), Language(ID1)))
            )

            assertFalse(state.canDelete(ID0))
            assertTrue(state.canDelete(ID1))
        }
    }

}