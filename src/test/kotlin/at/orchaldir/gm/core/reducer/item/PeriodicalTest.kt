package at.orchaldir.gm.core.reducer.item

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeletePeriodical
import at.orchaldir.gm.core.action.UpdatePeriodical
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.item.periodical.DailyPublication
import at.orchaldir.gm.core.model.item.periodical.Periodical
import at.orchaldir.gm.core.model.item.periodical.WeeklyPublication
import at.orchaldir.gm.core.model.language.Language
import at.orchaldir.gm.core.model.name.NameWithReference
import at.orchaldir.gm.core.model.name.ReferencedFullName
import at.orchaldir.gm.core.model.name.SimpleName
import at.orchaldir.gm.core.model.util.CreatedByCharacter
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.util.OwnedByCharacter
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith


class PeriodicalTest {

    @Nested
    inner class DeleteTest {
        val action = DeletePeriodical(PERIODICAL_ID_0)

        @Test
        fun `Can delete an existing periodical`() {
            val state = State(Storage(Periodical(PERIODICAL_ID_0)))

            assertEquals(0, REDUCER.invoke(state, action).first.getPeriodicalStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            assertIllegalArgument("Requires unknown Periodical 0!") { REDUCER.invoke(State(), action) }
        }
    }

    @Nested
    inner class UpdateTest {

        private val STATE = State(
            listOf(
                Storage(Periodical(PERIODICAL_ID_0)),
                Storage(CALENDAR0),
                Storage(Character(CHARACTER_ID_0)),
                Storage(Language(LANGUAGE_ID_0)),
            )
        )

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdatePeriodical(Periodical(PERIODICAL_ID_0))
            val state = STATE.removeStorage(PERIODICAL_ID_0)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Named after unknown character`() {
            val name = NameWithReference(ReferencedFullName(CHARACTER_ID_0), "A", "B")
            val action = UpdatePeriodical(Periodical(PERIODICAL_ID_0, name))
            val state = STATE.removeStorage(CHARACTER_ID_0)

            assertIllegalArgument("Reference for complex name is unknown!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Owner is an unknown character`() {
            val action =
                UpdatePeriodical(Periodical(PERIODICAL_ID_0, ownership = History(OwnedByCharacter(CHARACTER_ID_0))))
            val state = STATE.removeStorage(CHARACTER_ID_0)

            assertIllegalArgument("Cannot use an unknown character 0 as owner!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Founder is an unknown character`() {
            val action = UpdatePeriodical(Periodical(PERIODICAL_ID_0, founder = CreatedByCharacter(CHARACTER_ID_0)))
            val state = STATE.removeStorage(CHARACTER_ID_0)

            assertIllegalArgument("Cannot use an unknown character 0 as Founder!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Date is in the future`() {
            val action = UpdatePeriodical(Periodical(PERIODICAL_ID_0, frequency = DailyPublication(FUTURE_DAY_0)))

            assertIllegalArgument("Date (Founding) is in the future!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `The calendar is unknown`() {
            val action = UpdatePeriodical(Periodical(PERIODICAL_ID_0, calendar = UNKNOWN_CALENDAR_ID))

            assertIllegalArgument("Requires unknown Calendar 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `The calendar doesn't support weeks`() {
            val action = UpdatePeriodical(Periodical(PERIODICAL_ID_0, frequency = WeeklyPublication()))

            assertIllegalArgument("The Calendar 0 doesn't support Weekly!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `The language is unknown`() {
            val action = UpdatePeriodical(Periodical(PERIODICAL_ID_0, language = UNKNOWN_LANGUAGE_ID))

            assertIllegalArgument("Requires unknown Language 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Test Success`() {
            val periodical = Periodical(PERIODICAL_ID_0, SimpleName("Test"))
            val action = UpdatePeriodical(periodical)

            assertEquals(periodical, REDUCER.invoke(STATE, action).first.getPeriodicalStorage().get(PERIODICAL_ID_0))
        }
    }

}