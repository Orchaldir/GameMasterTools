package at.orchaldir.gm.core.model.appearance

import at.orchaldir.gm.core.model.appearance.Rarity.Unavailable
import at.orchaldir.gm.core.model.appearance.Size.Small
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class RarityMapTest {

    private val emptyRarityMap = RarityMap<Size>(setOf())
    private val unavailableRarityMap = RarityMap(mapOf(Small to Unavailable))

    @Nested
    inner class HasValidValuesTest {

        @Test
        fun `An empty map is not valid`() {
            assertFalse(emptyRarityMap.hasValidValues())
        }

        @Test
        fun `Unavailable is not available`() {
            assertFalse(unavailableRarityMap.hasValidValues())
        }

        @Test
        fun `Other rarities are available`() {
            Rarity.entries
                .filter { it != Unavailable }
                .forEach {
                    val rarityMap = RarityMap(mapOf(Small to it))

                    assertTrue(rarityMap.hasValidValues())
                }
        }
    }

    @Nested
    inner class IsValidTest {

        @Test
        fun `Value not in map is invalid`() {
            assertFalse(emptyRarityMap.isAvailable(Small))
        }

        @Test
        fun `Unavailable is not available`() {
            assertFalse(unavailableRarityMap.isAvailable(Small))
        }

        @Test
        fun `Other rarities are available`() {
            Rarity.entries
                .filter { it != Unavailable }
                .forEach {
                    val rarityMap = RarityMap(mapOf(Small to it))

                    assertTrue(rarityMap.isAvailable(Small))
                }
        }
    }

}