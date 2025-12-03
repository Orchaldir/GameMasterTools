package at.orchaldir.gm.core.selector.character

import at.orchaldir.gm.CHARACTER_ID_0
import at.orchaldir.gm.GOD_ID_0
import at.orchaldir.gm.PERSONALITY_ID_0
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.rpg.trait.CharacterTrait
import at.orchaldir.gm.core.model.religion.God
import at.orchaldir.gm.core.selector.rpg.canDeleteCharacterTrait
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PersonalityTest {

    @Nested
    inner class CanDeleteTest {
        private val trait = CharacterTrait(PERSONALITY_ID_0)
        private val state = State(
            listOf(
                Storage(trait),
            )
        )

        @Test
        fun `Cannot delete a personality trait used by a character`() {
            val character = Character(CHARACTER_ID_0, personality = setOf(PERSONALITY_ID_0))
            val newState = state.updateStorage(Storage(character))

            failCanDelete(newState, CHARACTER_ID_0)
        }

        @Test
        fun `Cannot delete a personality trait used by a god`() {
            val god = God(GOD_ID_0, personality = setOf(PERSONALITY_ID_0))
            val newState = state.updateStorage(Storage(god))

            failCanDelete(newState, GOD_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(
                DeleteResult(PERSONALITY_ID_0).addId(blockingId),
                state.canDeleteCharacterTrait(PERSONALITY_ID_0)
            )
        }
    }

}