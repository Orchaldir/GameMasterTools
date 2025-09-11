package at.orchaldir.gm.core.reducer.realm

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.UpdateCatastrophe
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.Catastrophe
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CatastropheTest {

    private val STATE = State(
        listOf(
            Storage(CALENDAR0),
            Storage(Catastrophe(CATASTROPHE_ID_0)),
        )
    )

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateCatastrophe(Catastrophe(UNKNOWN_CATASTROPHE_ID))

            assertIllegalArgument("Requires unknown Catastrophe 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Update a catastrophe`() {
            val catastrophe = Catastrophe(CATASTROPHE_ID_0, NAME)
            val action = UpdateCatastrophe(catastrophe)

            assertEquals(catastrophe, REDUCER.invoke(STATE, action).first.getCatastropheStorage().get(CATASTROPHE_ID_0))
        }
    }

}