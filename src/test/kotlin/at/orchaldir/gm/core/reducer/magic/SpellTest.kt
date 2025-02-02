package at.orchaldir.gm.core.reducer.magic

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteSpell
import at.orchaldir.gm.core.action.UpdateSpell
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.magic.ModifiedSpell
import at.orchaldir.gm.core.model.magic.Spell
import at.orchaldir.gm.core.model.magic.TranslatedSpell
import at.orchaldir.gm.core.model.util.UndefinedCreator
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private val spell0 = Spell(SPELL_ID_0)
private val STATE = State(
    listOf(
        Storage(CALENDAR0),
        Storage(Business(BUSINESS_ID_0)),
        Storage(Character(CHARACTER_ID_0)),
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
        fun `Cannot delete a spell modified by another spell`() {
            val spell1 = Spell(SPELL_ID_1, origin = ModifiedSpell(UndefinedCreator, SPELL_ID_0))
            val state = STATE.updateStorage(Storage(listOf(spell0, spell1)))

            assertIllegalArgument("The spell 0 is used!") {
                REDUCER.invoke(state, action)
            }
        }

        @Test
        fun `Cannot delete a translated spell`() {
            val spell1 = Spell(SPELL_ID_1, origin = TranslatedSpell(UndefinedCreator, SPELL_ID_0))
            val state = STATE.updateStorage(Storage(listOf(spell0, spell1)))

            assertIllegalArgument("The spell 0 is used!") {
                REDUCER.invoke(state, action)
            }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateSpell(Spell(SPELL_ID_0))
            val state = STATE.removeStorage(SPELL_ID_0)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot modify an unknown spell`() {
            val spell = Spell(SPELL_ID_0, origin = ModifiedSpell(UndefinedCreator, SPELL_ID_1))
            val action = UpdateSpell(spell)

            assertIllegalArgument("Original spell 1 is unknown!") {
                REDUCER.invoke(STATE, action)
            }
        }

        @Test
        fun `Cannot translate an unknown spell`() {
            val spell = Spell(SPELL_ID_0, origin = TranslatedSpell(UndefinedCreator, SPELL_ID_1))
            val action = UpdateSpell(spell)

            assertIllegalArgument("Original spell 1 is unknown!") {
                REDUCER.invoke(STATE, action)
            }
        }

        @Test
        fun `Update a spell`() {
            val spell = Spell(SPELL_ID_0, "Test")
            val action = UpdateSpell(spell)

            assertEquals(spell, REDUCER.invoke(STATE, action).first.getSpellStorage().get(SPELL_ID_0))
        }
    }

}