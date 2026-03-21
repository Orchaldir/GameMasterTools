package at.orchaldir.gm.core.model.rpg.dice

import at.orchaldir.gm.core.model.Config
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.RpgConfig
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.security.DigestException

class NumberTest {

    private val standardType = DieType.D6
    private val otherType = DieType.D20
    private val state = State(config = Config(rpg = RpgConfig(defaultDieType = standardType)))
    // input
    private val fixed = FixedNumber(42)
    private val standard = StandardDice(2, 1)
    private val diceWithStandardType = Dice(20, standardType, 10)
    private val diceWithOtherType = Dice(200, otherType, 100)
    private val mixed = MixedDice(mapOf(standardType to 3000, otherType to 2000), 1000)
    // results
    private val fixedPlusStandard = StandardDice(2, 43)
    private val fixedPlusDice = Dice(200, otherType, 142)
    private val mixedPlusMixed = MixedDice(mixed.dice, 1042)

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
            assertEquals(mixedPlusMixed, fixed.addNumber(mixed))
        }

    }

}