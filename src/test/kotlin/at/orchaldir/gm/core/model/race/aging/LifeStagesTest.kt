package at.orchaldir.gm.core.model.race.aging

import at.orchaldir.gm.core.model.race.appearance.RaceAppearanceId
import at.orchaldir.gm.utils.math.FULL
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LifeStagesTest {

    @Nested
    inner class SimpleAgingTest {
        private val appearance = RaceAppearanceId(3)
        val a = SimpleLifeStage("A", 2)
        val b = SimpleLifeStage("B", 4)
        private val simpleAging = SimpleAging(appearance, listOf(a, b))

        @Test
        fun `Always get the same appearance`() {
            repeat(10) { age ->
                assertEquals(appearance, simpleAging.getAppearance(age))
            }
        }

        @Test
        fun `Get name of life stages`() {
            assertEquals(a, simpleAging.getLifeStage(0))
            assertEquals(a, simpleAging.getLifeStage(1))
            assertEquals(a, simpleAging.getLifeStage(2))
            assertEquals(b, simpleAging.getLifeStage(3))
            assertEquals(b, simpleAging.getLifeStage(4))
        }

        @Test
        fun `Get name of last life stage if too old`() {
            assertEquals(b, simpleAging.getLifeStage(5))
        }

    }

    @Nested
    inner class ComplexAgingTest {
        private val appearance0 = RaceAppearanceId(1)
        private val appearance1 = RaceAppearanceId(2)
        val a = ComplexLifeStage("A", 2, FULL, appearance0)
        val b = ComplexLifeStage("B", 4, FULL, appearance1)
        private val simpleAging = ComplexAging(listOf(a, b))

        @Test
        fun `Get appearance of life stages`() {
            assertEquals(appearance0, simpleAging.getAppearance(0))
            assertEquals(appearance0, simpleAging.getAppearance(1))
            assertEquals(appearance0, simpleAging.getAppearance(2))
            assertEquals(appearance1, simpleAging.getAppearance(3))
            assertEquals(appearance1, simpleAging.getAppearance(4))
        }

        @Test
        fun `Get appearance of last life stage if too old`() {
            assertEquals(appearance1, simpleAging.getAppearance(5))
        }

        @Test
        fun `Get name of life stages`() {
            assertEquals(a, simpleAging.getLifeStage(0))
            assertEquals(a, simpleAging.getLifeStage(1))
            assertEquals(a, simpleAging.getLifeStage(2))
            assertEquals(b, simpleAging.getLifeStage(3))
            assertEquals(b, simpleAging.getLifeStage(4))
        }

        @Test
        fun `Get name of last life stage if too old`() {
            assertEquals(b, simpleAging.getLifeStage(5))
        }

    }

}