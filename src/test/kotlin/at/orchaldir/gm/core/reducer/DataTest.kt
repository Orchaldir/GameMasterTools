package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.UpdateData
import at.orchaldir.gm.core.model.Data
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.EconomyData
import at.orchaldir.gm.core.model.economy.job.AffordableStandardOfLiving
import at.orchaldir.gm.core.model.economy.job.Job
import at.orchaldir.gm.core.model.economy.money.Currency
import at.orchaldir.gm.core.model.economy.money.Price
import at.orchaldir.gm.core.model.economy.standard.StandardOfLiving
import at.orchaldir.gm.core.model.realm.District
import at.orchaldir.gm.core.model.realm.Realm
import at.orchaldir.gm.core.model.realm.Town
import at.orchaldir.gm.core.model.time.Time
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.realm.population.AbstractPopulation
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DataTest {

    private val name0 = Name.init("A")
    private val name1 = Name.init("B")
    private val usedIncome = AffordableStandardOfLiving(STANDARD_ID_1)
    private val population = AbstractPopulation(income = usedIncome)
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
            val action = UpdateData(Data(economy = EconomyData(UNKNOWN_CURRENCY_ID)))

            assertIllegalArgument("Requires unknown Currency 99!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot reuse standard of living names`() {
            val standards = listOf(StandardOfLiving(STANDARD_ID_0, name0), StandardOfLiving(STANDARD_ID_1, name0))
            val action = UpdateData(Data(economy = EconomyData(standardsOfLiving = standards)))

            assertIllegalArgument("Name 'A' is duplicated for standards of living!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `A max income must be greater than the last one`() {
            val income = Price(100)
            val standards = listOf(
                StandardOfLiving(STANDARD_ID_0, name0, income),
                StandardOfLiving(STANDARD_ID_1, name1, income),
            )
            val action = UpdateData(Data(economy = EconomyData(standardsOfLiving = standards)))

            assertIllegalArgument("Standard of Living 'B' must have a greater income than the last one!") {
                REDUCER.invoke(state, action)
            }
        }

        @Nested
        inner class CanDeleteTest {

            @Test
            fun `Cannot delete a standard of living used by a district`() {
                assertDelete(Storage(District(DISTRICT_ID_0, population = population)))
            }

            @Test
            fun `Cannot delete a standard of living used by a job`() {
                assertDelete(Storage(Job(JOB_ID_0, income = usedIncome)))
            }

            @Test
            fun `Cannot delete a standard of living used by a realm`() {
                assertDelete(Storage(Realm(REALM_ID_0, population = population)))
            }

            @Test
            fun `Cannot delete a standard of living used by a town`() {
                assertDelete(Storage(Town(TOWN_ID_0, population = population)))
            }

            private fun <ID : Id<ID>, ELEMENT : Element<ID>> assertDelete(storage: Storage<ID, ELEMENT>) {
                val newState = state.updateStorage(storage)
                val data = Data(economy = EconomyData(standardsOfLiving = listOf(StandardOfLiving(STANDARD_ID_0))))
                val action = UpdateData(data)

                assertIllegalArgument("The number of required Standards of Living is 2!") {
                    REDUCER.invoke(newState, action)
                }
            }
        }

        @Test
        fun `Update multiple Standards of Living`() {
            val standards = listOf(
                StandardOfLiving(STANDARD_ID_0, name0, Price(100)),
                StandardOfLiving(STANDARD_ID_1, name1, Price(200)),
            )
            val data = Data(economy = EconomyData(standardsOfLiving = standards))
            val action = UpdateData(data)

            assertEquals(data, REDUCER.invoke(state, action).first.data)
        }
    }

}