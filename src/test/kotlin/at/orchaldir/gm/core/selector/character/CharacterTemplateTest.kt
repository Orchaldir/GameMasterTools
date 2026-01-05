package at.orchaldir.gm.core.selector.character

import at.orchaldir.gm.CHARACTER_ID_1
import at.orchaldir.gm.CHARACTER_TEMPLATE_ID_0
import at.orchaldir.gm.RACE_ID_0
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterTemplate
import at.orchaldir.gm.core.model.rpg.statblock.UseStatblockOfTemplate
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CharacterTemplateTest {

    @Nested
    inner class CanDeleteTest {
        private val template = CharacterTemplate(CHARACTER_TEMPLATE_ID_0, race = RACE_ID_0)
        private val state = State(
            listOf(
                Storage(template),
            )
        )

        @Test
        fun `Cannot delete a template that is used by a character as statblock`() {
            val element = Character(CHARACTER_ID_1, statblock = UseStatblockOfTemplate(CHARACTER_TEMPLATE_ID_0))
            val newState = state.updateStorage(element)

            failCanDelete(newState, CHARACTER_ID_1)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(
                DeleteResult(CHARACTER_TEMPLATE_ID_0).addId(blockingId),
                state.canDeleteCharacterTemplate(CHARACTER_TEMPLATE_ID_0)
            )
        }
    }

}