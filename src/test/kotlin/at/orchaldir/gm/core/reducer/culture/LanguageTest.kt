package at.orchaldir.gm.core.reducer.culture

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteLanguage
import at.orchaldir.gm.core.action.UpdateLanguage
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.language.ComprehensionLevel
import at.orchaldir.gm.core.model.culture.language.Language
import at.orchaldir.gm.core.model.item.periodical.Periodical
import at.orchaldir.gm.core.model.item.text.Text
import at.orchaldir.gm.core.model.util.CharacterReference
import at.orchaldir.gm.core.model.util.origin.CreatedElement
import at.orchaldir.gm.core.model.util.origin.EvolvedElement
import at.orchaldir.gm.core.model.world.plane.Plane
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LanguageTest {

    private val state = State(
        listOf(
            Storage(CALENDAR0),
            Storage(Character(CHARACTER_ID_0)),
            Storage(Culture(CULTURE_ID_0)),
            Storage(Language(LANGUAGE_ID_0)),
        )
    )

    @Nested
    inner class DeleteTest {
        val action = DeleteLanguage(LANGUAGE_ID_0)

        @Test
        fun `Can delete an existing language`() {
            assertEquals(0, REDUCER.invoke(state, action).first.getLanguageStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            assertIllegalArgument("Requires unknown Language 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot delete a language with children`() {
            val state = state.updateStorage(
                Storage(
                    listOf(
                        Language(LANGUAGE_ID_0),
                        Language(LANGUAGE_ID_1, origin = EvolvedElement(LANGUAGE_ID_0.value))
                    )
                )
            )

            assertIllegalArgument("Cannot delete Language 0, because it has children!") {
                REDUCER.invoke(
                    state,
                    action
                )
            }
        }

        @Test
        fun `Cannot delete a language known by a character`() {
            val character = Character(CHARACTER_ID_0, languages = mapOf(LANGUAGE_ID_0 to ComprehensionLevel.Native))
            val state = state.updateStorage(Storage(character))

            assertIllegalArgument("Cannot delete Language 0, because it is known by characters!") {
                REDUCER.invoke(state, action)
            }
        }

        @Test
        fun `Cannot delete a language used by a periodical`() {
            val periodical = Periodical(PERIODICAL_ID_0, language = LANGUAGE_ID_0)
            val state = state.updateStorage(Storage(periodical))

            assertIllegalArgument("Cannot delete Language 0, because it is used by a periodical!") {
                REDUCER.invoke(state, action)
            }
        }

        @Test
        fun `Cannot delete a language used by a plane`() {
            val plane = Plane(PLANE_ID_0, languages = setOf(LANGUAGE_ID_0))
            val state = state.updateStorage(Storage(plane))

            assertIllegalArgument("Cannot delete Language 0, because it is used by a plane!") {
                REDUCER.invoke(state, action)
            }
        }

        @Test
        fun `Cannot delete a language used by a text`() {
            val text = Text(TEXT_ID_0, language = LANGUAGE_ID_0)
            val state = state.updateStorage(Storage(text))

            assertIllegalArgument("Cannot delete Language 0, because it is used by a text!") {
                REDUCER.invoke(
                    state,
                    action
                )
            }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateLanguage(Language(LANGUAGE_ID_1))

            assertIllegalArgument("Requires unknown Language 1!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Inventor must exist`() {
            val origin = CreatedElement(CharacterReference(UNKNOWN_CHARACTER_ID))
            val action = UpdateLanguage(Language(LANGUAGE_ID_0, date = DAY0, origin = origin))

            assertIllegalArgument("Requires unknown Creator (Character 99)!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Parent language must exist`() {
            val origin = EvolvedElement(UNKNOWN_LANGUAGE_ID)
            val action = UpdateLanguage(Language(LANGUAGE_ID_0, origin = origin))

            assertIllegalArgument("Requires unknown parent Language 99!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `A language cannot be its own parent`() {
            val action = UpdateLanguage(Language(LANGUAGE_ID_0, origin = EvolvedElement(LANGUAGE_ID_0)))

            assertIllegalArgument("Language 0 cannot be its own parent!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Date is in the future`() {
            val origin = CreatedElement(CharacterReference(CHARACTER_ID_0))
            val action = UpdateLanguage(Language(LANGUAGE_ID_0, date = FUTURE_DAY_0, origin = origin))

            assertIllegalArgument("Date (Language) is in the future!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Parent language exist`() {
            val state = state.updateStorage(Storage(listOf(Language(LANGUAGE_ID_0), Language(LANGUAGE_ID_1))))
            val language = Language(LANGUAGE_ID_0, origin = EvolvedElement(LANGUAGE_ID_1))
            val action = UpdateLanguage(language)

            assertEquals(language, REDUCER.invoke(state, action).first.getLanguageStorage().get(LANGUAGE_ID_0))
        }
    }

}