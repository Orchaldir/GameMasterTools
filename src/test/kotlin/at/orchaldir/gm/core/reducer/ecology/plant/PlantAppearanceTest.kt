package at.orchaldir.gm.core.reducer.ecology.plant

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.UpdateAction
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.ecology.plant.Plant
import at.orchaldir.gm.core.model.ecology.plant.Tree
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.core.model.util.CharacterReference
import at.orchaldir.gm.core.model.util.origin.CreatedElement
import at.orchaldir.gm.core.model.util.origin.ModifiedElement
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


class PlantAppearanceTest {

    private val state = State(
        listOf(
            Storage(CALENDAR0),
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