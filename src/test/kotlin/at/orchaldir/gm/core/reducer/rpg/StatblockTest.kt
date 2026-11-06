package at.orchaldir.gm.core.reducer.rpg

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterTemplate
import at.orchaldir.gm.core.model.rpg.statblock.*
import at.orchaldir.gm.core.model.rpg.statistic.RelativeStatisticLimit
import at.orchaldir.gm.core.model.rpg.statistic.Statistic
import at.orchaldir.gm.core.model.rpg.statistic.StatisticLimitLayer
import at.orchaldir.gm.core.model.rpg.trait.CharacterTrait
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.RangeInt
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class StatblockTest {

    private val validStatblock = Statblock(mapOf(STATISTIC_ID_0 to 2), setOf(CHARACTER_TRAIT_ID_0))
    private val validUpdate = StatblockUpdate(
        mapOf(STATISTIC_ID_0 to 1),
        setOf(CHARACTER_TRAIT_ID_1),
        setOf(CHARACTER_TRAIT_ID_0),
    )
    val statistic = Statistic(
        STATISTIC_ID_0,
        raceLimit = RelativeStatisticLimit(RangeInt(-2, 2)),
        characterLimit = RelativeStatisticLimit(RangeInt(-1, 1)),
    )
    private val state = State(
        listOf(
            Storage(
                CharacterTemplate(
                    CHARACTER_TEMPLATE_ID_0,
                    race = RACE_LOOKUP_0,
                    statblock = UniqueStatblock(StatblockUpdate(validStatblock))
                )
            ),
            Storage(listOf(CharacterTrait(CHARACTER_TRAIT_ID_0), CharacterTrait(CHARACTER_TRAIT_ID_1))),
            Storage(statistic),
        )
    )

    @Nested
    inner class StatblockTest {

        @Test
        fun `Using an unknown character trait`() {
            val statblock = Statblock(traits = setOf(UNKNOWN_CHARACTER_TRAIT_ID))

            assertIllegalArgument("Requires unknown Character Trait 99!") { validate(statblock) }
        }

        @Test
        fun `Using an unknown statistic`() {
            val statblock = Statblock(mapOf(UNKNOWN_STATISTIC_ID to 4))

            assertIllegalArgument("Requires unknown Statistic 99!") { validate(statblock) }
        }

        @Test
        fun `Using a too high value for a statistic`() {
            val statblock = Statblock(mapOf(STATISTIC_ID_0 to 3))

            assertIllegalArgument("The value (3) for Statistic 0 is too large!") { validate(statblock) }
        }

        @Test
        fun `Using a too low value for a statistic`() {
            val statblock = Statblock(mapOf(STATISTIC_ID_0 to -3))

            assertIllegalArgument("The value (-3) for Statistic 0 is too small!") { validate(statblock) }
        }

        @Test
        fun `Test a valid statblock`() {
            validate(-2)
            validate(-1)
            validate(0)
            validate(+1)
            validate(+2)
        }

        private fun validate(value: Int) {
            val statblock = Statblock(mapOf(STATISTIC_ID_0 to value))

            validate(statblock)
        }

        private fun validate(statblock: Statblock) {
            validateStatblock(state, statblock, StatisticLimitLayer.Race)
        }
    }

    @Nested
    inner class StatblockUpdateTest {

        @Test
        fun `Adding an unknown character trait`() {
            val update = StatblockUpdate(addedTraits = setOf(UNKNOWN_CHARACTER_TRAIT_ID))

            assertIllegalArgument("Requires unknown Character Trait 99!") { validate(update) }
        }

        @Test
        fun `Removing an unknown character trait`() {
            val update = StatblockUpdate(removedTraits = setOf(UNKNOWN_CHARACTER_TRAIT_ID))

            assertIllegalArgument("Requires unknown Character Trait 99!") { validate(update) }
        }

        @Test
        fun `Cannot add & remove the same character trait`() {
            val sameTrait = setOf(CHARACTER_TRAIT_ID_0)
            val update = StatblockUpdate(addedTraits = sameTrait, removedTraits = sameTrait)

            assertIllegalArgument("Cannot add & remove Character Trait 0!") { validate(update) }
        }

        @Test
        fun `Cannot add the same character trait again`() {
            val sameTrait = setOf(CHARACTER_TRAIT_ID_0)
            val update = StatblockUpdate(addedTraits = sameTrait)

            assertIllegalArgument("Cannot add Character Trait 0 again!") { validate(update) }
        }

        @Test
        fun `Cannot remove a character trait not in the statblock`() {
            val update = StatblockUpdate(removedTraits = setOf(CHARACTER_TRAIT_ID_1))

            assertIllegalArgument("Cannot remove Character Trait 1, because it is not in the statblock!") {
                validate(update)
            }
        }

        @Test
        fun `Using an unknown statistic`() {
            val update = StatblockUpdate(mapOf(UNKNOWN_STATISTIC_ID to 4))

            assertIllegalArgument("Requires unknown Statistic 99!") { validate(update) }
        }

        @Test
        fun `Using a too high value for a statistic`() {
            val update = StatblockUpdate(mapOf(STATISTIC_ID_0 to 2))

            assertIllegalArgument("The value (2) for Statistic 0 is too large!") { validate(update) }
        }

        @Test
        fun `Using a too low value for a statistic`() {
            val update = StatblockUpdate(mapOf(STATISTIC_ID_0 to -2))

            assertIllegalArgument("The value (-2) for Statistic 0 is too small!") { validate(update) }
        }

        @Test
        fun `Test a valid update`() {
            validate(-1)
            validate(0)
            validate(+1)
        }

        private fun validate(value: Int) {
            val update = StatblockUpdate(mapOf(STATISTIC_ID_0 to value))

            validate(update)
        }

        private fun validate(update: StatblockUpdate) {
            validateStatblockUpdate(state, validStatblock, update)
        }

    }

    @Nested
    inner class StatblockLookupTest {

        @Test
        fun `Using an unknown template`() {
            val statblock = UseStatblockOfTemplate(UNKNOWN_CHARACTER_TEMPLATE_ID)

            assertIllegalArgument("Requires unknown Character Template 99!") { validate(statblock) }
        }

        @Test
        fun `Modifying an unknown template`() {
            val statblock = ModifyStatblockOfTemplate(UNKNOWN_CHARACTER_TEMPLATE_ID, StatblockUpdate())

            assertIllegalArgument("Requires unknown Character Template 99!") { validate(statblock) }
        }

        @Test
        fun `Using an unknown statistic`() {
            val update = StatblockUpdate(mapOf(UNKNOWN_STATISTIC_ID to 4))
            val statblock = UniqueStatblock(update)

            assertIllegalArgument("Requires unknown Statistic 99!") { validate(statblock) }
        }

        @Test
        fun `Modifying an unknown statistic`() {
            val update = StatblockUpdate(mapOf(UNKNOWN_STATISTIC_ID to 4))
            val statblock = ModifyStatblockOfTemplate(CHARACTER_TEMPLATE_ID_0, update)

            assertIllegalArgument("Requires unknown Statistic 99!") { validate(statblock) }
        }

        @Test
        fun `Using a valid character template`() {
            val statblock = UseStatblockOfTemplate(CHARACTER_TEMPLATE_ID_0)

            validate(statblock)
        }

        @Test
        fun `Modifying a valid character template`() {
            val statblock = ModifyStatblockOfTemplate(CHARACTER_TEMPLATE_ID_0, validUpdate)

            validate(statblock)
        }

        private fun validate(statblock: StatblockLookup) {
            validateStatblockLookup(state, Statblock(), statblock)
        }
    }
}