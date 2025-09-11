package at.orchaldir.gm.core.selector.culture

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.realm.War
import at.orchaldir.gm.core.model.realm.WarParticipant
import at.orchaldir.gm.core.model.util.CultureReference
import at.orchaldir.gm.core.model.util.Dead
import at.orchaldir.gm.core.model.util.KilledBy
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CultureTest {

    @Nested
    inner class CanDeleteTest {
        private val culture = Culture(CULTURE_ID_0)
        private val reference = CultureReference(CULTURE_ID_0)
        private val state = State(
            listOf(
                Storage(culture),
            )
        )

        @Test
        fun `Cannot delete a culture that with a character`() {
            val character = Character(CHARACTER_ID_0, culture = CULTURE_ID_0)
            val newState = state.updateStorage(Storage(character))

            failCanDelete(newState, CHARACTER_ID_0)
        }

        @Test
        fun `Cannot delete a culture that killed a character`() {
            val dead = Dead(DAY0, KilledBy(reference))
            val character = Character(CHARACTER_ID_0, vitalStatus = dead)
            val newState = state.updateStorage(Storage(character))

            failCanDelete(newState, CHARACTER_ID_0)
        }

        @Test
        fun `Cannot delete a culture that created another element`() {
            val building = Building(BUILDING_ID_0, builder = reference)
            val newState = state.updateStorage(Storage(building))

            failCanDelete(newState, BUILDING_ID_0)
        }

        @Test
        fun `Cannot delete a culture that participated in a war`() {
            val participant = WarParticipant(reference)
            val war = War(WAR_ID_0, participants = listOf(participant))
            val newState = state.updateStorage(Storage(war))

            failCanDelete(newState, WAR_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(DeleteResult(CULTURE_ID_0).addId(blockingId), state.canDeleteCulture(CULTURE_ID_0))
        }
    }
  
}