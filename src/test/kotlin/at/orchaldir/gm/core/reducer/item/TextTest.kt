package at.orchaldir.gm.core.reducer.item

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteText
import at.orchaldir.gm.core.action.UpdateText
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.item.text.*
import at.orchaldir.gm.core.model.item.text.book.ComplexSewingPattern
import at.orchaldir.gm.core.model.item.text.book.CopticBinding
import at.orchaldir.gm.core.model.item.text.book.Hardcover
import at.orchaldir.gm.core.model.item.text.book.SimpleSewingPattern
import at.orchaldir.gm.core.model.item.text.scroll.ScrollHandle
import at.orchaldir.gm.core.model.item.text.scroll.ScrollWithOneRod
import at.orchaldir.gm.core.model.language.Language
import at.orchaldir.gm.core.model.util.CreatedByCharacter
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

private val STATE = State(
    listOf(
        Storage(listOf(Text(TEXT_ID_0), Text(TEXT_ID_1, date = DAY1))),
        Storage(CALENDAR0),
        Storage(Character(CHARACTER_ID_0)),
        Storage(Language(LANGUAGE_ID_0)),
    )
)

class TextTest {

    @Nested
    inner class DeleteTest {
        val action = DeleteText(TEXT_ID_0)

        @Test
        fun `Can delete an existing text`() {
            assertFalse(REDUCER.invoke(STATE, action).first.getTextStorage().contains(TEXT_ID_0))
        }

        @Test
        fun `Cannot delete unknown id`() {
            assertIllegalArgument("Requires unknown Text 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot delete a translated text`() {
            val origin = TranslatedText(TEXT_ID_0, CreatedByCharacter(CHARACTER_ID_0))
            val state = STATE.updateStorage(
                Storage(listOf(Text(TEXT_ID_0), Text(TEXT_ID_1, origin = origin)))
            )

            assertIllegalArgument("The text 0 is used") {
                REDUCER.invoke(state, action)
            }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateText(Text(TEXT_ID_0))

            assertIllegalArgument("Requires unknown Text 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Author must exist`() {
            val origin = OriginalText(CreatedByCharacter(CHARACTER_ID_1))
            val action = UpdateText(Text(TEXT_ID_0, origin = origin))

            assertIllegalArgument("Cannot use an unknown character 1 as Author!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Successfully update an original text`() {
            val origin = OriginalText(CreatedByCharacter(CHARACTER_ID_0))
            val text = Text(TEXT_ID_0, origin = origin)
            val action = UpdateText(text)

            assertEquals(text, REDUCER.invoke(STATE, action).first.getTextStorage().get(TEXT_ID_0))
        }

        @Test
        fun `Translator must exist`() {
            val origin = TranslatedText(TEXT_ID_1, CreatedByCharacter(CHARACTER_ID_1))
            val action = UpdateText(Text(TEXT_ID_0, origin = origin))

            assertIllegalArgument("Cannot use an unknown character 1 as Translator!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Translated text must exist`() {
            val origin = TranslatedText(TEXT_ID_2, CreatedByCharacter(CHARACTER_ID_0))
            val action = UpdateText(Text(TEXT_ID_0, origin = origin))

            assertIllegalArgument("Requires unknown Text 2!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `A text cannot translate itself`() {
            val origin = TranslatedText(TEXT_ID_0, CreatedByCharacter(CHARACTER_ID_0))
            val action = UpdateText(Text(TEXT_ID_0, origin = origin))

            assertIllegalArgument("The text cannot translate itself!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `The translation must happen after the original was written`() {
            val origin = TranslatedText(TEXT_ID_1, CreatedByCharacter(CHARACTER_ID_0))
            val action = UpdateText(Text(TEXT_ID_0, date = DAY0, origin = origin))

            assertIllegalArgument("The translation must happen after the original was written!") {
                REDUCER.invoke(STATE, action)
            }
        }

        @Test
        fun `Successfully update a translated text`() {
            val origin = TranslatedText(TEXT_ID_1, CreatedByCharacter(CHARACTER_ID_0))
            val text = Text(TEXT_ID_0, origin = origin)
            val action = UpdateText(text)

            assertEquals(text, REDUCER.invoke(STATE, action).first.getTextStorage().get(TEXT_ID_0))
        }

        @Nested
        inner class FormatTest {
            @Test
            fun `Too few pages`() {
                val action = UpdateText(Text(TEXT_ID_0, format = Book(2, Hardcover())))

                assertIllegalArgument("The text requires at least 10 pages!") { REDUCER.invoke(STATE, action) }
            }

            @Test
            fun `Too few stitches for the simple pattern`() {
                val pattern = SimpleSewingPattern(stitches = emptyList())
                val binding = CopticBinding(sewingPattern = pattern)
                val action = UpdateText(Text(TEXT_ID_0, format = Book(100, binding)))

                assertIllegalArgument("The sewing pattern requires at least 2 stitches!") {
                    REDUCER.invoke(
                        STATE,
                        action
                    )
                }
            }

            @Test
            fun `Too few stitches for the complex pattern`() {
                val pattern = ComplexSewingPattern(stitches = emptyList())
                val binding = CopticBinding(sewingPattern = pattern)
                val action = UpdateText(Text(TEXT_ID_0, format = Book(100, binding)))

                assertIllegalArgument("The sewing pattern requires at least 2 stitches!") {
                    REDUCER.invoke(
                        STATE,
                        action
                    )
                }
            }

            @Test
            fun `Too few scroll handle segments`() {
                val format = ScrollWithOneRod(ScrollHandle(emptyList()))
                val action = UpdateText(Text(TEXT_ID_0, format = Scroll(format)))

                assertIllegalArgument("A scroll handle needs at least 1 segment!") { REDUCER.invoke(STATE, action) }
            }
        }
    }

}