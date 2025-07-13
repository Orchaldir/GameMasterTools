package at.orchaldir.gm.core.reducer.item

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteText
import at.orchaldir.gm.core.action.UpdateText
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.culture.language.Language
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.item.text.Book
import at.orchaldir.gm.core.model.item.text.Scroll
import at.orchaldir.gm.core.model.item.text.Text
import at.orchaldir.gm.core.model.item.text.book.ComplexSewingPattern
import at.orchaldir.gm.core.model.item.text.book.CopticBinding
import at.orchaldir.gm.core.model.item.text.book.Hardcover
import at.orchaldir.gm.core.model.item.text.book.SimpleSewingPattern
import at.orchaldir.gm.core.model.item.text.content.*
import at.orchaldir.gm.core.model.item.text.scroll.ScrollWithOneRod
import at.orchaldir.gm.core.model.magic.Spell
import at.orchaldir.gm.core.model.util.CreatedByCharacter
import at.orchaldir.gm.core.model.util.font.SolidFont
import at.orchaldir.gm.core.model.util.origin.CreatedElement
import at.orchaldir.gm.core.model.util.origin.TranslatedElement
import at.orchaldir.gm.core.model.util.part.Segments
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMillimeters
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class TextTest {

    private val STATE = State(
        listOf(
            Storage(listOf(Text(TEXT_ID_0), Text(TEXT_ID_1, date = DAY1))),
            Storage(CALENDAR0),
            Storage(Business(BUSINESS_ID_0, startDate = DAY2)),
            Storage(Character(CHARACTER_ID_0, birthDate = DAY0)),
            Storage(Language(LANGUAGE_ID_0)),
            Storage(Spell(SPELL_ID_0)),
        )
    )
    val unknownFont = SolidFont(fromMillimeters(2), font = UNKNOWN_FONT_ID)

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
            val origin = TranslatedElement(TEXT_ID_0, CreatedByCharacter(CHARACTER_ID_0))
            val state = STATE.updateStorage(
                Storage(listOf(Text(TEXT_ID_0), Text(TEXT_ID_1, origin = origin)))
            )

            assertIllegalArgument("Cannot delete Text 0, because it is used!") {
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
        fun `Date is in the future`() {
            val action = UpdateText(Text(TEXT_ID_0, date = FUTURE_DAY_0))

            assertIllegalArgument("Date (Text) is in the future!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Author must exist`() {
            val origin = CreatedElement(CreatedByCharacter(UNKNOWN_CHARACTER_ID))
            val action = UpdateText(Text(TEXT_ID_0, origin = origin))

            assertIllegalArgument("Cannot use an unknown Character 99 as Creator!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Publisher must exist`() {
            val action = UpdateText(Text(TEXT_ID_0, publisher = UNKNOWN_BUSINESS_ID))

            assertIllegalArgument("Requires unknown Business 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Publisher must exist at the time of publishing`() {
            val action = UpdateText(Text(TEXT_ID_0, publisher = BUSINESS_ID_0, date = DAY1))

            assertIllegalArgument("The Business 0 doesn't exist at the required date!") {
                REDUCER.invoke(
                    STATE,
                    action
                )
            }
        }

        @Test
        fun `Successfully update an original text`() {
            val origin = CreatedElement(CreatedByCharacter(CHARACTER_ID_0))
            val text = Text(TEXT_ID_0, origin = origin)
            val action = UpdateText(text)

            assertEquals(text, REDUCER.invoke(STATE, action).first.getTextStorage().get(TEXT_ID_0))
        }

        @Test
        fun `Translator must exist`() {
            val origin = TranslatedElement(TEXT_ID_1, CreatedByCharacter(UNKNOWN_CHARACTER_ID))
            val action = UpdateText(Text(TEXT_ID_0, origin = origin))

            assertIllegalArgument("Cannot use an unknown Character 99 as Creator!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Translated text must exist`() {
            val origin = TranslatedElement(UNKNOWN_TEXT_ID, CreatedByCharacter(CHARACTER_ID_0))
            val action = UpdateText(Text(TEXT_ID_0, origin = origin))

            assertIllegalArgument("Requires unknown parent Text 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `A text cannot translate itself`() {
            val origin = TranslatedElement(TEXT_ID_0, CreatedByCharacter(CHARACTER_ID_0))
            val action = UpdateText(Text(TEXT_ID_0, origin = origin))

            assertIllegalArgument("Text 0 cannot be its own parent!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `The translation must happen after the original was written`() {
            val origin = TranslatedElement(TEXT_ID_1, CreatedByCharacter(CHARACTER_ID_0))
            val action = UpdateText(Text(TEXT_ID_0, date = DAY0, origin = origin))

            assertIllegalArgument("The parent Text 1 doesn't exist at the required date!") {
                REDUCER.invoke(STATE, action)
            }
        }

        @Test
        fun `Successfully update a translated text`() {
            val origin = TranslatedElement(TEXT_ID_1, CreatedByCharacter(CHARACTER_ID_0))
            val text = Text(TEXT_ID_0, origin = origin)
            val action = UpdateText(text)

            assertEquals(text, REDUCER.invoke(STATE, action).first.getTextStorage().get(TEXT_ID_0))
        }

        @Nested
        inner class FormatTest {
            @Test
            fun `Too few pages`() {
                val action = UpdateText(Text(TEXT_ID_0, format = Book(Hardcover(), 2)))

                assertIllegalArgument("The text requires at least 10 pages!") { REDUCER.invoke(STATE, action) }
            }

            @Test
            fun `Too few stitches for the simple pattern`() {
                val pattern = SimpleSewingPattern(stitches = emptyList())
                val binding = CopticBinding(sewingPattern = pattern)
                val action = UpdateText(Text(TEXT_ID_0, format = Book(binding, 100)))

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
                val action = UpdateText(Text(TEXT_ID_0, format = Book(binding, 100)))

                assertIllegalArgument("The sewing pattern requires at least 2 stitches!") {
                    REDUCER.invoke(
                        STATE,
                        action
                    )
                }
            }

            @Test
            fun `Too few scroll handle segments`() {
                val format = ScrollWithOneRod(Segments(emptyList()))
                val action = UpdateText(Text(TEXT_ID_0, format = Scroll(format)))

                assertIllegalArgument("A scroll handle needs at least 1 segment!") { REDUCER.invoke(STATE, action) }
            }
        }

        @Nested
        inner class ContentTest {
            @Test
            fun `Too few pages`() {
                val content = AbstractText(AbstractContent(0))
                val action = UpdateText(Text(TEXT_ID_0, content = content))

                assertIllegalArgument("The abstract text requires at least 1 pages!") { REDUCER.invoke(STATE, action) }
            }

            @Test
            fun `Unknown quote`() {
                val quote = LinkedQuote(UNKNOWN_QUOTE_ID)
                val chapter = SimpleChapter(0, listOf(quote))
                val content = SimpleChapters(listOf(chapter))
                val action = UpdateText(Text(TEXT_ID_0, content = content))

                assertIllegalArgument("Requires unknown Quote 99!") { REDUCER.invoke(STATE, action) }
            }

            @Test
            fun `Unknown main font`() {
                testUnknownFont(AbstractText(style = ContentStyle(unknownFont)))
            }

            @Test
            fun `Unknown title font`() {
                testUnknownFont(AbstractText(style = ContentStyle(title = unknownFont)))
            }

            @Test
            fun `Unknown page numbering font`() {
                testUnknownFont(AbstractText(pageNumbering = SimplePageNumbering(unknownFont)))
            }

            @Test
            fun `Unknown initials font`() {
                testUnknownFont(AbstractText(style = ContentStyle(initials = FontInitials(unknownFont))))
            }

            @Test
            fun `Unknown main font for table of contents`() {
                val content = AbstractChapters(
                    pageNumbering = PageNumberingReusingFont(),
                    tableOfContents = ComplexTableOfContents(mainOptions = unknownFont),
                )

                testUnknownFont(content)
            }

            @Test
            fun `Unknown title font for table of contents`() {
                val content = AbstractChapters(
                    pageNumbering = PageNumberingReusingFont(),
                    tableOfContents = ComplexTableOfContents(titleOptions = unknownFont),
                )

                testUnknownFont(content)
            }

            private fun testUnknownFont(content: TextContent) {
                val action = UpdateText(Text(TEXT_ID_0, content = content))

                assertIllegalArgument("Requires unknown Font 99!") { REDUCER.invoke(STATE, action) }
            }

            @Test
            fun `Unknown spell`() {
                val content = AbstractText(AbstractContent(100, setOf(UNKNOWN_SPELL_ID)))
                val action = UpdateText(Text(TEXT_ID_0, content = content))

                assertIllegalArgument("Requires unknown Spell 99!") { REDUCER.invoke(STATE, action) }
            }

            @Test
            fun `Unknown spell in chapter`() {
                val chapter = AbstractChapter(0, AbstractContent(100, setOf(UNKNOWN_SPELL_ID)))
                val content = AbstractChapters(listOf(chapter))
                val action = UpdateText(Text(TEXT_ID_0, content = content))

                assertIllegalArgument("Requires unknown Spell 99!") { REDUCER.invoke(STATE, action) }
            }

            @Test
            fun `Successful update`() {
                val content = AbstractText(AbstractContent(100, setOf(SPELL_ID_0)))
                val text = Text(TEXT_ID_0, content = content)
                val action = UpdateText(text)

                assertEquals(text, REDUCER.invoke(STATE, action).first.getTextStorage().get(TEXT_ID_0))
            }
        }
    }

}