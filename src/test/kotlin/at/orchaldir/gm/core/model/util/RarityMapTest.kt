package at.orchaldir.gm.core.model.util

import at.orchaldir.gm.core.model.util.render.Color.*
import at.orchaldir.gm.core.model.util.Rarity.*
import at.orchaldir.gm.core.model.util.Size.Small
import org.junit.jupiter.api.Assertions.assertFalse
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
    inner class OneOrNoneTest {

        @Test
        fun `An empty map is valid`() {
            OneOrNone<Size>(setOf())
        }

        @Test
        fun `Is fine with only unavailable`() {
            OneOrNone(mapOf(Small to Unavailable))
        }

        @Nested
        inner class IsValidTest {

            @Test
            fun `Other rarities are available`() {
                Rarity.entries
                    .filter { it != Unavailable }
                    .forEach {
                        val rarityMap = OneOrNone(mapOf(Small to it))

                        assertTrue(rarityMap.isAvailable(Small))
                    }
            }

            @Test
            fun `Unavailable is not available`() {
                val rarityMap = SomeOf(mapOf(Small to Unavailable))

                assertFalse(rarityMap.isAvailable(Small))
            }
        }
    }

    @Nested
    inner class SomeOfTest {

        @Test
        fun `An empty map is valid`() {
            SomeOf<Size>(setOf())
        }

        @Nested
        inner class GetValuesForTest {

            val colors = SomeOf.init(mapOf(Red to Everyone, Green to Common, Blue to Common))

            @Test
            fun `Get the correct value for a rarity`() {
                assertEquals(setOf(Red), colors.getValuesFor(Everyone))
            }

            @Test
            fun `Get multiple values for a rarity`() {
                assertEquals(setOf(Green, Blue), colors.getValuesFor(Common))
            }

            @Test
            fun `A rarity without values`() {
                assertEquals(emptySet(), colors.getValuesFor(Rare))
            }
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

            @Test
            fun `Is fine with only unavailable`() {
                val rarityMap = SomeOf(mapOf(Small to Unavailable))

                assertFalse(rarityMap.isAvailable(Small))
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