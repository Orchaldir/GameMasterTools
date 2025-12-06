package at.orchaldir.gm.core.selector.rpg

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterTemplate
import at.orchaldir.gm.core.model.religion.God
import at.orchaldir.gm.core.model.rpg.Statblock
import at.orchaldir.gm.core.model.rpg.trait.CharacterTrait
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CharacterTraitTest {

    @Nested
    inner class CanDeleteTest {
        private val trait = CharacterTrait(CHARACTER_TRAIT_ID_0)
        private val state = State(
            listOf(
                Storage(trait),
            )
        )

        @Test
        fun `Cannot delete a character trait used by a character`() {
            val character = Character(CHARACTER_ID_0, personality = setOf(CHARACTER_TRAIT_ID_0))
            val newState = state.updateStorage(Storage(character))

            failCanDelete(newState, CHARACTER_ID_0)
        }

        @Test
        fun `Cannot delete a character trait used by a character template`() {
            val statblock = Statblock(traits = setOf(CHARACTER_TRAIT_ID_0))
            val character = CharacterTemplate(CHARACTER_TEMPLATE_ID_0, race = RACE_ID_0, statblock = statblock)
            val newState = state.updateStorage(Storage(character))

            failCanDelete(newState, CHARACTER_TEMPLATE_ID_0)
        }

        @Test
        fun `Cannot delete a character trait used by a god`() {
            val god = God(GOD_ID_0, personality = setOf(CHARACTER_TRAIT_ID_0))
            val newState = state.updateStorage(Storage(god))

            failCanDelete(newState, GOD_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(
                DeleteResult(CHARACTER_TRAIT_ID_0).addId(blockingId),
                state.canDeleteCharacterTrait(CHARACTER_TRAIT_ID_0)
            )
        }
    }

}