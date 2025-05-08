package at.orchaldir.gm.core.reducer.font

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteFont
import at.orchaldir.gm.core.action.UpdateFont
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.money.Coin
import at.orchaldir.gm.core.model.economy.money.CurrencyUnit
import at.orchaldir.gm.core.model.economy.money.ShowValue
import at.orchaldir.gm.core.model.font.Font
import at.orchaldir.gm.core.model.font.SolidFont
import at.orchaldir.gm.core.model.item.text.Book
import at.orchaldir.gm.core.model.item.text.Text
import at.orchaldir.gm.core.model.item.text.book.Hardcover
import at.orchaldir.gm.core.model.item.text.book.typography.SimpleTitleTypography
import at.orchaldir.gm.core.model.item.text.content.AbstractText
import at.orchaldir.gm.core.model.item.text.content.ContentStyle
import at.orchaldir.gm.core.model.item.text.content.SimplePageNumbering
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromCentimeters
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class FontTest {
    private val font0 = Font(FONT_ID_0)
    private val STATE = State(
        listOf(
            Storage(CALENDAR0),
            Storage(font0),
        )
    )

    @Nested
    inner class DeleteTest {
        val action = DeleteFont(FONT_ID_0)
        val font = SolidFont(fromCentimeters(1), font = FONT_ID_0)

        @Test
        fun `Can delete an existing font`() {
            assertEquals(0, REDUCER.invoke(STATE, action).first.getFontStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            assertIllegalArgument("Requires unknown Font 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot delete, if used by a currency coin`() {
            val coin = Coin(front = ShowValue(FONT_ID_0))
            val state = STATE.updateStorage(Storage(CurrencyUnit(CURRENCY_UNIT_ID_0, format = coin)))

            assertIllegalArgument("Font 0 is used") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot delete, if used by a book cover`() {
            val book = Book(Hardcover(typography = SimpleTitleTypography(font)))
            val state = STATE.updateStorage(Storage(Text(TEXT_ID_0, format = book)))

            assertIllegalArgument("Font 0 is used") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot delete, if used by a book's content`() {
            val content = AbstractText(style = ContentStyle(title = font))
            val state = STATE.updateStorage(Storage(Text(TEXT_ID_0, content = content)))

            assertIllegalArgument("Font 0 is used") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot delete, if used by a book's page numbering`() {
            val content = AbstractText(pageNumbering = SimplePageNumbering(font))
            val state = STATE.updateStorage(Storage(Text(TEXT_ID_0, content = content)))

            assertIllegalArgument("Font 0 is used") { REDUCER.invoke(state, action) }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateFont(Font(FONT_ID_0))
            val state = STATE.removeStorage(FONT_ID_0)

            assertIllegalArgument("Requires unknown Font 0!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Date is in the future`() {
            val action = UpdateFont(Font(FONT_ID_0, date = FUTURE_DAY_0))

            assertIllegalArgument("Date (Font) is in the future!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Update a font`() {
            val font = Font(FONT_ID_0, NAME)
            val action = UpdateFont(font)

            assertEquals(font, REDUCER.invoke(STATE, action).first.getFontStorage().get(FONT_ID_0))
        }
    }

}