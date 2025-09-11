package at.orchaldir.gm.core.reducer.religion

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.UpdatePantheon
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.religion.God
import at.orchaldir.gm.core.model.religion.Pantheon
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PantheonTest {

    private val pantheon0 = Pantheon(PANTHEON_ID_0)
    private val state = State(
        listOf(
            Storage(CALENDAR0),
            Storage(God(GOD_ID_0)),
            Storage(pantheon0),
        )
    )

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdatePantheon(Pantheon(UNKNOWN_PANTHEON_ID))

            assertIllegalArgument("Requires unknown Pantheon 99!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot use an unknown god`() {
            val action = UpdatePantheon(Pantheon(PANTHEON_ID_0, gods = setOf(UNKNOWN_GOD_ID)))

            assertIllegalArgument("Requires unknown God 99!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Update a pantheon`() {
            val pantheon = Pantheon(
                PANTHEON_ID_0,
                gods = setOf(GOD_ID_0),
            )
            val action = UpdatePantheon(pantheon)

            assertEquals(pantheon, REDUCER.invoke(state, action).first.getPantheonStorage().get(PANTHEON_ID_0))
        }
    }

}