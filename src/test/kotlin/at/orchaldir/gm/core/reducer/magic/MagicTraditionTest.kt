package at.orchaldir.gm.core.reducer.magic

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteMagicTradition
import at.orchaldir.gm.core.action.UpdateMagicTradition
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.magic.MagicTradition
import at.orchaldir.gm.core.model.magic.SpellGroup
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MagicTraditionTest {

    private val STATE = State(
        listOf(
            Storage(CALENDAR0),
            Storage(MagicTradition(MAGIC_TRADITION_ID_0)),
            Storage(SpellGroup(SPELL_GROUP_ID_0)),
        )
    )

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateMagicTradition(MagicTradition(UNKNOWN_MAGIC_TRADITION_ID))

            assertIllegalArgument("Requires unknown Magic Tradition 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Cannot use an unknown spell`() {
            val tradition = MagicTradition(MAGIC_TRADITION_ID_0, groups = setOf(UNKNOWN_SPELL_GROUP_ID))
            val action = UpdateMagicTradition(tradition)

            assertIllegalArgument("Requires unknown Spell Group 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Update a domain`() {
            val domain = MagicTradition(MAGIC_TRADITION_ID_0, NAME, groups = setOf(SPELL_GROUP_ID_0))
            val action = UpdateMagicTradition(domain)

            assertEquals(
                domain,
                REDUCER.invoke(STATE, action).first.getMagicTraditionStorage().get(MAGIC_TRADITION_ID_0)
            )
        }
    }

}