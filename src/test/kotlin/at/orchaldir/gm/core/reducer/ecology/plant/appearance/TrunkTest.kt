package at.orchaldir.gm.core.reducer.ecology.plant.appearance

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.ecology.plant.Tree
import at.orchaldir.gm.core.model.ecology.plant.appearance.MAX_TRUNK_HEIGHT
import at.orchaldir.gm.core.model.ecology.plant.appearance.MIN_TRUNK_HEIGHT
import at.orchaldir.gm.core.model.ecology.plant.appearance.Trunk
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Distribution
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class TrunkTest {

    private val state = State(
        listOf(
            Storage(Material(MATERIAL_ID_0)),
        )
    )

    @Nested
    inner class TreeTest {

        @Test
        fun `Cannot use too small height`() {
            val tree = Trunk(Distribution(MIN_TRUNK_HEIGHT - Distance.fromMillimeters(1)))

            assertIllegalArgument("The trunk's height is too small!") { tree.validate(state) }
        }

        @Test
        fun `Can use min height`() {
            val tree = Trunk(Distribution(MIN_TRUNK_HEIGHT))

            tree.validate(state)
        }

        @Test
        fun `Cannot use too large height`() {
            val tree = Trunk(Distribution(MAX_TRUNK_HEIGHT + Distance.fromMillimeters(1)))

            assertIllegalArgument("The trunk's height is too large!") { tree.validate(state) }
        }

        @Test
        fun `Can use max height`() {
            val tree = Trunk(Distribution(MAX_TRUNK_HEIGHT))

            tree.validate(state)
        }
    }

}