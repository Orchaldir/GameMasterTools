package at.orchaldir.gm.core.selector.realm

import at.orchaldir.gm.BATTLE_ID_0
import at.orchaldir.gm.CHARACTER_ID_0
import at.orchaldir.gm.DAY0
import at.orchaldir.gm.REGION_ID_0
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.realm.Battle
import at.orchaldir.gm.core.model.util.Dead
import at.orchaldir.gm.core.model.util.DeathInBattle
import at.orchaldir.gm.core.model.world.terrain.Battlefield
import at.orchaldir.gm.core.model.world.terrain.Region
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class BattleTest {

    @Nested
    inner class CanDeleteTest {
        private val battle = Battle(BATTLE_ID_0)
        private val state = State(
            listOf(
                Storage(battle),
            )
        )

        @Test
        fun `Cannot delete a battle that killed a character`() {
            val dead = Dead(DAY0, DeathInBattle(BATTLE_ID_0))
            val character = Character(CHARACTER_ID_0, vitalStatus = dead)
            val newState = state.updateStorage(Storage(character))

            failCanDelete(newState, CHARACTER_ID_0)
        }

        @Test
        fun `Cannot delete a battle with a battlefield`() {
            val region = Region(REGION_ID_0, data = Battlefield(BATTLE_ID_0))
            val newState = state.updateStorage(Storage(region))

            failCanDelete(newState, REGION_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(DeleteResult(BATTLE_ID_0).addId(blockingId), state.canDeleteBattle(BATTLE_ID_0))
        }
    }

}