package at.orchaldir.gm.core.model.rpg.statblock

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.statistic.*
import at.orchaldir.gm.core.model.rpg.trait.CharacterTrait
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class StatblockUpdateTest {

    private val data0 = Attribute(FixedNumber(10), FixedStatisticCost(5))
    private val data1 = Attribute(cost = FixedStatisticCost(10))
    private val attribute0 = Statistic(STATISTIC_ID_0, data = data0)
    private val attribute1 = Statistic(STATISTIC_ID_1, data = data1)
    private val statistics = mapOf(STATISTIC_ID_0 to 3, STATISTIC_ID_1 to 4)
    private val trait0 = CharacterTrait(CHARACTER_TRAIT_ID_0, cost = 15)
    private val trait1 = CharacterTrait(CHARACTER_TRAIT_ID_1, cost = -5)
    private val traits = setOf(CHARACTER_TRAIT_ID_0, CHARACTER_TRAIT_ID_1)
    private val state = State(
        listOf(
            Storage(listOf(attribute0, attribute1)),
            Storage(listOf(trait0, trait1)),
        )
    )

    @Nested
    inner class CalculateCostTest {

        @Test
        fun `Cost of unknown statistic`() {
            val update = StatblockUpdate(mapOf(UNKNOWN_STATISTIC_ID to 5))

            assertIllegalArgument("Requires unknown Statistic 99!") { update.calculateCost(state) }
        }

        @Test
        fun `Cost of unknown added character trait`() {
            val update = StatblockUpdate(addedTraits = setOf(UNKNOWN_CHARACTER_TRAIT_ID))

            assertIllegalArgument("Requires unknown Character Trait 99!") { update.calculateCost(state) }
        }

        @Test
        fun `Cost of unknown removed character trait`() {
            val update = StatblockUpdate(addedTraits = setOf(UNKNOWN_CHARACTER_TRAIT_ID))

            assertIllegalArgument("Requires unknown Character Trait 99!") { update.calculateCost(state) }
        }

        @Test
        fun `Cost of empty update`() {
            val update = StatblockUpdate()

            assertEquals(0, update.calculateCost(state))
        }

        @Test
        fun `Cost of multiple statistics`() {
            val update = StatblockUpdate(statistics)

            assertEquals(55, update.calculateCost(state))
        }

        @Test
        fun `Cost of multiple added traits`() {
            val update = StatblockUpdate(addedTraits = traits)

            assertEquals(10, update.calculateCost(state))
        }

        @Test
        fun `Cost of multiple removed traits`() {
            val update = StatblockUpdate(removedTraits = traits)

            assertEquals(-10, update.calculateCost(state))
        }
    }

    @Test
    fun `Construct update from statblock`() {
        val statblock = Statblock(statistics, traits)
        val update = StatblockUpdate(statblock)

        assertEquals( statistics, update.statistics)
        assertEquals( traits, update.addedTraits)
        assertEquals( emptySet(), update.removedTraits)
    }

    @Nested
    inner class ContainsStatisticTest {

        @Test
        fun `Contains added trait`() {
            val update = StatblockUpdate(addedTraits = setOf(CHARACTER_TRAIT_ID_0))

            assertTrue(update.contains(CHARACTER_TRAIT_ID_0))
            assertFalse(update.contains(CHARACTER_TRAIT_ID_1))
        }

        @Test
        fun `Contains removed trait`() {
            val update = StatblockUpdate(removedTraits = setOf(CHARACTER_TRAIT_ID_0))

            assertTrue(update.contains(CHARACTER_TRAIT_ID_0))
            assertFalse(update.contains(CHARACTER_TRAIT_ID_1))
        }

        @Test
        fun `Contains no trait`() {
            val update = StatblockUpdate()

            assertFalse(update.contains(CHARACTER_TRAIT_ID_0))
            assertFalse(update.contains(CHARACTER_TRAIT_ID_1))
        }
    }

}