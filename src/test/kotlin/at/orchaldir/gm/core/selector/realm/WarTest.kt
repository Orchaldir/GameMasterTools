package at.orchaldir.gm.core.selector.settlement

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.realm.Battle
import at.orchaldir.gm.core.model.realm.War
import at.orchaldir.gm.core.model.time.holiday.Holiday
import at.orchaldir.gm.core.model.time.holiday.HolidayOfWar
import at.orchaldir.gm.core.model.util.Dead
import at.orchaldir.gm.core.model.util.DeathInWar
import at.orchaldir.gm.core.model.util.WarReference
import at.orchaldir.gm.core.model.world.terrain.Battlefield
import at.orchaldir.gm.core.model.world.terrain.Region
import at.orchaldir.gm.core.model.world.terrain.Wasteland
import at.orchaldir.gm.core.selector.realm.canDeleteWar
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class WarTest {

    @Nested
    inner class CanDeleteTest {
        private val war = War(WAR_ID_0)
        private val state = State(
            listOf(
                Storage(war),
            )
        )

        @Test
        fun `Cannot delete a war that is celebrated by a holiday`() {
            val purpose = HolidayOfWar(WAR_ID_0)
            val holiday = Holiday(HOLIDAY_ID_0, purpose = purpose)
            val newState = state.updateStorage(holiday)

            failCanDelete(newState, HOLIDAY_ID_0)
        }

        @Test
        fun `Cannot delete a war that has a battle`() {
            val battle = Battle(BATTLE_ID_0, war = WAR_ID_0)
            val newState = state.updateStorage(battle)

            failCanDelete(newState, BATTLE_ID_0)
        }

        @Test
        fun `Cannot delete a war that killed a character`() {
            val dead = Dead(DAY0, DeathInWar(WAR_ID_0))
            val character = Character(CHARACTER_ID_0, status = dead)
            val newState = state.updateStorage(character)

            failCanDelete(newState, CHARACTER_ID_0)
        }

        @Test
        fun `Cannot delete a war with a battlefield`() {
            val region = Region(REGION_ID_0, data = Battlefield(WarReference(WAR_ID_0)))
            val newState = state.updateStorage(region)

            failCanDelete(newState, REGION_ID_0)
        }

        @Test
        fun `Cannot delete a war that caused a wasteland`() {
            val region = Region(REGION_ID_0, data = Wasteland(WarReference(WAR_ID_0)))
            val newState = state.updateStorage(region)

            failCanDelete(newState, REGION_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(DeleteResult(WAR_ID_0).addId(blockingId), state.canDeleteWar(WAR_ID_0))
        }
    }

}