package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.util.OnMoon
import at.orchaldir.gm.core.model.util.Position
import at.orchaldir.gm.core.model.world.moon.Moon
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MoonTest {

    @Nested
    inner class CanDeleteTest {
        val moon = Moon(MOON_ID_0)
        private val state = State(
            listOf(
                Storage(moon),
            )
        )
        private val position = OnMoon(MOON_ID_0)

        @Test
        fun `Cannot delete an element used as home`() {
            val housingStatus = History<Position>(position)
            val character = Character(CHARACTER_ID_0, housingStatus = housingStatus)
            val newState = state.updateStorage(Storage(character))

            assertCanDelete(newState, CHARACTER_ID_0)
        }

        @Test
        fun `Cannot delete an element used as a position`() {
            val business = Business(BUSINESS_ID_0, position = position)
            val newState = state.updateStorage(Storage(business))

            assertCanDelete(newState, BUSINESS_ID_0)
        }

        private fun <ID : Id<ID>> assertCanDelete(state: State, blockingId: ID) {
            assertEquals(DeleteResult(MOON_ID_0).addId(blockingId), state.canDeleteMoon(MOON_ID_0))
        }
    }

}