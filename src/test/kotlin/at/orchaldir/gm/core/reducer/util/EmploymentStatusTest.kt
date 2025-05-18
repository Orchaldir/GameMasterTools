package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Employed
import at.orchaldir.gm.core.model.character.EmployedByRealm
import at.orchaldir.gm.core.model.character.EmployedByTown
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.economy.job.Job
import at.orchaldir.gm.core.model.realm.Realm
import at.orchaldir.gm.core.model.realm.Town
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class EmploymentStatusTest {

    private val state = State(
        listOf(
            Storage(CALENDAR0),
            Storage(Business(BUSINESS_ID_0)),
            Storage(Job(JOB_ID_0)),
            Storage(Realm(REALM_ID_0, date = YEAR0)),
            Storage(Town(TOWN_ID_0, foundingDate = YEAR0)),
        )
    )

    @Nested
    inner class EmployedTest {

        @Test
        fun `Cannot use unknown business`() {
            assertIllegalArgument("Requires unknown Business 99!") {
                checkEmploymentStatusHistory(state, History(Employed(UNKNOWN_BUSINESS_ID, JOB_ID_0)), DAY0)
            }
        }

        @Test
        fun `Cannot use unknown job`() {
            assertIllegalArgument("Requires unknown Job 99!") {
                checkEmploymentStatusHistory(state, History(Employed(BUSINESS_ID_0, UNKNOWN_JOB_ID)), DAY0)
            }
        }

        @Test
        fun `Character employed by a business before its founding`() {
            val newState = state.updateStorage(Storage(Business(BUSINESS_ID_0, startDate = DAY1)))

            assertIllegalArgument("The Business 0 doesn't exist at the required date!") {
                checkEmploymentStatusHistory(newState, History(Employed(BUSINESS_ID_0, JOB_ID_0)), DAY0)
            }
        }

        @Test
        fun `Character has a valid job`() {
            checkEmploymentStatusHistory(state, History(Employed(BUSINESS_ID_0, JOB_ID_0)), DAY0)
        }
    }

    @Nested
    inner class EmployedByRealmTest {

        @Test
        fun `Cannot use unknown job`() {
            assertIllegalArgument("Requires unknown Job 99!") {
                checkEmploymentStatusHistory(state, History(EmployedByRealm(UNKNOWN_JOB_ID, REALM_ID_0)), DAY0)
            }
        }

        @Test
        fun `Cannot use unknown realm`() {
            assertIllegalArgument("Requires unknown Realm 99!") {
                checkEmploymentStatusHistory(state, History(EmployedByRealm(JOB_ID_0, UNKNOWN_REALM_ID)), DAY0)
            }
        }

        @Test
        fun `Character employed by a realm before its founding`() {
            val newState = state.updateStorage(Storage(Realm(REALM_ID_0, date = DAY1)))

            assertIllegalArgument("The Realm 0 doesn't exist at the required date!") {
                checkEmploymentStatusHistory(newState, History(EmployedByRealm(JOB_ID_0, REALM_ID_0)), DAY0)
            }
        }

        @Test
        fun `Character has a valid job at a town`() {
            checkEmploymentStatusHistory(state, History(EmployedByRealm(JOB_ID_0, REALM_ID_0)), DAY0)
        }
    }

    @Nested
    inner class EmployedByTownTest {

        @Test
        fun `Cannot use unknown job`() {
            assertIllegalArgument("Requires unknown Job 99!") {
                checkEmploymentStatusHistory(state, History(EmployedByTown(UNKNOWN_JOB_ID, TOWN_ID_0)), DAY0)
            }
        }

        @Test
        fun `Cannot use unknown town`() {
            assertIllegalArgument("Requires unknown Town 99!") {
                checkEmploymentStatusHistory(state, History(EmployedByTown(JOB_ID_0, UNKNOWN_TOWN_ID)), DAY0)
            }
        }

        @Test
        fun `Character employed by a town before its founding`() {
            val newState = state.updateStorage(Storage(Town(TOWN_ID_0, foundingDate = DAY1)))

            assertIllegalArgument("The Town 0 doesn't exist at the required date!") {
                checkEmploymentStatusHistory(newState, History(EmployedByTown(JOB_ID_0, TOWN_ID_0)), DAY0)
            }
        }

        @Test
        fun `Character has a valid job at a town`() {
            checkEmploymentStatusHistory(state, History(EmployedByTown(JOB_ID_0, TOWN_ID_0)), DAY0)
        }
    }

}