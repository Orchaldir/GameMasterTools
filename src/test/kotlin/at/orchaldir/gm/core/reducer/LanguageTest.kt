package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteLanguage
import at.orchaldir.gm.core.action.UpdateLanguage
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.item.periodical.Periodical
import at.orchaldir.gm.core.model.item.text.Text
import at.orchaldir.gm.core.model.language.*
import at.orchaldir.gm.core.model.util.CreatedByCharacter
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LanguageTest {

    @Nested
    inner class DeleteTest {

        @Test
        fun `Can delete an existing language`() {
            val state = State(Storage(Language(LANGUAGE_ID_0)))
            val action = DeleteLanguage(LANGUAGE_ID_0)

            assertEquals(0, REDUCER.invoke(state, action).first.getLanguageStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeleteLanguage(LANGUAGE_ID_0)

            assertIllegalArgument("Requires unknown Language 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot delete a language with children`() {
            val state = State(
                Storage(
                    listOf(
                        Language(LANGUAGE_ID_0),
                        Language(LANGUAGE_ID_1, origin = EvolvedLanguage(LANGUAGE_ID_0))
                    )
                )
            )
            val action = DeleteLanguage(LANGUAGE_ID_0)

            assertIllegalArgument("Cannot delete language 0 with children!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot delete a language known by a character`() {
            val character = Character(CHARACTER_ID_0, languages = mapOf(LANGUAGE_ID_0 to ComprehensionLevel.Native))
            val state = State(
                listOf(
                    Storage(character),
                    Storage(Culture(CULTURE_ID_0)),
                    Storage(Language(LANGUAGE_ID_0)),
                )
            )
            val action = DeleteLanguage(LANGUAGE_ID_0)

            assertIllegalArgument("Cannot delete language 0 that is known by characters!") {
                REDUCER.invoke(state, action)
            }
        }

        @Test
        fun `Cannot delete a language used by a periodical`() {
            val periodical = Periodical(PERIODICAL_ID_0, language = LANGUAGE_ID_1)
            val state = State(listOf(Storage(periodical), Storage(Language(LANGUAGE_ID_1))))
            val action = DeleteLanguage(LANGUAGE_ID_1)

            assertIllegalArgument("Cannot delete language 1 that is used by a periodical!") {
                REDUCER.invoke(
                    state,
                    action
                )
            }
        }

        @Test
        fun `Cannot delete a language used by a text`() {
            val text = Text(TEXT_ID_0, language = LANGUAGE_ID_1)
            val state = State(listOf(Storage(text), Storage(Language(LANGUAGE_ID_1))))
            val action = DeleteLanguage(LANGUAGE_ID_1)

            assertIllegalArgument("Cannot delete language 1 that is used by a text!") { REDUCER.invoke(state, action) }
        }
    }

    @Nested
    inner class UpdateTest {

        val STATE = State(
            listOf(
                Storage(CALENDAR0),
                Storage(Character(CHARACTER_ID_0)),
                Storage(Language(LANGUAGE_ID_0)),
            )
        )

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateLanguage(Language(LANGUAGE_ID_1))

            assertIllegalArgument("Requires unknown Language 1!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Inventor must exist`() {
            val origin = InventedLanguage(CreatedByCharacter(CHARACTER_ID_1), DAY0)
            val action = UpdateLanguage(Language(LANGUAGE_ID_0, origin = origin))

            assertIllegalArgument("Cannot use an unknown character 1 as Inventor!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Parent language must exist`() {
            val origin = EvolvedLanguage(LANGUAGE_ID_1)
            val action = UpdateLanguage(Language(LANGUAGE_ID_0, origin = origin))

            assertIllegalArgument("Cannot use an unknown parent language 1!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `A language cannot be its own parent`() {
            val action = UpdateLanguage(Language(LANGUAGE_ID_0, origin = EvolvedLanguage(LANGUAGE_ID_0)))

            assertIllegalArgument("A language cannot be its own parent!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Date is in the future`() {
            val origin = InventedLanguage(CreatedByCharacter(CHARACTER_ID_0), FUTURE_DAY_0)
            val action = UpdateLanguage(Language(LANGUAGE_ID_0, origin = origin))

            assertIllegalArgument("Date (Language) is in the future!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `The plane of a planar language must exist`() {
            val action = UpdateLanguage(Language(LANGUAGE_ID_0, origin = PlanarLanguage(UNKNOWN_PLANE_ID)))

            assertIllegalArgument("Requires unknown Plane 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Parent language exist`() {
            val state = STATE.updateStorage(Storage(listOf(Language(LANGUAGE_ID_0), Language(LANGUAGE_ID_1))))
            val language = Language(LANGUAGE_ID_0, origin = EvolvedLanguage(LANGUAGE_ID_1))
            val action = UpdateLanguage(language)

            assertEquals(language, REDUCER.invoke(state, action).first.getLanguageStorage().get(LANGUAGE_ID_0))
        }
    }

}