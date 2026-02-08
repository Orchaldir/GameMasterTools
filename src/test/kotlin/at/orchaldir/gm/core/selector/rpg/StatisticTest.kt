package at.orchaldir.gm.core.selector.rpg

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterTemplate
import at.orchaldir.gm.core.model.economy.job.Job
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.rpg.combat.*
import at.orchaldir.gm.core.model.rpg.statblock.StatblockUpdate
import at.orchaldir.gm.core.model.rpg.statblock.UniqueStatblock
import at.orchaldir.gm.core.model.rpg.statistic.Statistic
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.DOUBLE
import at.orchaldir.gm.utils.math.ONE
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class StatisticTest {

    @Nested
    inner class CanDeleteTest {
        private val statistic = Statistic(STATISTIC_ID_0)
        private val statblock = UniqueStatblock(StatblockUpdate(mapOf(STATISTIC_ID_0 to 2)))
        private val state = State(
            listOf(
                Storage(Race(RACE_ID_0)),
                Storage(statistic),
            )
        )

        @Test
        fun `Cannot delete a statistic used by a job`() {
            val element = Job(JOB_ID_0, importantStatistics = setOf(STATISTIC_ID_0))
            val newState = state.updateStorage(element)

            failCanDelete(newState, JOB_ID_0)
        }

        @Test
        fun `Cannot delete a statistic used a character`() {
            val element = Character(CHARACTER_ID_0, statblock = statblock)
            val newState = state.updateStorage(element)

            failCanDelete(newState, CHARACTER_ID_0)
        }

        @Test
        fun `Cannot delete a statistic used a character template`() {
            val element = CharacterTemplate(CHARACTER_TEMPLATE_ID_0, race = RACE_ID_0, statblock = statblock)
            val newState = state.updateStorage(element)

            failCanDelete(newState, CHARACTER_TEMPLATE_ID_0)
        }

        @Test
        fun `Cannot delete a statistic used by a melee weapon's damage`() {
            val amount = StatisticBasedDamage(STATISTIC_ID_0)
            val attack = MeleeAttack(Damage(amount, DAMAGE_TYPE_ID_0))
            val element = MeleeWeaponType(MELEE_WEAPON_TYPE_ID_0, attacks = listOf(attack))
            val newState = state.updateStorage(element)

            failCanDelete(newState, MELEE_WEAPON_TYPE_ID_0)
        }

        @Test
        fun `Cannot delete a statistic used by a melee weapon's used skill`() {
            val attack = MeleeAttack(skill = ModifiedUsedSkill(STATISTIC_ID_0))
            val element = MeleeWeaponType(MELEE_WEAPON_TYPE_ID_0, attacks = listOf(attack))
            val newState = state.updateStorage(element)

            failCanDelete(newState, MELEE_WEAPON_TYPE_ID_0)
        }

        @Test
        fun `Cannot delete a statistic used by a ranged weapon's damage`() {
            val amount = StatisticBasedDamage(STATISTIC_ID_0)
            val attack = RangedAttack(effect = Damage(amount, DAMAGE_TYPE_ID_0))
            val element = RangedWeaponType(RANGED_WEAPON_TYPE_ID_0, attacks = listOf(attack))
            val newState = state.updateStorage(element)

            failCanDelete(newState, RANGED_WEAPON_TYPE_ID_0)
        }

        @Test
        fun `Cannot delete a statistic used by a ranged weapon's range`() {
            val range = StatisticBasedHalfAndMaxRange(STATISTIC_ID_0, ONE, DOUBLE)
            val attack = RangedAttack(range = range)
            val element = RangedWeaponType(RANGED_WEAPON_TYPE_ID_0, attacks = listOf(attack))
            val newState = state.updateStorage(element)

            failCanDelete(newState, RANGED_WEAPON_TYPE_ID_0)
        }

        @Test
        fun `Cannot delete a statistic used by a ranged weapon's used skill`() {
            val attack = RangedAttack(skill = ModifiedUsedSkill(STATISTIC_ID_0))
            val element = RangedWeaponType(RANGED_WEAPON_TYPE_ID_0, attacks = listOf(attack))
            val newState = state.updateStorage(element)

            failCanDelete(newState, RANGED_WEAPON_TYPE_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(DeleteResult(STATISTIC_ID_0).addId(blockingId), state.canDeleteStatistic(STATISTIC_ID_0))
        }
    }

}