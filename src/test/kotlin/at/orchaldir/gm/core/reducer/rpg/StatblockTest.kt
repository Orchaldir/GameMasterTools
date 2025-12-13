package at.orchaldir.gm.core.reducer.rpg

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.UpdateAction
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.rpg.statblock.Statblock
import at.orchaldir.gm.core.model.rpg.statblock.UniqueCharacterStatblock
import at.orchaldir.gm.core.model.rpg.statblock.UseStatblockOfTemplate
import at.orchaldir.gm.core.model.rpg.statistic.Statistic
import at.orchaldir.gm.core.model.rpg.trait.CharacterTrait
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class StatblockTest {

    private val state = State(
        listOf(
            Storage(CharacterTrait(CHARACTER_TRAIT_ID_0)),
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
            val statblock = Statblock(mapOf(STATISTIC_ID_0 to 4), setOf(CHARACTER_TRAIT_ID_0))

            validateStatblock(state, statblock)
        }
    }
}