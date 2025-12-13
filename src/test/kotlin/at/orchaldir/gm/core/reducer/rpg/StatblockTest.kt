package at.orchaldir.gm.core.reducer.rpg

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterTemplate
import at.orchaldir.gm.core.model.rpg.statblock.*
import at.orchaldir.gm.core.model.rpg.statistic.Statistic
import at.orchaldir.gm.core.model.rpg.trait.CharacterTrait
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class StatblockTest {

    private val validStatblock = Statblock(mapOf(STATISTIC_ID_0 to 4), setOf(CHARACTER_TRAIT_ID_0))
    private val validUpdate =
        StatblockUpdate(mapOf(STATISTIC_ID_0 to 4), setOf(CHARACTER_TRAIT_ID_1), setOf(CHARACTER_TRAIT_ID_0))
    private val state = State(
        listOf(
            Storage(
                CharacterTemplate(
                    CHARACTER_TEMPLATE_ID_0,
                    race = RACE_ID_0,
                    statblock = UniqueStatblock(validStatblock)
                )
            ),
            Storage(listOf(CharacterTrait(CHARACTER_TRAIT_ID_0), CharacterTrait(CHARACTER_TRAIT_ID_1))),
            Storage(Statistic(STATISTIC_ID_0)),
        )
    )

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
        fun `Cannot add & remove the same character trait`() {
            val sameTrait = setOf(CHARACTER_TRAIT_ID_0)
            val update = StatblockUpdate(addedTraits = sameTrait, removedTraits = sameTrait)

            assertIllegalArgument("Cannot add & remove Character Trait 0!") { validate(update) }
        }

        @Test
        fun `Cannot remove a character trait not in the statblock`() {
            val update = StatblockUpdate(removedTraits = setOf(CHARACTER_TRAIT_ID_1))

            assertIllegalArgument("Cannot remove Character Trait 1, because it is not in the statblock!") {
                validate(
                    update
                )
            }
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
            validateStatblockLookup(state, statblock)
        }
    }
}