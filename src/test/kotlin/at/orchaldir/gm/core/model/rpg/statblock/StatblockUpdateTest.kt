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
    inner class CalculateUpdateCostTest {

        @Test
        fun `Cost of empty update`() {
            assertCost(Statblock(), 0)
        }

        @Test
        fun `Cost of multiple statistics`() {
            assertCost(Statblock(statistics), 55)
        }

        @Test
        fun `Cost of multiple added traits`() {
            assertCost(Statblock(traits = traits), 10)
        }

        private fun assertCost(resolved: Statblock, cost: Int) {
            assertEquals(cost, calculateUpdateCost(state, Statblock(), resolved))
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

    @Nested
    inner class ApplyToTest {

        @Test
        fun `Add a statistic`() {
            val statblock = Statblock(mapOf(STATISTIC_ID_0 to 3))
            val update = StatblockUpdate(mapOf(STATISTIC_ID_1 to 4))
            val result = update.applyTo(statblock)

            assertEquals( statistics, result.statistics)
            assertEquals( emptySet(), result.traits)
        }

        @Test
        fun `Modify a statistic`() {
            val statblock = Statblock(mapOf(STATISTIC_ID_0 to 3))
            val update = StatblockUpdate(mapOf(STATISTIC_ID_0 to 4))
            val result = update.applyTo(statblock)

            assertEquals( mapOf(STATISTIC_ID_0 to 7), result.statistics)
            assertEquals( emptySet(), result.traits)
        }

        @Test
        fun `Add a trait`() {
            val statblock = Statblock(traits = setOf(CHARACTER_TRAIT_ID_0))
            val update = StatblockUpdate(addedTraits = setOf(CHARACTER_TRAIT_ID_1))
            val result = update.applyTo(statblock)

            assertEquals( emptyMap(), result.statistics)
            assertEquals( traits, result.traits)
        }

        @Test
        fun `Remove a trait`() {
            val statblock = Statblock(traits = traits)
            val update = StatblockUpdate(removedTraits = setOf(CHARACTER_TRAIT_ID_1))
            val result = update.applyTo(statblock)

            assertEquals( emptyMap(), result.statistics)
            assertEquals( setOf(CHARACTER_TRAIT_ID_0), result.traits)
        }
    }

}