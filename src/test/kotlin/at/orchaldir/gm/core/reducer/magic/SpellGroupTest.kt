package at.orchaldir.gm.core.reducer.magic

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteSpellGroup
import at.orchaldir.gm.core.action.UpdateSpellGroup
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.magic.MagicTradition
import at.orchaldir.gm.core.model.magic.Spell
import at.orchaldir.gm.core.model.magic.SpellGroup
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SpellGroupTest {

    private val STATE = State(
        listOf(
            Storage(CALENDAR0),
            Storage(SpellGroup(SPELL_GROUP_ID_0)),
            Storage(Spell(SPELL_ID_0)),
        )
    )

    @Nested
    inner class DeleteTest {
        val action = DeleteSpellGroup(SPELL_GROUP_ID_0)

        @Test
        fun `Can delete an existing domain`() {
            assertEquals(0, REDUCER.invoke(STATE, action).first.getSpellGroupStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeleteSpellGroup(UNKNOWN_SPELL_GROUP_ID)

            assertIllegalArgument("Requires unknown Spell Group 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Cannot delete a spell group used by a tradition`() {
            val tradition = MagicTradition(MAGIC_TRADITION_ID_0, groups = setOf(SPELL_GROUP_ID_0))
            val state = STATE.updateStorage(Storage(tradition))

            assertIllegalArgument("Cannot delete Spell Group 0, because it is used!") { REDUCER.invoke(state, action) }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateSpellGroup(SpellGroup(UNKNOWN_SPELL_GROUP_ID))

            assertIllegalArgument("Requires unknown Spell Group 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Cannot use an unknown spell`() {
            val action = UpdateSpellGroup(SpellGroup(SPELL_GROUP_ID_0, spells = setOf(UNKNOWN_SPELL_ID)))

            assertIllegalArgument("Requires unknown Spell 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Update a domain`() {
            val domain = SpellGroup(SPELL_GROUP_ID_0, NAME, setOf(SPELL_ID_0))
            val action = UpdateSpellGroup(domain)

            assertEquals(domain, REDUCER.invoke(STATE, action).first.getSpellGroupStorage().get(SPELL_GROUP_ID_0))
        }
    }

}