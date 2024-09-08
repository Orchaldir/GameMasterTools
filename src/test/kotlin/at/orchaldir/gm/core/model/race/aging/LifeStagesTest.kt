package at.orchaldir.gm.core.model.race.aging

import at.orchaldir.gm.core.model.race.appearance.RaceAppearanceId
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LifeStagesTest {

    @Nested
    inner class SimpleAgingTest {
        private val appearance = RaceAppearanceId(3)
        private val simpleAging = SimpleAging(appearance, listOf(SimpleLifeStage("A", 2), SimpleLifeStage("B", 4)))

        @Test
        fun `Always get the same appearance`() {
            repeat(10) { age ->
                assertEquals(appearance, simpleAging.getAppearance(age))
            }
        }

        @Test
        fun `Get name of life stages`() {
            assertEquals("A", simpleAging.getLifeStageName(0))
            assertEquals("A", simpleAging.getLifeStageName(1))
            assertEquals("A", simpleAging.getLifeStageName(2))
            assertEquals("B", simpleAging.getLifeStageName(3))
            assertEquals("B", simpleAging.getLifeStageName(4))
        }

        @Test
        fun `Get name of last life stage if too old`() {
            assertEquals("B", simpleAging.getLifeStageName(5))
        }

    }

}