package at.orchaldir.gm.core.reducer.rpg

import at.orchaldir.gm.STATISTIC_ID_0
import at.orchaldir.gm.UNKNOWN_STATISTIC_ID
import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.FixedHalfAndMaxRange
import at.orchaldir.gm.core.model.rpg.combat.MusclePoweredHalfAndMaxRange
import at.orchaldir.gm.core.model.rpg.combat.SimpleUsedSkill
import at.orchaldir.gm.core.model.rpg.combat.StatisticBasedHalfAndMaxRange
import at.orchaldir.gm.core.model.rpg.combat.UsedSkill
import at.orchaldir.gm.core.model.rpg.statistic.Statistic
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.DOUBLE
import at.orchaldir.gm.utils.math.ONE
import at.orchaldir.gm.utils.math.ZERO
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class UseSkillTest {

    private val STATE = State(
        listOf(
            Storage(Statistic(STATISTIC_ID_0)),
        )
    )

    @Nested
    inner class SimpleUsedSkillTest {
        @Test
        fun `Skill must exist`() {
            val skill = SimpleUsedSkill(UNKNOWN_STATISTIC_ID)

            assertInvalidSkill(skill, "Requires unknown Statistic 99!")
        }

        @Test
        fun `Modifier must greater or equal the minimum`() {
            val skill = SimpleUsedSkill(STATISTIC_ID_0, -10)

            assertInvalidSkill(skill, "Used Skill Modifier needs to be >= -2!")
        }

        @Test
        fun `Modifier must less or equal the maximum`() {
            val skill = SimpleUsedSkill(STATISTIC_ID_0, 10)

            assertInvalidSkill(skill, "Used Skill Modifier needs to be <= 2!")
        }

        @Test
        fun `Test valid skill`() {
            validateUsedSkill(STATE, SimpleUsedSkill(STATISTIC_ID_0))
        }
    }

    private fun assertInvalidSkill(skill: UsedSkill, message: String) {
        assertIllegalArgument(message) { validateUsedSkill(STATE, skill) }
    }

}