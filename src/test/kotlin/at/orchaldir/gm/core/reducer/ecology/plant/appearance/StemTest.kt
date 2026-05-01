package at.orchaldir.gm.core.reducer.ecology.plant.appearance

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.ecology.plant.Tree
import at.orchaldir.gm.core.model.ecology.plant.appearance.MAX_SEGMENTS
import at.orchaldir.gm.core.model.ecology.plant.appearance.MAX_TRUNK_HEIGHT
import at.orchaldir.gm.core.model.ecology.plant.appearance.MIN_SEGMENTS
import at.orchaldir.gm.core.model.ecology.plant.appearance.MIN_TRUNK_HEIGHT
import at.orchaldir.gm.core.model.ecology.plant.appearance.Stem
import at.orchaldir.gm.core.model.ecology.plant.appearance.Trunk
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Distribution
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class StemTest {

    private val state = State(
        listOf(
            Storage(Material(MATERIAL_ID_0)),
        )
    )

    @Test
    fun `Cannot use too few segments`() {
        val tree = Stem(MIN_SEGMENTS - 1)

        assertIllegalArgument("The test's number of segments is too small!") { tree.validate(state, "test") }
    }

    @Test
    fun `Cannot use too many segments`() {
        val tree = Stem(MAX_SEGMENTS + 1)

        assertIllegalArgument("The test's number of segments is too large!") { tree.validate(state, "test") }
    }
}