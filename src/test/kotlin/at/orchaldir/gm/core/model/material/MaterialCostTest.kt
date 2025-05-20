package at.orchaldir.gm.core.model.material

import at.orchaldir.gm.MATERIAL_ID_0
import at.orchaldir.gm.MATERIAL_ID_1
import at.orchaldir.gm.core.model.economy.material.MaterialCost
import at.orchaldir.gm.core.model.economy.material.MaterialCost.Companion.init
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

class MaterialCostTest {

    @Test
    fun `Test simple constructor`() {
        assertEquals(init(mapOf(MATERIAL_ID_0 to 1)), MaterialCost(MATERIAL_ID_0))
    }

    @Test
    fun `Filter a cost of 0`() {
        assertEquals(init(mapOf(MATERIAL_ID_0 to 2)), init(mapOf(MATERIAL_ID_0 to 2, MATERIAL_ID_1 to 0)))
    }

    @Test
    fun `Filter a negative cost`() {
        assertEquals(init(mapOf(MATERIAL_ID_1 to 3)), init(mapOf(MATERIAL_ID_0 to -1, MATERIAL_ID_1 to 3)))
    }

}