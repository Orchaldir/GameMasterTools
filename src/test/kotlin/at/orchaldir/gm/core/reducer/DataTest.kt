package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.CALENDAR_ID_0
import at.orchaldir.gm.CURRENCY_ID_0
import at.orchaldir.gm.UNKNOWN_CALENDAR_ID
import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.core.action.UpdateData
import at.orchaldir.gm.core.model.Data
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.money.Currency
import at.orchaldir.gm.core.model.time.Time
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Test

class DataTest {

    private val state = State(
        listOf(
            Storage(Calendar(CALENDAR_ID_0)),
            Storage(Currency(CURRENCY_ID_0)),
        )
    )

    @Test
    fun `Cannot use unknown an unknown calendar`() {
        val action = UpdateData(Data(time = Time(UNKNOWN_CALENDAR_ID)))

        assertIllegalArgument("Requires unknown Calendar 99!") { REDUCER.invoke(state, action) }
    }

}