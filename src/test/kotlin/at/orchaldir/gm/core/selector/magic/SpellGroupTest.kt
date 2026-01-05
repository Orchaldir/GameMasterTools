package at.orchaldir.gm.core.selector.magic

import at.orchaldir.gm.MAGIC_TRADITION_ID_0
import at.orchaldir.gm.SPELL_GROUP_ID_0
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.magic.MagicTradition
import at.orchaldir.gm.core.model.magic.SpellGroup
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SpellGroupTest {

    @Nested
    inner class CanDeleteTest {
        private val group = SpellGroup(SPELL_GROUP_ID_0)
        private val state = State(
            listOf(
                Storage(group),
            )
        )

        @Test
        fun `Cannot delete a spell group used by a tradition`() {
            val tradition = MagicTradition(MAGIC_TRADITION_ID_0, groups = setOf(SPELL_GROUP_ID_0))
            val newState = state.updateStorage(tradition)

            failCanDelete(newState, MAGIC_TRADITION_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(
                DeleteResult(SPELL_GROUP_ID_0).addId(blockingId),
                state.canDeleteSpellGroup(SPELL_GROUP_ID_0)
            )
        }
    }

}