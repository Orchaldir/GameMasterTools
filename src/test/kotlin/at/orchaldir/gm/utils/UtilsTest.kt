package at.orchaldir.gm.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class UtilsTest {

    @Nested
    inner class CombineTest {
        private val list0 = listOf(1, 2, 3)
        private val list1 = listOf(100, 200)
        private val emptyList = emptyList<Pair<Int, Int>>()

        @Test
        fun `Combine to lists`() {
            assertEquals(
                listOf(
                    Pair(1, 100),
                    Pair(1, 200),
                    Pair(2, 100),
                    Pair(2, 200),
                    Pair(3, 100),
                    Pair(3, 200),
                ),
                list0.combine(list1),
            )
        }

        @Test
        fun `Combine with empty list`() {
            assertEquals(emptyList, list0.combine(emptyList))
        }

    }
}