package at.orchaldir.gm.core.reducer.realm

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.UpdateLegalCode
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.LegalCode
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LegalCodeTest {

    private val STATE = State(
        listOf(
            Storage(CALENDAR0),
            Storage(LegalCode(LEGAL_CODE_ID_0)),
        )
    )

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateLegalCode(LegalCode(UNKNOWN_LEGAL_CODE_ID))

            assertIllegalArgument("Requires unknown Legal Code 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Update a code`() {
            val code = LegalCode(LEGAL_CODE_ID_0, NAME)
            val action = UpdateLegalCode(code)

            assertEquals(code, REDUCER.invoke(STATE, action).first.getLegalCodeStorage().get(LEGAL_CODE_ID_0))
        }
    }

}