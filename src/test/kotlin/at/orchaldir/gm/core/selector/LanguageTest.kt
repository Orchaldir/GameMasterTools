package at.orchaldir.gm.core.selector

import at.orchaldir.gm.CULTURE_ID_0
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.culture.language.CombinedLanguage
import at.orchaldir.gm.core.model.culture.language.ComprehensionLevel.Native
import at.orchaldir.gm.core.model.culture.language.EvolvedLanguage
import at.orchaldir.gm.core.model.culture.language.Language
import at.orchaldir.gm.core.model.culture.language.LanguageId
import at.orchaldir.gm.core.model.util.SomeOf
import at.orchaldir.gm.core.selector.culture.canDeleteLanguage
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private val ID0 = LanguageId(0)
private val ID1 = LanguageId(1)
private val CHARACTER0 = CharacterId(0)
private val CULTURE0 = CultureId(0)

class LanguageTest {

    @Nested
    inner class CanDeleteTest {

        @Test
        fun `Can delete unconnected language`() {
            val state = State(Storage(listOf(Language(ID0), Language(ID1))))

            assertTrue(state.canDeleteLanguage(ID0))
            assertTrue(state.canDeleteLanguage(ID1))
        }

        @Test
        fun `Cannot delete parent of evolved language`() {
            val state = State(Storage(listOf(Language(ID0), Language(ID1, origin = EvolvedLanguage(ID0)))))

            assertFalse(state.canDeleteLanguage(ID0))
            assertTrue(state.canDeleteLanguage(ID1))
        }

        @Test
        fun `Cannot delete parent of combined language`() {
            val state = State(Storage(listOf(Language(ID0), Language(ID1, origin = CombinedLanguage(setOf(ID0))))))

            assertFalse(state.canDeleteLanguage(ID0))
            assertTrue(state.canDeleteLanguage(ID1))
        }

        @Test
        fun `Cannot delete language used by character`() {
            val state = State(
                listOf(
                    Storage(Character(CHARACTER0, languages = mapOf(ID0 to Native))),
                    Storage(Culture(CULTURE_ID_0)),
                    Storage(listOf(Language(ID0), Language(ID1))),
                )
            )

            assertFalse(state.canDeleteLanguage(ID0))
            assertTrue(state.canDeleteLanguage(ID1))
        }

        @Test
        fun `Cannot delete language used by culture`() {
            val state = State(
                listOf(
                    Storage(Culture(CULTURE0, languages = SomeOf(setOf(ID0)))),
                    Storage(listOf(Language(ID0), Language(ID1))),
                )
            )

            assertFalse(state.canDeleteLanguage(ID0))
            assertTrue(state.canDeleteLanguage(ID1))
        }
    }

}