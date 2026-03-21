package at.orchaldir.gm.core.model.rpg.dice

import at.orchaldir.gm.core.model.Config
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.RpgConfig
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class RandomNumberTest {

    private val standardType = DieType.D6
    private val otherType = DieType.D20
    private val state = State(config = Config(rpg = RpgConfig(defaultDieType = standardType)))
    // input
    private val notRandom = NotRandomNumber(42)
    private val standard = StandardDice(3, 1)
    private val diceWithStandardType = Dice(30, standardType, 10)
    private val diceWithOtherType = Dice(300, otherType, 100)
    private val mixed = MixedDice(mapOf(standardType to 4000, otherType to 3000), 1000)
    // results
    private val notRandomPlusStandard = StandardDice(3, 43)
    private val notRandomPlusDice = Dice(300, otherType, 142)
    private val notRandomPlusMixed = MixedDice(mixed.dice, 1042)
    private val standardPlusWithStandard = StandardDice(33, 11)
    private val standardPlusWithOther = MixedDice(mapOf(standardType to 3, otherType to 300), 101)
    private val standardPlusMixed = MixedDice(mapOf(standardType to 4003, otherType to 3000), 1001)
    private val dicePlusMixed = MixedDice(mapOf(standardType to 4000, otherType to 3300), 1100)

    @Nested
    inner class NotRandomNumberTest {

        @Test
        fun `Add not random number`() {
            assertEquals(NotRandomNumber(84), notRandom.addNumber(notRandom))
        }

        @Test
        fun `Add standard dice`() {
            assertEquals(notRandomPlusStandard, notRandom.addNumber(standard))
        }

        @Test
        fun `Add dice`() {
            assertEquals(notRandomPlusDice, notRandom.addNumber(diceWithOtherType))
        }

        @Test
        fun `Add mixed dice`() {
            assertEquals(notRandomPlusMixed, notRandom.addNumber(mixed))
        }

    }

    @Nested
    inner class StandardDiceTest {

        @Test
        fun `Add not random number`() {
            assertEquals(notRandomPlusStandard, standard.addNumber(state, notRandom))
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

    @Nested
    inner class DiceTest {

        @Test
        fun `Add not random number`() {
            assertEquals(notRandomPlusDice, diceWithOtherType.addNumber(state, notRandom))
        }

        @Test
        fun `Add standard dice`() {
            assertEquals(standardPlusWithOther, diceWithOtherType.addNumber(state, standard))
        }

        @Test
        fun `Add dice with same type`() {
            assertEquals(Dice(600, otherType, 200), diceWithOtherType.addNumber(state, diceWithOtherType))
        }

        @Test
        fun `Add dice with other type`() {
            val expected = MixedDice(mapOf(standardType to 30, otherType to 300), 110)

            assertEquals(expected, diceWithOtherType.addNumber(state, diceWithStandardType))
        }

        @Test
        fun `Add mixed dice`() {
            assertEquals(dicePlusMixed, diceWithOtherType.addNumber(state, mixed))
        }

    }

    @Nested
    inner class MixedDiceTest {

        @Test
        fun `Add not random number`() {
            assertEquals(notRandomPlusMixed, mixed.addNumber(state, notRandom))
        }

        @Test
        fun `Add standard dice`() {
            assertEquals(standardPlusMixed, mixed.addNumber(state, standard))
        }

        @Test
        fun `Add dice`() {
            assertEquals(dicePlusMixed, mixed.addNumber(state, diceWithOtherType))
        }

        @Test
        fun `Add mixed dice`() {
            val input = MixedDice(mapOf(standardType to 500, DieType.D100 to 50), 5)
            val result = MixedDice(mapOf(standardType to 4500, otherType to 3000, DieType.D100 to 50), 1005)

            assertEquals(result, mixed.addNumber(state, input))
        }

    }

}