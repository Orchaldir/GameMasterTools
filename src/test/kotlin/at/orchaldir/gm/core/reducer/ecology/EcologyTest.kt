package at.orchaldir.gm.core.reducer.ecology

import at.orchaldir.gm.PLANT_ID_0
import at.orchaldir.gm.UNKNOWN_PLANT_ID
import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.ecology.Ecology
import at.orchaldir.gm.core.model.ecology.EcologyWithRarity
import at.orchaldir.gm.core.model.ecology.EcologyWithSets
import at.orchaldir.gm.core.model.ecology.plant.Plant
import at.orchaldir.gm.core.model.util.SomeOf
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class EcologyTest {
    private val state = State(
        Storage(Plant(PLANT_ID_0)),
    )

    @Nested
    inner class EcologyWithSetsTest {

        @Test
        fun `Ecology is valid`() {
            EcologyWithSets(setOf(PLANT_ID_0)).validate(state)
        }

        @Test
        fun `Has unknown plant`() {
            assertEcology(
                EcologyWithSets(setOf(UNKNOWN_PLANT_ID)),
                "Requires unknown Plant 99!",
            )
        }

    }

    @Nested
    inner class EcologyWithRarityTest {

        @Test
        fun `Ecology is valid`() {
            EcologyWithRarity(SomeOf(PLANT_ID_0)).validate(state)
        }

        @Test
        fun `Has unknown plant`() {
            assertEcology(
                EcologyWithRarity(SomeOf(UNKNOWN_PLANT_ID)),
                "Requires unknown Plant 99!",
            )
        }

    }


    private fun assertEcology(ecology: Ecology, message: String) {
        assertIllegalArgument(message) {
            ecology.validate(state)
        }
    }

}