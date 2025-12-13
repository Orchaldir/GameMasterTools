package at.orchaldir.gm.core.reducer.rpg

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.statblock.Statblock
import at.orchaldir.gm.core.model.rpg.statblock.StatblockUpdate
import at.orchaldir.gm.core.model.rpg.statistic.Statistic
import at.orchaldir.gm.core.model.rpg.trait.CharacterTrait
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class StatblockTest {

    private val state = State(
        listOf(
            Storage(listOf(CharacterTrait(CHARACTER_TRAIT_ID_0), CharacterTrait(CHARACTER_TRAIT_ID_1))),
            Storage(Statistic(STATISTIC_ID_0)),
        )
    )
    private val validStatblock = Statblock(mapOf(STATISTIC_ID_0 to 4), setOf(CHARACTER_TRAIT_ID_0))

    @Nested
    inner class StatblockTest {

        @Test
        fun `Using an unknown character trait`() {
            val statblock = Statblock(traits = setOf(UNKNOWN_CHARACTER_TRAIT_ID))

            assertIllegalArgument("Requires unknown Character Trait 99!") { validateStatblock(state, statblock) }
        }

        @Test
        fun `Using an unknown statistic`() {
            val statblock = Statblock(mapOf(UNKNOWN_STATISTIC_ID to 4))

            assertIllegalArgument("Requires unknown Statistic 99!") { validateStatblock(state, statblock) }
        }

        @Test
        fun `Test a valid statblock`() {
            validateStatblock(state, validStatblock)
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
        fun `Using an unknown statistic`() {
            val update = StatblockUpdate(mapOf(UNKNOWN_STATISTIC_ID to 4))

            assertIllegalArgument("Requires unknown Statistic 99!") { validate(update) }
        }

        @Test
        fun `Test a valid update`() {
            val update =
                StatblockUpdate(mapOf(STATISTIC_ID_0 to 4), setOf(CHARACTER_TRAIT_ID_1), setOf(CHARACTER_TRAIT_ID_0))

            validate(update)
        }

        private fun validate(update: StatblockUpdate) {
            validateStatblockUpdate(state, validStatblock, update)
        }

    }
}