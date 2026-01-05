package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.CURRENCY_UNIT_ID_0
import at.orchaldir.gm.FONT_ID_0
import at.orchaldir.gm.MATERIAL_ID_0
import at.orchaldir.gm.TEXT_ID_0
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.money.Coin
import at.orchaldir.gm.core.model.economy.money.CurrencyUnit
import at.orchaldir.gm.core.model.economy.money.ShowValue
import at.orchaldir.gm.core.model.item.text.Book
import at.orchaldir.gm.core.model.item.text.Text
import at.orchaldir.gm.core.model.item.text.book.Hardcover
import at.orchaldir.gm.core.model.item.text.book.typography.SimpleTitleTypography
import at.orchaldir.gm.core.model.item.text.content.*
import at.orchaldir.gm.core.model.util.font.Font
import at.orchaldir.gm.core.model.util.font.SolidFont
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.unit.Distance
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class FontTest {

    @Nested
    inner class CanDeleteTest {
        private val font = Font(FONT_ID_0)
        val option = SolidFont(Distance.fromCentimeters(1), font = FONT_ID_0)
        private val state = State(
            listOf(
                Storage(font),
            )
        )

        @Test
        fun `Cannot delete, if used by a currency coin`() {
            val coin = Coin(MATERIAL_ID_0, front = ShowValue(FONT_ID_0))
            val unit = CurrencyUnit(CURRENCY_UNIT_ID_0, format = coin)
            val newState = state.updateStorage(unit)

            failCanDelete(newState, CURRENCY_UNIT_ID_0)
        }

        @Test
        fun `Cannot delete, if used by a book cover`() {
            val book = Book(Hardcover(typography = SimpleTitleTypography(option)))

            failCanDelete(Text(TEXT_ID_0, format = book))
        }

        @Test
        fun `Cannot delete, if used by a book's content`() {
            val content = AbstractText(style = ContentStyle(title = option))

            failCanDelete(Text(TEXT_ID_0, content = content))
        }

        @Test
        fun `Cannot delete, if used by a book's page numbering`() {
            val content = AbstractText(pageNumbering = SimplePageNumbering(option))

            failCanDelete(Text(TEXT_ID_0, content = content))
        }

        @Test
        fun `Cannot delete, if used by a book's initials`() {
            val content = AbstractText(style = ContentStyle(initials = FontInitials(option)))

            failCanDelete(Text(TEXT_ID_0, content = content))
        }

        @Test
        fun `Cannot delete, if used by a book's table of content - main`() {
            val content = AbstractChapters(
                pageNumbering = PageNumberingReusingFont(),
                tableOfContents = ComplexTableOfContents(mainOptions = option),
            )

            failCanDelete(Text(TEXT_ID_0, content = content))
        }

        @Test
        fun `Cannot delete, if used by a book's table of content - title`() {
            val content = AbstractChapters(
                pageNumbering = PageNumberingReusingFont(),
                tableOfContents = ComplexTableOfContents(titleOptions = option),
            )

            failCanDelete(Text(TEXT_ID_0, content = content))
        }

        private fun failCanDelete(text: Text) {
            val newState = state.updateStorage(text)

            failCanDelete(newState, TEXT_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(DeleteResult(FONT_ID_0).addId(blockingId), state.canDeleteFont(FONT_ID_0))
        }
    }

}