package at.orchaldir.gm.core.reducer.realm

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteBattle
import at.orchaldir.gm.core.action.UpdateBattle
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.realm.Battle
import at.orchaldir.gm.core.model.realm.BattleParticipant
import at.orchaldir.gm.core.model.realm.Realm
import at.orchaldir.gm.core.model.util.Dead
import at.orchaldir.gm.core.model.util.DeathInBattle
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.world.region.Battlefield
import at.orchaldir.gm.core.model.world.region.Region
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BattleTest {

    private val STATE = State(
        listOf(
            Storage(CALENDAR0),
            Storage(Realm(REALM_ID_0)),
            Storage(Battle(BATTLE_ID_0)),
        )
    )

    @Nested
    inner class DeleteTest {

        private val action = DeleteBattle(BATTLE_ID_0)

        @Test
        fun `Can delete an existing Battle`() {
            val state = State(Storage(Battle(BATTLE_ID_0)))

            assertEquals(0, REDUCER.invoke(state, action).first.getBattleStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        // see VitalStatusTest for other elements
        @Test
        fun `Cannot delete a battle that killed a character`() {
            val dead = Dead(DAY0, DeathInBattle(BATTLE_ID_0))
            val character = Character(CHARACTER_ID_0, vitalStatus = dead)
            val newState = STATE.updateStorage(Storage(character))

            assertIllegalArgument("Cannot delete Battle 0, because it is used!") {
                REDUCER.invoke(newState, action)
            }
        }

        @Test
        fun `Cannot delete a battle with a battlefield`() {
            val region = Region(REGION_ID_0, data = Battlefield(BATTLE_ID_0))
            val newState = STATE.updateStorage(Storage(region))

            assertIllegalArgument("Cannot delete Battle 0, because it is used!") {
                REDUCER.invoke(newState, action)
            }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateBattle(Battle(BATTLE_ID_0))

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `A participating realm must exist`() {
            val participant = BattleParticipant(UNKNOWN_REALM_ID)
            val action = UpdateBattle(Battle(BATTLE_ID_0, participants = listOf(participant)))

            assertIllegalArgument("Requires unknown Realm 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `A character leading the battle must exist`() {
            val participant = BattleParticipant(REALM_ID_0, UNKNOWN_CHARACTER_ID)
            val action = UpdateBattle(Battle(BATTLE_ID_0, participants = listOf(participant)))

            assertIllegalArgument("Requires unknown Character 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Update is valid`() {
            val battle = Battle(BATTLE_ID_0, Name.Companion.init("Test"))
            val action = UpdateBattle(battle)

            assertEquals(battle, REDUCER.invoke(STATE, action).first.getBattleStorage().get(BATTLE_ID_0))
        }
    }

}