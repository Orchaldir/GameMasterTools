package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.RIVER_ID_0
import at.orchaldir.gm.TEXT_ID_0
import at.orchaldir.gm.UNKNOWN_RIVER_ID
import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.core.action.CreateAction
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.terrain.River
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CreateTest {
    val state = State(Storage(River(RIVER_ID_0)))

    @Test
    fun `Can create an element with the correct id`() {
        val storage = REDUCER.invoke(State(), CreateAction(TEXT_ID_0)).first.getTextStorage()

        assertEquals(1, storage.getSize())
        assertEquals(TEXT_ID_0, storage.get(TEXT_ID_0)?.id)
    }

    @Test
    fun `Cannot create an element with the wrong id`() {
        val action = CreateAction(UNKNOWN_RIVER_ID)

        assertIllegalArgument("Added River 99 doesn't have the next free id!") { REDUCER.invoke(state, action) }
    }

}