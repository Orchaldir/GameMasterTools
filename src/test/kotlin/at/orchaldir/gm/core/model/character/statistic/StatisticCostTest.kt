package at.orchaldir.gm.core.model.character.statistic

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class StatisticCostTest {

    @Nested
    inner class CalculateCostTest {

        @Test
        fun `The cost of an undefined cost is always 0`() {
            val cost = UndefinedStatisticCost

            assertEquals(0, cost.calculate(-1))
            assertEquals(0, cost.calculate(0))
            assertEquals(0, cost.calculate(1))
        }

        @Test
        fun `The cost of an fixed cost`() {
            val cost = FixedStatisticCost(20)

            assertEquals(-20, cost.calculate(-1))
            assertEquals(0, cost.calculate(0))
            assertEquals(20, cost.calculate(1))
        }

        @Test
        fun `The cost of a GURPS skill`() {
            val cost = GurpsSkillCost

            assertEquals(0, cost.calculate(-1))
            assertEquals(0, cost.calculate(0))
            assertEquals(1, cost.calculate(1))
            assertEquals(2, cost.calculate(2))
            assertEquals(4, cost.calculate(3))
            assertEquals(8, cost.calculate(4))
            assertEquals(12, cost.calculate(5))
        }
    }

}