package at.orchaldir.gm.core.reducer.magic

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteSpell
import at.orchaldir.gm.core.action.UpdateIllness
import at.orchaldir.gm.core.action.UpdateSpell
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.illness.Illness
import at.orchaldir.gm.core.model.IllnessId
import at.orchaldir.gm.core.model.magic.*
import at.orchaldir.gm.core.model.religion.Domain
import at.orchaldir.gm.core.model.util.CreatedByCharacter
import at.orchaldir.gm.core.model.util.CreatedOrigin
import at.orchaldir.gm.core.model.util.SomeOf
import at.orchaldir.gm.core.model.util.UndefinedCreator
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

private val spell0 = Spell(SPELL_ID_0)
private val STATE = State(
    listOf(
        Storage(CALENDAR0),
        Storage(spell0),
    )
)

class SpellTest {

    @Nested
    inner class DeleteTest {
        val action = DeleteSpell(SPELL_ID_0)

        @Test
        fun `Can delete an existing spell`() {
            assertEquals(0, REDUCER.invoke(STATE, action).first.getSpellStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            assertIllegalArgument("Requires unknown Spell 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot delete a spell used by a domain`() {
            val state = STATE.updateStorage(Storage(Domain(DOMAIN_ID_0, spells = SomeOf(SPELL_ID_0))))

            assertIllegalArgument("Cannot delete Spell 0, because it is used!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot delete a spell used by a spell group`() {
            val state = STATE.updateStorage(Storage(SpellGroup(SPELL_GROUP_ID_0, spells = setOf(SPELL_ID_0))))

            assertIllegalArgument("Cannot delete Spell 0, because it is used!") { REDUCER.invoke(state, action) }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateSpell(Spell(SPELL_ID_0))
            val state = STATE.removeStorage(SPELL_ID_0)

            assertIllegalArgument("Requires unknown Spell 0!") { REDUCER.invoke(state, action) }
        }

        // See OriginTest for more
        @Test
        fun `Test origin`() {
            val origin = CreatedOrigin<SpellId>(CreatedByCharacter(UNKNOWN_CHARACTER_ID))
            val action = UpdateSpell(Spell(SPELL_ID_0, origin = origin))

            assertIllegalArgument("Cannot use an unknown Character 99 as Creator!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Update a spell`() {
            val spell = Spell(SPELL_ID_0, NAME)
            val action = UpdateSpell(spell)

            assertEquals(spell, REDUCER.invoke(STATE, action).first.getSpellStorage().get(SPELL_ID_0))
        }
    }

}