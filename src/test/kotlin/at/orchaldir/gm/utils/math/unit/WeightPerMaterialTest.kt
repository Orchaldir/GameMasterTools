package at.orchaldir.gm.utils.math.unit

import at.orchaldir.gm.MATERIAL_ID_0
import at.orchaldir.gm.MATERIAL_ID_1
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class WeightPerMaterialTest {
    private val weightA = Weight.fromKilograms(1)
    private val weightB = Weight.fromKilograms(2)
    private val weightAB = Weight.fromKilograms(3)

    @Test
    fun `Add new material`() {
        val wpm = WeightPerMaterial().add(MATERIAL_ID_0, weightA)

        assertEquals(weightA, wpm.get(MATERIAL_ID_0))
        assertNull(wpm.get(MATERIAL_ID_1))
    }

    @Test
    fun `Add 2 materials`() {
        val wpm = WeightPerMaterial().add(MATERIAL_ID_0, weightA).add(MATERIAL_ID_1, weightB)

        assertEquals(weightA, wpm.get(MATERIAL_ID_0))
        assertEquals(weightB, wpm.get(MATERIAL_ID_1))
    }

    @Test
    fun `Add material again`() {
        val wpm = WeightPerMaterial().add(MATERIAL_ID_0, weightA).add(MATERIAL_ID_0, weightB)

        assertEquals(weightAB, wpm.get(MATERIAL_ID_0))
        assertNull(wpm.get(MATERIAL_ID_1))
    }

}