package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Employed
import at.orchaldir.gm.core.model.character.EmployedByRealm
import at.orchaldir.gm.core.model.character.EmployedBySettlement
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.economy.job.EmployerType
import at.orchaldir.gm.core.model.economy.job.Job
import at.orchaldir.gm.core.model.realm.Realm
import at.orchaldir.gm.core.model.realm.Settlement
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class EmploymentStatusTest {

    private val state = State(
        listOf(
            Storage(CALENDAR0),
            Storage(Business(BUSINESS_ID_0)),
            Storage(
                listOf(
                    Job(JOB_ID_0),
                    Job(JOB_ID_1, employerType = EmployerType.Realm),
                    Job(JOB_ID_2, employerType = EmployerType.Settlement),
                )
            ),
            Storage(Realm(REALM_ID_0, date = YEAR0)),
            Storage(Settlement(SETTLEMENT_ID_0, date = YEAR0)),
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
        fun `Cannot use wrong employer type`() {
            assertIllegalArgument("Job 2 has the wrong type of employer!") {
                checkEmploymentStatusHistory(state, History(Employed(BUSINESS_ID_0, JOB_ID_2)), DAY0)
            }
        }

        @Test
        fun `Character employed by a business before its founding`() {
            val newState = state.updateStorage(Business(BUSINESS_ID_0, date = DAY1))

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
                checkEmploymentStatusHistory(state, History(EmployedByRealm(JOB_ID_1, UNKNOWN_REALM_ID)), DAY0)
            }
        }

        @Test
        fun `Cannot use wrong employer type`() {
            assertIllegalArgument("Job 2 has the wrong type of employer!") {
                checkEmploymentStatusHistory(state, History(EmployedByRealm(JOB_ID_2, REALM_ID_0)), DAY0)
            }
        }

        @Test
        fun `Character employed by a realm before its founding`() {
            val newState = state.updateStorage(Realm(REALM_ID_0, date = DAY1))

            assertIllegalArgument("The Realm 0 doesn't exist at the required date!") {
                checkEmploymentStatusHistory(newState, History(EmployedByRealm(JOB_ID_1, REALM_ID_0)), DAY0)
            }
        }

        @Test
        fun `Character has a valid job at a settlement`() {
            checkEmploymentStatusHistory(state, History(EmployedByRealm(JOB_ID_1, REALM_ID_0)), DAY0)
        }
    }

    @Nested
    inner class EmployedBySettlementTest {

        @Test
        fun `Cannot use unknown job`() {
            assertIllegalArgument("Requires unknown Job 99!") {
                checkEmploymentStatusHistory(
                    state,
                    History(EmployedBySettlement(UNKNOWN_JOB_ID, SETTLEMENT_ID_0)),
                    DAY0
                )
            }
        }

        @Test
        fun `Cannot use unknown settlement`() {
            assertIllegalArgument("Requires unknown Settlement 99!") {
                checkEmploymentStatusHistory(
                    state,
                    History(EmployedBySettlement(JOB_ID_2, UNKNOWN_SETTLEMENT_ID)),
                    DAY0
                )
            }
        }

        @Test
        fun `Cannot use wrong employer type`() {
            assertIllegalArgument("Job 0 has the wrong type of employer!") {
                checkEmploymentStatusHistory(state, History(EmployedBySettlement(JOB_ID_0, SETTLEMENT_ID_0)), DAY0)
            }
        }

        @Test
        fun `Character employed by a settlement before its founding`() {
            val newState = state.updateStorage(Settlement(SETTLEMENT_ID_0, date = DAY1))

            assertIllegalArgument("The Settlement 0 doesn't exist at the required date!") {
                checkEmploymentStatusHistory(newState, History(EmployedBySettlement(JOB_ID_2, SETTLEMENT_ID_0)), DAY0)
            }
        }

        @Test
        fun `Character has a valid job at a settlement`() {
            checkEmploymentStatusHistory(state, History(EmployedBySettlement(JOB_ID_2, SETTLEMENT_ID_0)), DAY0)
        }
    }

}