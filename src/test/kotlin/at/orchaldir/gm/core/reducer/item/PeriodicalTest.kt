package at.orchaldir.gm.core.reducer.item

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeletePeriodical
import at.orchaldir.gm.core.action.UpdatePeriodical
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.culture.language.Language
import at.orchaldir.gm.core.model.item.periodical.Periodical
import at.orchaldir.gm.core.model.item.periodical.PeriodicalIssue
import at.orchaldir.gm.core.model.item.periodical.PublicationFrequency
import at.orchaldir.gm.core.model.util.CharacterReference
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.util.Reference
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


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

        @Test
        fun `Cannot delete, if it has an issue`() {
            val state = State(
                listOf(
                    Storage(Periodical(PERIODICAL_ID_0)),
                    Storage(PeriodicalIssue(ISSUE_ID_0, PERIODICAL_ID_0)),
                )
            )

            assertIllegalArgument("Cannot delete Periodical 0, because it is used!") { REDUCER.invoke(state, action) }
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
            val action = UpdatePeriodical(Periodical(UNKNOWN_PERIODICAL_ID))

            assertIllegalArgument("Requires unknown Periodical 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Owner is an unknown character`() {
            val ownership: History<Reference> = History(CharacterReference(UNKNOWN_CHARACTER_ID))
            val action = UpdatePeriodical(Periodical(PERIODICAL_ID_0, ownership = ownership))

            assertIllegalArgument("Cannot use an unknown Character 99 as owner!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Date is in the future`() {
            val action = UpdatePeriodical(Periodical(PERIODICAL_ID_0, date = FUTURE_DAY_0))

            assertIllegalArgument("Date (Founding) is in the future!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `The calendar is unknown`() {
            val action = UpdatePeriodical(Periodical(PERIODICAL_ID_0, calendar = UNKNOWN_CALENDAR_ID))

            assertIllegalArgument("Requires unknown Calendar 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `The calendar doesn't support weeks`() {
            val action = UpdatePeriodical(Periodical(PERIODICAL_ID_0, frequency = PublicationFrequency.Weekly))

            assertIllegalArgument("The Calendar 0 doesn't support Weekly!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `The language is unknown`() {
            val action = UpdatePeriodical(Periodical(PERIODICAL_ID_0, language = UNKNOWN_LANGUAGE_ID))

            assertIllegalArgument("Requires unknown Language 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Test Success`() {
            val periodical = Periodical(PERIODICAL_ID_0, Name.init("Test"))
            val action = UpdatePeriodical(periodical)

            assertEquals(periodical, REDUCER.invoke(STATE, action).first.getPeriodicalStorage().get(PERIODICAL_ID_0))
        }
    }

}