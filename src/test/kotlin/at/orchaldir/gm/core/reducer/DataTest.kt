package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.CALENDAR_ID_0
import at.orchaldir.gm.CURRENCY_ID_0
import at.orchaldir.gm.STANDARD_ID_0
import at.orchaldir.gm.STANDARD_ID_1
import at.orchaldir.gm.UNKNOWN_CALENDAR_ID
import at.orchaldir.gm.UNKNOWN_CURRENCY_ID
import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.core.action.UpdateData
import at.orchaldir.gm.core.model.Data
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.money.Currency
import at.orchaldir.gm.core.model.economy.standard.StandardOfLiving
import at.orchaldir.gm.core.model.name.Name
import at.orchaldir.gm.core.model.time.Time
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.selector.economy.Economy
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class DataTest {

    private val state = State(
        listOf(
            Storage(Calendar(CALENDAR_ID_0)),
            Storage(Currency(CURRENCY_ID_0)),
        )
    )

    @Test
    fun `Cannot use an unknown calendar`() {
        val action = UpdateData(Data(time = Time(UNKNOWN_CALENDAR_ID)))

        assertIllegalArgument("Requires unknown Calendar 99!") { REDUCER.invoke(state, action) }
    }

    @Nested
    inner class EconomyTest {

        @Test
        fun `Cannot use an unknown currency`() {
            val action = UpdateData(Data(economy = Economy(UNKNOWN_CURRENCY_ID)))

            assertIllegalArgument("Requires unknown Currency 99!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot reuse standard of living names`() {
            val name = Name.init("A")
            val standards = listOf(StandardOfLiving(STANDARD_ID_0, name), StandardOfLiving(STANDARD_ID_1, name))
            val action = UpdateData(Data(economy = Economy(standardsOfLiving = standards)))

            assertIllegalArgument("Name 'A' is duplicated for standards of living!") { REDUCER.invoke(state, action) }
        }
    }

}