package at.orchaldir.gm.core.reducer.ecology.plant

import at.orchaldir.gm.MATERIAL_ID_0
import at.orchaldir.gm.UNKNOWN_MATERIAL_ID
import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.ecology.plant.Tree
import at.orchaldir.gm.core.model.ecology.plant.appearance.BaseSplitting
import at.orchaldir.gm.core.model.ecology.plant.appearance.SegmentSplitting
import at.orchaldir.gm.core.model.ecology.plant.appearance.SimpleTreeSilhouette
import at.orchaldir.gm.core.model.ecology.plant.appearance.Stem
import at.orchaldir.gm.core.model.ecology.plant.appearance.StemSplitting
import at.orchaldir.gm.core.model.ecology.plant.appearance.Trunk
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

        @Nested
        inner class SplittingVsSilhouetteTest {

            @Test
            fun `Cannot have BaseSplitting with a simple silhouette`() {
                failWithSilhouette(BaseSplitting())
            }

            @Test
            fun `Cannot have SegmentSplitting with a simple silhouette`() {
                failWithSilhouette(SegmentSplitting())
            }

            private fun failWithSilhouette(splitting: StemSplitting) {
                val trunk = Trunk(stem = Stem(splitting = splitting))
                val tree = Tree(trunk, SimpleTreeSilhouette())

                assertIllegalArgument("SimpleTreeSilhouette doesn't support StemSplitting for the trunk!") {
                    tree.validate(
                        state
                    )
                }
            }
        }

        @Test
        fun `Valid tree`() {
            val tree = Tree(wood = MATERIAL_ID_0)

            tree.validate(state)
        }
    }

}