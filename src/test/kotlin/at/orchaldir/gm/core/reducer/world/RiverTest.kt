package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.NAME
import at.orchaldir.gm.RIVER_ID_0
import at.orchaldir.gm.core.action.UpdateAction
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.terrain.River
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RiverTest {

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateAction(River(RIVER_ID_0))

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Update is valid`() {
            val state = State(Storage(River(RIVER_ID_0)))
            val river = River(RIVER_ID_0, NAME)
            val action = UpdateAction(river)

            assertEquals(river, REDUCER.invoke(state, action).first.getRiverStorage().get(RIVER_ID_0))
        }
    }

}