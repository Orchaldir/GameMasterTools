package at.orchaldir.gm.core.selector.character

import at.orchaldir.gm.CHARACTER_ID_1
import at.orchaldir.gm.CHARACTER_TEMPLATE_ID_0
import at.orchaldir.gm.CHARACTER_TEMPLATE_ID_1
import at.orchaldir.gm.ENCOUNTER_ID_0
import at.orchaldir.gm.RACE_LOOKUP_0
import at.orchaldir.gm.REGION_ID_0
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterTemplate
import at.orchaldir.gm.core.model.rpg.encounter.CharacterTemplateEncounter
import at.orchaldir.gm.core.model.rpg.encounter.Encounter
import at.orchaldir.gm.core.model.rpg.statblock.UseStatblockOfTemplate
import at.orchaldir.gm.core.model.world.terrain.Region
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CharacterTemplateTest {

    @Nested
    inner class CanDeleteTest {
        private val template = CharacterTemplate(CHARACTER_TEMPLATE_ID_0, race = RACE_LOOKUP_0)
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

        @Test
        fun `Cannot delete a template that is used by another template as statblock`() {
            val template1 = CharacterTemplate(
                CHARACTER_TEMPLATE_ID_1,
                race = RACE_LOOKUP_0,
                statblock = UseStatblockOfTemplate(CHARACTER_TEMPLATE_ID_0),
            )
            val newState = state.updateStorage(listOf(template, template1))

            failCanDelete(newState, CHARACTER_TEMPLATE_ID_1)
        }

        @Test
        fun `Cannot delete a template that is used by an encounter`() {
            val element = Encounter(ENCOUNTER_ID_0, entry = CharacterTemplateEncounter(CHARACTER_TEMPLATE_ID_0))
            val newState = state.updateStorage(element)

            failCanDelete(newState, ENCOUNTER_ID_0)
        }

        @Test
        fun `Cannot delete a template that is used by a regional encounter`() {
            val element = Region(REGION_ID_0, encounter = CharacterTemplateEncounter(CHARACTER_TEMPLATE_ID_0))
            val newState = state.updateStorage(element)

            failCanDelete(newState, REGION_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(
                DeleteResult(CHARACTER_TEMPLATE_ID_0).addId(blockingId),
                state.canDeleteCharacterTemplate(CHARACTER_TEMPLATE_ID_0)
            )
        }
    }

}