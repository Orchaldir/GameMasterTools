package at.orchaldir.gm.core.selector.culture

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterTemplate
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.language.ComprehensionLevel
import at.orchaldir.gm.core.model.culture.language.ComprehensionLevel.Native
import at.orchaldir.gm.core.model.culture.language.Language
import at.orchaldir.gm.core.model.culture.language.LanguageId
import at.orchaldir.gm.core.model.item.periodical.Periodical
import at.orchaldir.gm.core.model.item.text.Text
import at.orchaldir.gm.core.model.util.SomeOf
import at.orchaldir.gm.core.model.util.origin.CombinedElement
import at.orchaldir.gm.core.model.util.origin.EvolvedElement
import at.orchaldir.gm.core.model.world.plane.Plane
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LanguageTest {

    @Nested
    inner class CanDeleteTest {
        private val language = Language(LANGUAGE_ID_0)
        private val languages = mapOf(LANGUAGE_ID_0 to Native)
        private val state = State(
            listOf(
                Storage(language),
            )
        )

        @Test
        fun `Cannot delete parent of evolved language`() {
            val language1 = Language(LANGUAGE_ID_1, origin = EvolvedElement(LANGUAGE_ID_0.value))
            val newState = state.updateStorage(Storage(listOf(language, language1)))

            failCanDelete(newState, LANGUAGE_ID_1)
            assertCanDelete(newState, LANGUAGE_ID_1)
        }

        @Test
        fun `Cannot delete parent of combined language`() {
            val origin = CombinedElement(setOf(LANGUAGE_ID_0.value))
            val language1 = Language(LANGUAGE_ID_1, origin = origin)
            val newState = state.updateStorage(Storage(listOf(language, language1)))

            failCanDelete(newState, LANGUAGE_ID_1)
            assertCanDelete(newState, LANGUAGE_ID_1)
        }

        @Test
        fun `Cannot delete a language known by a character`() {
            val character = Character(CHARACTER_ID_0, languages = languages)
            val newState = state.updateStorage(Storage(character))

            failCanDelete(newState, CHARACTER_ID_0)
        }

        @Test
        fun `Cannot delete a language known by a character template`() {
            val template = CharacterTemplate(CHARACTER_TEMPLATE_ID_0, race = RACE_ID_0, languages = languages)
            val newState = state.updateStorage(Storage(template))

            failCanDelete(newState, CHARACTER_TEMPLATE_ID_0)
        }

        @Test
        fun `Cannot delete a language used by a culture`() {
            val culture = Culture(CULTURE_ID_0, languages = SomeOf(LANGUAGE_ID_0))
            val newState = state.updateStorage(Storage(culture))

            failCanDelete(newState, CULTURE_ID_0)
        }

        @Test
        fun `Cannot delete a language used by a periodical`() {
            val periodical = Periodical(PERIODICAL_ID_0, language = LANGUAGE_ID_0)
            val newState = state.updateStorage(Storage(periodical))

            failCanDelete(newState, PERIODICAL_ID_0)
        }

        @Test
        fun `Cannot delete a language used by a plane`() {
            val plane = Plane(PLANE_ID_0, languages = setOf(LANGUAGE_ID_0))
            val newState = state.updateStorage(Storage(plane))

            failCanDelete(newState, PLANE_ID_0)
        }

        @Test
        fun `Cannot delete a language used by a text`() {
            val text = Text(TEXT_ID_0, language = LANGUAGE_ID_0)
            val newState = state.updateStorage(Storage(text))

            failCanDelete(newState, TEXT_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(DeleteResult(LANGUAGE_ID_0).addId(blockingId), state.canDeleteLanguage(LANGUAGE_ID_0))
        }

        private fun assertCanDelete(state: State, language: LanguageId) {
            assertEquals(DeleteResult(language), state.canDeleteLanguage(language))
        }
    }

}