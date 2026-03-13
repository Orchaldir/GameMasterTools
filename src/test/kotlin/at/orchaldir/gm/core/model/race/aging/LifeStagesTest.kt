package at.orchaldir.gm.core.model.race.aging

import at.orchaldir.gm.NAME0
import at.orchaldir.gm.NAME1
import at.orchaldir.gm.core.model.race.appearance.RaceAppearanceId
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LifeStagesTest {

    @Nested
    inner class SimpleAgingTest {
        private val appearance = RaceAppearanceId(3)
        val a = LifeStage(NAME0, 2, fromPercentage(1000))
        val b = LifeStage(NAME1, 4, fromPercentage(2000))
        private val customAging = CustomAging(appearance, listOf(a, b))

        @Test
        fun `Always get the same appearance`() {
            assertEquals(appearance, customAging.getRaceAppearance())
        }

        @Nested
        inner class GetLifeStageTest {

            @Test
            fun `Get life stage before being born`() {
                assertEquals(a, customAging.getLifeStageForAge(-1))
            }

            @Test
            fun `Get life stage`() {
                assertEquals(a, customAging.getLifeStageForAge(0))
                assertEquals(a, customAging.getLifeStageForAge(1))
                assertEquals(a, customAging.getLifeStageForAge(2))
                assertEquals(b, customAging.getLifeStageForAge(3))
                assertEquals(b, customAging.getLifeStageForAge(4))
            }

            @Test
            fun `Get last life stage if too old`() {
                assertEquals(b, customAging.getLifeStageForAge(5))
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
                assertEquals(fromPercentage(relativeSize), customAging.getRelativeSize(age))
            }

        }

    }

}