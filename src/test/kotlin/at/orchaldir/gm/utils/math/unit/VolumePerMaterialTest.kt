package at.orchaldir.gm.utils.math.unit

import at.orchaldir.gm.MATERIAL_ID_0
import at.orchaldir.gm.MATERIAL_ID_1
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class VolumePerMaterialTest {
    private val volumeA = Volume.fromCubicMeters(1)
    private val volumeB = Volume.fromCubicMeters(2)
    private val volumeAB = Volume.fromCubicMeters(3)

    @Test
    fun `Add new material`() {
        val wpm = VolumePerMaterial().add(MATERIAL_ID_0, volumeA)

        assertEquals(volumeA, wpm.get(MATERIAL_ID_0))
        assertNull(wpm.get(MATERIAL_ID_1))
    }

    @Test
    fun `Add 2 materials`() {
        val wpm = VolumePerMaterial().add(MATERIAL_ID_0, volumeA).add(MATERIAL_ID_1, volumeB)

        assertEquals(volumeA, wpm.get(MATERIAL_ID_0))
        assertEquals(volumeB, wpm.get(MATERIAL_ID_1))
    }

    @Test
    fun `Add material again`() {
        val wpm = VolumePerMaterial().add(MATERIAL_ID_0, volumeA).add(MATERIAL_ID_0, volumeB)

        assertEquals(volumeAB, wpm.get(MATERIAL_ID_0))
        assertNull(wpm.get(MATERIAL_ID_1))
    }

}