package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.RACE_ID_0
import at.orchaldir.gm.RACE_ID_1
import at.orchaldir.gm.RACE_ID_2
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class RankingTest {

    @Nested
    inner class CalculateRankOfElementTest {
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
        fun `Test with 3 different values`() {
            val lookup = mapOf(
                element0 to 100,
                element1 to 200,
                element2 to 300,
            )

            assertRank(lookup, element0, 3)
            assertRank(lookup, element1, 2)
            assertRank(lookup, element2, 1)
        }

        @Test
        fun `Test with 2 identical values`() {
            val lookup = mapOf(
                element0 to 100,
                element1 to 300,
                element2 to 300,
            )

            assertRank(lookup, element0, 3)
            assertRank(lookup, element1, 1)
            assertRank(lookup, element2, 1)
        }

        @Test
        fun `Test with null`() {
            val lookup = mapOf(
                element0 to 4,
                element1 to null,
                element2 to 3,
            )

            assertRank(lookup, element0, 1)
            assertRank(lookup, element1, null)
            assertRank(lookup, element2, 2)
        }

        @Test
        fun `Test with 0`() {
            val lookup = mapOf(
                element0 to 4,
                element1 to 0,
                element2 to 3,
            )

            assertRank(lookup, element0, 1)
            assertRank(lookup, element1, null)
            assertRank(lookup, element2, 2)
        }

        private fun assertRank(lookup: Map<Race, Int?>, element: Race, rank: Int?) {
            assertEquals(
                rank,
                state.calculateRankOfElement(element, lookup::get)
            )
        }

    }

}