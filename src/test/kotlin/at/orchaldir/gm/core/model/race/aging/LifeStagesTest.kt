package at.orchaldir.gm.core.model.race.aging

import at.orchaldir.gm.core.model.race.appearance.RaceAppearanceId
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LifeStagesTest {

    @Nested
    inner class SimpleAgingTest {
        private val appearance = RaceAppearanceId(3)
        val a = LifeStage("A", 2, fromPercentage(1000))
        val b = LifeStage("B", 4, fromPercentage(2000))
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
                assertRelativeSize(-1, 500)
            }

            @Test
            fun `Get relative size`() {
                assertRelativeSize(0, 500)
                assertRelativeSize(1, 750)
                assertRelativeSize(2, 1000)
                assertRelativeSize(3, 1500)
                assertRelativeSize(4, 2000)
            }

            @Test
            fun `Get relative size if too old`() {
                assertRelativeSize(5, 2000)
            }

            private fun assertRelativeSize(age: Int, relativeSize: Int) {
                assertEquals(fromPercentage(relativeSize), simpleAging.getRelativeSize(age))
            }

        }

    }

}