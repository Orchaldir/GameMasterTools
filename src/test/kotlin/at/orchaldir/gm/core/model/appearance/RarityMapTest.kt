package at.orchaldir.gm.core.model.appearance

import at.orchaldir.gm.core.model.appearance.Rarity.*
import at.orchaldir.gm.core.model.appearance.Size.Small
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RarityMapTest {

    @Nested
    inner class OneOfTest {

        @Test
        fun `An empty map is not valid`() {
            assertFailsWith<IllegalArgumentException> { OneOf<Size>(setOf()) }
        }

        @Test
        fun `Requires one rarity that is not unavailable`() {
            assertFailsWith<IllegalArgumentException> { OneOf(mapOf(Small to Unavailable)) }
        }

        @Nested
        inner class IsValidTest {

            @Test
            fun `Other rarities are available`() {
                Rarity.entries
                    .filter { it != Unavailable }
                    .forEach {
                        val rarityMap = OneOf(mapOf(Small to it))

                        assertTrue(rarityMap.isAvailable(Small))
                    }
            }
        }
    }

    @Nested
    inner class SomeOfTest {

        @Test
        fun `An empty map is valid`() {
            SomeOf<Size>(setOf())
        }

        @Test
        fun `Is fine with only unavailable`() {
            SomeOf(mapOf(Small to Unavailable))
        }

        @Nested
        inner class IsValidTest {

            @Test
            fun `Other rarities are available`() {
                Rarity.entries
                    .filter { it != Unavailable }
                    .forEach {
                        val rarityMap = SomeOf(mapOf(Small to it))

                        assertTrue(rarityMap.isAvailable(Small))
                    }
            }
        }
    }

    @Test
    fun `Reverse and sort, but not the value`() {
        assertEquals(
            sortedMapOf(Common to listOf(5, 2), Rare to listOf(1, 7)),
            reverseAndSort(mapOf(1 to Rare, 5 to Common, 7 to Rare, 2 to Common))
        )
    }

}