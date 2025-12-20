package at.orchaldir.gm.utils.math.unit

import at.orchaldir.gm.MATERIAL_ID_0
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class WeightPerMaterialTest {
    private val weightA = Weight.fromKilograms(1)
    private val weightB = Weight.fromKilograms(2)
    private val weightAB = Weight.fromKilograms(3)

    @Test
    fun `Add new material`() {
        assertEquals(weightA, WeightPerMaterial().add(MATERIAL_ID_0, weightA).get(MATERIAL_ID_0))
    }

}