package at.orchaldir.gm.core.reducer.race

import at.orchaldir.gm.MATERIAL_ID_0
import at.orchaldir.gm.MATERIAL_ID_1
import at.orchaldir.gm.NAME
import at.orchaldir.gm.RACE_APPEARANCE_ID_0
import at.orchaldir.gm.UNKNOWN_MATERIAL_ID
import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.core.action.UpdateAction
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.FeatureColorType.Hair
import at.orchaldir.gm.core.model.character.appearance.hair.HairType
import at.orchaldir.gm.core.model.character.appearance.tail.SimpleTailShape.Cat
import at.orchaldir.gm.core.model.character.appearance.tail.SimpleTailShape.Rat
import at.orchaldir.gm.core.model.character.appearance.wing.WingsLayout
import at.orchaldir.gm.core.model.economy.material.Fur
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.core.model.economy.material.Metal
import at.orchaldir.gm.core.model.race.appearance.*
import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.prototypes.visualization.mockMaterial
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class RaceAppearanceTest {

    @Nested
    inner class UpdateTest {
        val state = State(listOf(
            Storage(listOf(
                mockMaterial(MATERIAL_ID_0, Fur(HairColorOptions())),
                mockMaterial(MATERIAL_ID_1, Metal()),
            )),
            Storage(RaceAppearance(RACE_APPEARANCE_ID_0)),
        ))

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateAction(RaceAppearance(RACE_APPEARANCE_ID_0))

            assertIllegalArgument("Requires unknown Race Appearance 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `No tail options for for a simple tail shape`() {
            val options = TailOptions(simpleShapes = OneOf(Rat))
            val action = UpdateAction(RaceAppearance(RACE_APPEARANCE_ID_0, tail = options))

            assertIllegalArgument("No options for Rat tail!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Having wings requires wing types`() {
            val options = WingOptions(layouts = OneOf(WingsLayout.Two))
            val action = UpdateAction(RaceAppearance(RACE_APPEARANCE_ID_0, wing = options))

            assertIllegalArgument("Having wings requires wing types!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Reuse hair color option requires hair`() {
            val tailOptions = TailOptions(simpleOptions = mapOf(Cat to FeatureColorOptions(Hair)))
            val action = UpdateAction(
                RaceAppearance(
                    RACE_APPEARANCE_ID_0,
                    hair = HairOptions(hairTypes = OneOf(HairType.None)),
                    tail = tailOptions,
                )
            )

            assertIllegalArgument("Tail options for Cat require hair!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Fur exists`() {
            val options = SkinOptions(fur = UNKNOWN_MATERIAL_ID)
            val action = UpdateAction(RaceAppearance(RACE_APPEARANCE_ID_0, skin = options))

            assertIllegalArgument("Requires unknown Material 99!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Fur is not a fur`() {
            val options = SkinOptions(fur = MATERIAL_ID_1)
            val action = UpdateAction(RaceAppearance(RACE_APPEARANCE_ID_0, skin = options))

            assertIllegalArgument("Material 1 is not a fur!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Valid Fur`() {
            val options = SkinOptions(fur = MATERIAL_ID_0)
            val action = UpdateAction(RaceAppearance(RACE_APPEARANCE_ID_0, skin = options))

            REDUCER.invoke(state, action)
        }

        @Test
        fun `Update is valid`() {
            val appearance = RaceAppearance(RACE_APPEARANCE_ID_0, NAME)
            val action = UpdateAction(appearance)

            assertEquals(
                appearance,
                REDUCER.invoke(state, action).first.getRaceAppearanceStorage().get(RACE_APPEARANCE_ID_0)
            )
        }
    }

}