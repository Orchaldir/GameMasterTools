package at.orchaldir.gm.core.reducer.magic

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.UpdateAction
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.magic.Spell
import at.orchaldir.gm.core.model.util.CharacterReference
import at.orchaldir.gm.core.model.util.origin.CreatedElement
import at.orchaldir.gm.core.model.util.origin.ModifiedElement
import at.orchaldir.gm.core.model.util.origin.TranslatedElement
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
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateAction(Spell(SPELL_ID_0))
            val state = STATE.removeStorage(SPELL_ID_0)

            assertIllegalArgument("Requires unknown Spell 0!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot modify an unknown spell`() {
            val origin = ModifiedElement(SPELL_ID_1)
            val spell = Spell(SPELL_ID_0, origin = origin)
            val action = UpdateAction(spell)

            assertIllegalArgument("Requires unknown parent Spell 1!") {
                REDUCER.invoke(STATE, action)
            }
        }

        @Test
        fun `Cannot translate an unknown spell`() {
            val origin = TranslatedElement(UNKNOWN_SPELL_ID)
            val spell = Spell(SPELL_ID_0, origin = origin)
            val action = UpdateAction(spell)

            assertIllegalArgument("Requires unknown parent Spell 99!") {
                REDUCER.invoke(STATE, action)
            }
        }

        @Test
        fun `Creator must exist`() {
            val origin = CreatedElement(CharacterReference(UNKNOWN_CHARACTER_ID))
            val spell = Spell(SPELL_ID_0, origin = origin)
            val action = UpdateAction(spell)

            assertIllegalArgument("Requires unknown Creator (Character 99)!") {
                REDUCER.invoke(STATE, action)
            }
        }

        @Test
        fun `Date is in the future`() {
            val action = UpdateAction(Spell(SPELL_ID_0, date = FUTURE_DAY_0))

            assertIllegalArgument("Date (Spell) is in the future!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Update a spell`() {
            val spell = Spell(SPELL_ID_0, NAME)
            val action = UpdateAction(spell)

            assertEquals(spell, REDUCER.invoke(STATE, action).first.getSpellStorage().get(SPELL_ID_0))
        }
    }

}