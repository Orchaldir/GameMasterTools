package at.orchaldir.gm.core.reducer.ecology.plant

import at.orchaldir.gm.MATERIAL_ID_0
import at.orchaldir.gm.UNKNOWN_MATERIAL_ID
import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.ecology.plant.Tree
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test


class PlantAppearanceTest {

    private val state = State(
        listOf(
            Storage(Material(MATERIAL_ID_0)),
        )
    )

    @Nested
    inner class TreeTest {

        @Test
        fun `Cannot use unknown material`() {
            val tree = Tree(wood = UNKNOWN_MATERIAL_ID)

            assertIllegalArgument("Requires unknown Material 99!") { tree.validate(state) }
        }

        @Test
        fun `Valid tree`() {
            val tree = Tree(wood = MATERIAL_ID_0)

            tree.validate(state)
        }
    }

}