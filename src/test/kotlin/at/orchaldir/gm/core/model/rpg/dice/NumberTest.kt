package at.orchaldir.gm.core.model.rpg.dice

import at.orchaldir.gm.core.model.Config
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.RpgConfig
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class NumberTest {

    private val standardType = DieType.D6
    private val otherType = DieType.D20
    private val state = State(config = Config(rpg = RpgConfig(defaultDieType = standardType)))
    // input
    private val fixed = FixedNumber(42)
    private val standard = StandardDice(3, 1)
    private val diceWithStandardType = Dice(30, standardType, 10)
    private val diceWithOtherType = Dice(300, otherType, 100)
    private val mixed = MixedDice(mapOf(standardType to 4000, otherType to 3000), 1000)
    // results
    private val fixedPlusStandard = StandardDice(3, 43)
    private val fixedPlusDice = Dice(300, otherType, 142)
    private val fixedPlusMixed = MixedDice(mixed.dice, 1042)
    private val standardPlusWithStandard = StandardDice(33, 11)
    private val standardPlusWithOther = MixedDice(mapOf(standardType to 3, otherType to 300), 101)
    private val standardPlusMixed = MixedDice(mapOf(standardType to 4003, otherType to 3000), 1001)

    @Nested
    inner class FixedNumberTest {

        @Test
        fun `Add fixed number`() {
            assertEquals(FixedNumber(84), fixed.addNumber(fixed))
        }

        @Test
        fun `Add standard dice`() {
            assertEquals(fixedPlusStandard, fixed.addNumber(standard))
        }

        @Test
        fun `Add dice`() {
            assertEquals(fixedPlusDice, fixed.addNumber(diceWithOtherType))
        }

        @Test
        fun `Add mixed dice`() {
            assertEquals(fixedPlusMixed, fixed.addNumber(mixed))
        }

    }

    @Nested
    inner class StandardDiceTest {

        @Test
        fun `Add fixed number`() {
            assertEquals(fixedPlusStandard, standard.addNumber(state, fixed))
        }

        @Test
        fun `Add standard dice`() {
            assertEquals(StandardDice(6, 2), standard.addNumber(state, standard))
        }

        @Test
        fun `Add dice with standard type`() {
            assertEquals(standardPlusWithStandard, standard.addNumber(state, diceWithStandardType))
        }

        @Test
        fun `Add dice with other type`() {
            assertEquals(standardPlusWithOther, standard.addNumber(state, diceWithOtherType))
        }

        @Test
        fun `Add mixed dice`() {
            assertEquals(standardPlusMixed, standard.addNumber(state, mixed))
        }

    }

}