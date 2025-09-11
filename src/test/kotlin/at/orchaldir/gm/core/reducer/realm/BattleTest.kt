package at.orchaldir.gm.core.reducer.realm

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.UpdateBattle
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.Battle
import at.orchaldir.gm.core.model.realm.BattleParticipant
import at.orchaldir.gm.core.model.realm.Realm
import at.orchaldir.gm.core.model.util.name.Name
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