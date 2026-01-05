package at.orchaldir.gm.core.selector.character

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.organization.Organization
import at.orchaldir.gm.core.model.realm.Battle
import at.orchaldir.gm.core.model.realm.BattleParticipant
import at.orchaldir.gm.core.model.realm.Treaty
import at.orchaldir.gm.core.model.realm.TreatyParticipant
import at.orchaldir.gm.core.model.util.CharacterReference
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.util.Reference
import at.orchaldir.gm.core.model.util.SecretIdentity
import at.orchaldir.gm.core.model.util.origin.BornElement
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CharacterTest {

    @Nested
    inner class CanDeleteTest {
        private val character = Character(CHARACTER_ID_0)
        private val state = State(
            listOf(
                Storage(character),
            )
        )

        @Test
        fun `Cannot delete a character that created another element`() {
            val building = Building(BUILDING_ID_0, builder = CharacterReference(CHARACTER_ID_0))
            val newState = state.updateStorage(building)

            failCanDelete(newState, BUILDING_ID_0)
        }

        @Test
        fun `Cannot delete a character that owns another element`() {
            val ownership = History<Reference>(CharacterReference(CHARACTER_ID_0))
            val building = Building(BUILDING_ID_0, ownership = ownership)
            val newState = state.updateStorage(building)

            failCanDelete(newState, BUILDING_ID_0)
        }

        @Test
        fun `Cannot delete a member of an organization`() {
            val organization = Organization(ORGANIZATION_ID_0, members = mapOf(CHARACTER_ID_0 to History(0)))
            val newState = state.updateStorage(organization)

            failCanDelete(newState, ORGANIZATION_ID_0)
        }

        @Test
        fun `Cannot delete a character that signed a treaty`() {
            val participant = TreatyParticipant(REALM_ID_0, CHARACTER_ID_0)
            val treaty = Treaty(TREATY_ID_0, participants = listOf(participant))
            val newState = state.updateStorage(treaty)

            failCanDelete(newState, TREATY_ID_0)
        }

        @Test
        fun `Cannot delete a character that led a battle`() {
            val participant = BattleParticipant(REALM_ID_0, CHARACTER_ID_0)
            val battle = Battle(BATTLE_ID_0, participants = listOf(participant))
            val newState = state.updateStorage(battle)

            failCanDelete(newState, BATTLE_ID_0)
        }

        @Test
        fun `Cannot delete a character that has a secret identity`() {
            val identity = Character(CHARACTER_ID_1, authenticity = SecretIdentity(CHARACTER_ID_0))
            val newState = state.updateStorage(Storage(listOf(character, identity)))

            failCanDelete(newState, CHARACTER_ID_1)
        }

        @Test
        fun `Cannot delete a father`() {
            val child = Character(CHARACTER_ID_1, origin = BornElement(father = 0))
            val newState = state.updateStorage(Storage(listOf(character, child)))

            failCanDelete(newState, CHARACTER_ID_1)
        }

        @Test
        fun `Cannot delete a mother`() {
            val child = Character(CHARACTER_ID_1, origin = BornElement(mother = 0))
            val newState = state.updateStorage(Storage(listOf(character, child)))

            failCanDelete(newState, CHARACTER_ID_1)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(DeleteResult(CHARACTER_ID_0).addId(blockingId), state.canDeleteCharacter(CHARACTER_ID_0))
        }
    }

}