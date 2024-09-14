package at.orchaldir.gm.core.model.race.aging

import at.orchaldir.gm.core.model.race.appearance.RaceAppearanceId
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LifeStagesTest {

    @Nested
    inner class SimpleAgingTest {
        private val appearance = RaceAppearanceId(3)
        val a = LifeStage("A", 2)
        val b = LifeStage("B", 4)
        private val simpleAging = SimpleAging(appearance, listOf(a, b))

        @Test
        fun `Always get the same appearance`() {
            assertEquals(appearance, simpleAging.getRaceAppearance())
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