package at.orchaldir.gm.core.model.race.aging

import at.orchaldir.gm.core.model.race.appearance.RaceAppearanceId
import at.orchaldir.gm.utils.math.Factor
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LifeStagesTest {

    @Nested
    inner class SimpleAgingTest {
        private val appearance = RaceAppearanceId(3)
        val a = LifeStage("A", 2, Factor(10.0f))
        val b = LifeStage("B", 4, Factor(20.0f))
        private val simpleAging = SimpleAging(appearance, listOf(a, b))

        @Test
        fun `Always get the same appearance`() {
            assertEquals(appearance, simpleAging.getRaceAppearance())
        }

        @Nested
        inner class GetLifeStageTest {

            @Test
            fun `Get life stage before being born`() {
                assertEquals(a, simpleAging.getLifeStage(-1))
            }

            @Test
            fun `Get life stage`() {
                assertEquals(a, simpleAging.getLifeStage(0))
                assertEquals(a, simpleAging.getLifeStage(1))
                assertEquals(a, simpleAging.getLifeStage(2))
                assertEquals(b, simpleAging.getLifeStage(3))
                assertEquals(b, simpleAging.getLifeStage(4))
            }

            @Test
            fun `Get last life stage if too old`() {
                assertEquals(b, simpleAging.getLifeStage(5))
            }
        }

        @Nested
        inner class GetRelativeSizeTest {

            @Test
            fun `Get relative size before being born`() {
                assertRelativeSize(-1, 5.0f)
            }

            @Test
            fun `Get relative size`() {
                assertRelativeSize(0, 5.0f)
                assertRelativeSize(1, 7.5f)
                assertRelativeSize(2, 10.0f)
                assertRelativeSize(3, 15.0f)
                assertRelativeSize(4, 20.0f)
            }

            @Test
            fun `Get relative size if too old`() {
                assertRelativeSize(5, 20.0f)
            }

            private fun assertRelativeSize(age: Int, relativeSize: Float) {
                assertEquals(Factor(relativeSize), simpleAging.getRelativeSize(age))
            }

        }

    }

}