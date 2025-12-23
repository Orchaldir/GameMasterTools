package at.orchaldir.gm.core.model.material

import at.orchaldir.gm.MATERIAL_ID_0
import at.orchaldir.gm.MATERIAL_ID_1
import at.orchaldir.gm.core.model.economy.material.MaterialCost
import at.orchaldir.gm.core.model.economy.material.MaterialCost.Companion.init
import at.orchaldir.gm.utils.math.unit.Weight
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

class MaterialCostTest {

    val w0 = Weight.fromKilograms(0)
    val w1 = Weight.fromKilograms(1)
    val w2 = Weight.fromKilograms(2)
    val w3 = Weight.fromKilograms(3)

    @Test
    fun `Test simple constructor`() {
        assertEquals(MaterialCost(mapOf(MATERIAL_ID_0 to w1)), MaterialCost(MATERIAL_ID_0))
    }

    @Test
    fun `Filter a cost of 0`() {
        assertEquals(
            MaterialCost(mapOf(MATERIAL_ID_0 to w2)),
            init(mapOf(MATERIAL_ID_0 to w2, MATERIAL_ID_1 to w0)),
        )
    }

    @Test
    fun `Calculate Weight`() {
        assertEquals(w3, init(mapOf(MATERIAL_ID_0 to w2, MATERIAL_ID_1 to w1)).calculateWeight(),)
    }

}