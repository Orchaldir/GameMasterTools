package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.BUSINESS_ID_0
import at.orchaldir.gm.CHARACTER_ID_0
import at.orchaldir.gm.WORLD_ID_0
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.util.OnWorld
import at.orchaldir.gm.core.model.util.Position
import at.orchaldir.gm.core.model.world.World
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class WorldTest {

    @Nested
    inner class CanDeleteTest {
        private val world = World(WORLD_ID_0)
        private val state = State(
            listOf(
                Storage(world),
            )
        )
        private val position = OnWorld(WORLD_ID_0)

        @Test
        fun `Cannot delete an element used as home`() {
            val housingStatus = History<Position>(position)
            val character = Character(CHARACTER_ID_0, housingStatus = housingStatus)
            val newState = state.updateStorage(character)

            failCanDelete(newState, CHARACTER_ID_0)
        }

        @Test
        fun `Cannot delete an element used as a position`() {
            val business = Business(BUSINESS_ID_0, position = position)
            val newState = state.updateStorage(business)

            failCanDelete(newState, BUSINESS_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(DeleteResult(WORLD_ID_0).addId(blockingId), state.canDeleteWorld(WORLD_ID_0))
        }
    }

}