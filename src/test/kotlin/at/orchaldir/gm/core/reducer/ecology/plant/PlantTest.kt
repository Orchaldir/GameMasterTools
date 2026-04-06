package at.orchaldir.gm.core.reducer.ecology.plant

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.UpdateAction
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.ecology.plant.Plant
import at.orchaldir.gm.core.model.ecology.plant.Tree
import at.orchaldir.gm.core.model.util.CharacterReference
import at.orchaldir.gm.core.model.util.origin.CreatedElement
import at.orchaldir.gm.core.model.util.origin.ModifiedElement
import at.orchaldir.gm.core.model.util.origin.TranslatedElement
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals



class PlantTest {

    private val plant0 = Plant(PLANT_ID_0)
    private val state = State(
        listOf(
            Storage(CALENDAR0),
            Storage(plant0),
        )
    )

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateAction(Plant(PLANT_ID_0))
            val state = state.removeStorage(PLANT_ID_0)

            assertIllegalArgument("Requires unknown Plant 0!") { REDUCER.invoke(state, action) }
        }

        @Nested
        inner class OriginTest {

            @Test
            fun `Cannot modify an unknown plant`() {
                val origin = ModifiedElement(PLANT_ID_1)
                val plant = Plant(PLANT_ID_0, origin = origin)
                val action = UpdateAction(plant)

                assertIllegalArgument("Requires unknown parent Plant 1!") {
                    REDUCER.invoke(state, action)
                }
            }

            @Test
            fun `Creator must exist`() {
                val origin = CreatedElement(CharacterReference(UNKNOWN_CHARACTER_ID))
                val plant = Plant(PLANT_ID_0, origin = origin)
                val action = UpdateAction(plant)

                assertIllegalArgument("Requires unknown Creator (Character 99)!") {
                    REDUCER.invoke(state, action)
                }
            }
        }

        @Test
        fun `Date is in the future`() {
            val action = UpdateAction(Plant(PLANT_ID_0, date = FUTURE_DAY_0))

            assertIllegalArgument("Date (Plant) is in the future!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot use unknown material`() {
            val action = UpdateAction(Plant(PLANT_ID_0, appearance = Tree(UNKNOWN_MATERIAL_ID)))

            assertIllegalArgument("Requires unknown Material 99!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Update a plant`() {
            val plant = Plant(PLANT_ID_0, NAME)
            val action = UpdateAction(plant)

            assertEquals(plant, REDUCER.invoke(state, action).first.getPlantStorage().get(PLANT_ID_0))
        }
    }

}