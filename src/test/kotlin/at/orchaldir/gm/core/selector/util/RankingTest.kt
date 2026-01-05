package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.RACE_ID_0
import at.orchaldir.gm.RACE_ID_1
import at.orchaldir.gm.RACE_ID_2
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class RankingTest {

    @Nested
    inner class CalculateIndexOfElementWithConceptTest {
        val element0 = Race(RACE_ID_0)
        val element1 = Race(RACE_ID_1)
        val element2 = Race(RACE_ID_2)
        private val elements = listOf(
            element0,
            element1,
            element2,
        )
        private val state = State(Storage(elements))

        @Test
        fun `Test indices with 3 different values`() {
            val lookup = mapOf(
                element0 to 100,
                element1 to 200,
                element2 to 300,
            )

            assertIndex(lookup, element0, 3)
            assertIndex(lookup, element1, 2)
            assertIndex(lookup, element2, 1)
        }

        private fun assertIndex(lookup: Map<Race, Int>, element: Race, index: Int) {
            assertEquals(
                index,
                state.calculateIndexOfElementWithConcept(element, lookup::get)
            )
        }

    }

}