package at.orchaldir.gm.core.selector.rpg

import at.orchaldir.gm.CHARACTER_ID_0
import at.orchaldir.gm.CHARACTER_TEMPLATE_ID_0
import at.orchaldir.gm.DAMAGE_TYPE_ID_0
import at.orchaldir.gm.JOB_ID_0
import at.orchaldir.gm.MELEE_WEAPON_ID_0
import at.orchaldir.gm.RACE_ID_0
import at.orchaldir.gm.STATISTIC_ID_0
import at.orchaldir.gm.UNKNOWN_STATISTIC_ID
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterTemplate
import at.orchaldir.gm.core.model.economy.job.Job
import at.orchaldir.gm.core.model.rpg.Statblock
import at.orchaldir.gm.core.model.rpg.UniqueCharacterStatblock
import at.orchaldir.gm.core.model.rpg.combat.Damage
import at.orchaldir.gm.core.model.rpg.combat.MeleeAttack
import at.orchaldir.gm.core.model.rpg.combat.MeleeWeapon
import at.orchaldir.gm.core.model.rpg.combat.ModifiedBaseDamage
import at.orchaldir.gm.core.model.rpg.combat.ReachRange
import at.orchaldir.gm.core.model.rpg.statistic.Statistic
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class StatisticTest {

    @Nested
    inner class CanDeleteTest {
        private val statistic = Statistic(STATISTIC_ID_0)
        private val statblock = Statblock(mapOf(STATISTIC_ID_0 to 2))
        private val state = State(
            listOf(
                Storage(statistic),
            )
        )

        @Test
        fun `Cannot delete a statistic used by a job`() {
            val element = Job(JOB_ID_0, importantStatistics = setOf(STATISTIC_ID_0))
            val newState = state.updateStorage(Storage(element))

            failCanDelete(newState, JOB_ID_0)
        }

        @Test
        fun `Cannot delete a statistic used a character`() {
            val element = Character(CHARACTER_ID_0, statblock = UniqueCharacterStatblock(statblock))
            val newState = state.updateStorage(Storage(element))

            failCanDelete(newState, CHARACTER_ID_0)
        }

        @Test
        fun `Cannot delete a statistic used a character template`() {
            val element = CharacterTemplate(CHARACTER_TEMPLATE_ID_0, race = RACE_ID_0, statblock = statblock)
            val newState = state.updateStorage(Storage(element))

            failCanDelete(newState, CHARACTER_TEMPLATE_ID_0)
        }

        @Test
        fun `Cannot delete a statistic used by a melee weapon`() {
            val amount = ModifiedBaseDamage(STATISTIC_ID_0)
            val attack = MeleeAttack(Damage(amount, DAMAGE_TYPE_ID_0))
            val element = MeleeWeapon(MELEE_WEAPON_ID_0, attacks = listOf(attack))
            val newState = state.updateStorage(Storage(element))

            failCanDelete(newState, MELEE_WEAPON_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(DeleteResult(STATISTIC_ID_0).addId(blockingId), state.canDeleteStatistic(STATISTIC_ID_0))
        }
    }

}