package at.orchaldir.gm.core.reducer.magic

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.UpdateSpellGroup
import at.orchaldir.gm.core.model.State
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