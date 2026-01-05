package at.orchaldir.gm.core.reducer.religion

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.UpdateAction
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.religion.Domain
import at.orchaldir.gm.core.model.religion.God
import at.orchaldir.gm.core.model.rpg.trait.CharacterTrait
import at.orchaldir.gm.core.model.rpg.trait.CharacterTraitType
import at.orchaldir.gm.core.model.util.Alive
import at.orchaldir.gm.core.model.util.MaskOfOtherGod
import at.orchaldir.gm.core.model.util.VitalStatusType
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.core.reducer.util.testAllowedVitalStatusTypes
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GodTest {

    private val god0 = God(GOD_ID_0)
    private val state = State(
        listOf(
            Storage(CALENDAR0),
            Storage(Domain(DOMAIN_ID_0)),
            Storage(god0),
            Storage(CharacterTrait(CHARACTER_TRAIT_ID_0)),
        )
    )

    @Nested
    inner class UpdateTest {

        @Test
        fun `Test allowed vital status types`() {
            testAllowedVitalStatusTypes(
                state,
                mapOf(
                    VitalStatusType.Abandoned to false,
                    VitalStatusType.Alive to true,
                    VitalStatusType.Closed to false,
                    VitalStatusType.Dead to true,
                    VitalStatusType.Destroyed to false,
                    VitalStatusType.Vanished to true,
                ),
            ) { status ->
                God(GOD_ID_0, status = status)
            }
        }

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateAction(God(UNKNOWN_GOD_ID))

            assertIllegalArgument("Requires unknown God 99!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot be the mask of an unknown god`() {
            val action = UpdateAction(God(GOD_ID_0, authenticity = MaskOfOtherGod(UNKNOWN_GOD_ID)))

            assertIllegalArgument("Cannot be the mask of unknown God 99!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot use an unknown domain`() {
            val action = UpdateAction(God(GOD_ID_0, domains = setOf(UNKNOWN_DOMAIN_ID)))

            assertIllegalArgument("Requires unknown Domain 99!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot use an unknown personality trait`() {
            val action = UpdateAction(God(GOD_ID_0, personality = setOf(UNKNOWN_CHARACTER_TRAIT_ID)))

            assertIllegalArgument("Requires unknown Character Trait 99!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot use a character trait that has another type`() {
            val trait = CharacterTrait(CHARACTER_TRAIT_ID_0, type = CharacterTraitType.Body)
            val newState = state.updateStorage(trait)
            val action = UpdateAction(God(GOD_ID_0, personality = setOf(CHARACTER_TRAIT_ID_0)))

            assertIllegalArgument("Character Trait 0 has type other than Personality!") {
                REDUCER.invoke(
                    newState,
                    action
                )
            }
        }

        @Test
        fun `Update a god`() {
            val god = God(
                GOD_ID_0,
                NAME,
                null,
                Gender.Genderless,
                Alive,
                setOf(CHARACTER_TRAIT_ID_0),
                setOf(DOMAIN_ID_0),
            )
            val action = UpdateAction(god)

            assertEquals(god, REDUCER.invoke(state, action).first.getGodStorage().get(GOD_ID_0))
        }
    }

}