package at.orchaldir.gm.core.model.rpg

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.statistic.*
import at.orchaldir.gm.core.model.rpg.trait.CharacterTrait
import at.orchaldir.gm.core.reducer.rpg.validateStatblock
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class StatblockTest {

    private val data0 = Attribute(FixedNumber(10), FixedStatisticCost(5))
    private val data1 = Attribute(cost = FixedStatisticCost(10))
    private val attribute0 = Statistic(STATISTIC_ID_0, data = data0)
    private val attribute1 = Statistic(STATISTIC_ID_1, data = data1)
    private val trait0 = CharacterTrait(CHARACTER_TRAIT_ID_0, cost = 15)
    private val trait1 = CharacterTrait(CHARACTER_TRAIT_ID_1, cost = -5)
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
            val statblock = Statblock(mapOf(UNKNOWN_STATISTIC_ID to 5))

            assertIllegalArgument("Requires unknown Statistic 99!") { statblock.calculateCost(state) }
        }

        @Test
        fun `Cost of unknown character trait`() {
            val statblock = Statblock(traits = setOf(UNKNOWN_CHARACTER_TRAIT_ID))

            assertIllegalArgument("Requires unknown Character Trait 99!") { statblock.calculateCost(state) }
        }

        @Test
        fun `Cost of empty statblock`() {
            val statblock = Statblock()

            assertEquals(0, statblock.calculateCost(state))
        }

        @Test
        fun `Cost of multiple statistics`() {
            val statblock = Statblock(mapOf(STATISTIC_ID_0 to 3, STATISTIC_ID_1 to 4))

            assertEquals(55, statblock.calculateCost(state))
        }

        @Test
        fun `Cost of multiple traits`() {
            val statblock = Statblock(traits = setOf(CHARACTER_TRAIT_ID_0, CHARACTER_TRAIT_ID_1))

            assertEquals(10, statblock.calculateCost(state))
        }
    }

    @Nested
    inner class ResolveTest {

        @Test
        fun `Resolve unknown statistic`() {
            val statblock = Statblock()

            assertIllegalArgument("Requires unknown Statistic 99!") { statblock.resolve(state, UNKNOWN_STATISTIC_ID) }
        }

        @Test
        fun `Resolve contained attribute`() {
            val statblock = Statblock(mapOf(STATISTIC_ID_0 to 2))

            assertEquals(12, statblock.resolve(state, STATISTIC_ID_0))
        }

        @Test
        fun `Resolve default value of attribute`() {
            assertEquals(10, Statblock().resolve(state, STATISTIC_ID_0))
        }

        @Test
        fun `Resolve derived attribute`() {
            assertDerivedStatistic(
                DerivedAttribute(BasedOnStatistic(STATISTIC_ID_0, -1)),
                18,
                mapOf(STATISTIC_ID_0 to 5, STATISTIC_ID_1 to 4),
            )
        }

        @Test
        fun `Resolve default value derived attribute`() {
            assertDerivedStatistic(DerivedAttribute(BasedOnStatistic(STATISTIC_ID_0, -1)), 9)
        }

        @Test
        fun `Resolve skill that is a sum`() {
            val base = SumOfValues(
                listOf(
                    BasedOnStatistic(STATISTIC_ID_0),
                    FixedNumber(20)
                )
            )

            assertDerivedStatistic(Skill(base), 36, mapOf(STATISTIC_ID_0 to 2, STATISTIC_ID_1 to 4))
        }

        @Test
        fun `Resolve skill that is a product`() {
            val base = ProductOfValues(
                listOf(
                    BasedOnStatistic(STATISTIC_ID_0),
                    FixedNumber(20)
                )
            )

            assertDerivedStatistic(Skill(base), 244, mapOf(STATISTIC_ID_0 to 2, STATISTIC_ID_1 to 4))
        }

        @Test
        fun `Resolve skill that is a division`() {
            val base = DivisionOfValues(
                BasedOnStatistic(STATISTIC_ID_0),
                FixedNumber(2),
            )

            assertDerivedStatistic(Skill(base), 9, mapOf(STATISTIC_ID_1 to 4))
        }

        @Test
        fun `Skills have no default value`() {
            assertDerivedStatistic(Skill(FixedNumber(50)), null)
        }

        private fun assertDerivedStatistic(
            data: StatisticData,
            result: Int?,
            map: Map<StatisticId, Int> = emptyMap(),
        ) {
            val derived = Statistic(STATISTIC_ID_1, data = data)
            val newState = state.updateStorage(Storage(listOf(attribute0, derived)))
            val statblock = Statblock(map)

            assertEquals(result, statblock.resolve(newState, STATISTIC_ID_1))
        }

    }

    @Nested
    inner class ValidateTest {

        @Test
        fun `Validate unknown statistic`() {
            val statblock = Statblock(mapOf(UNKNOWN_STATISTIC_ID to 5))

            assertIllegalArgument("Requires unknown Statistic 99!") { validateStatblock(state, statblock) }
        }

        @Test
        fun `Validate unknown character trait`() {
            val statblock = Statblock(traits = setOf(UNKNOWN_CHARACTER_TRAIT_ID))

            assertIllegalArgument("Requires unknown Character Trait 99!") { validateStatblock(state, statblock) }
        }

    }

}