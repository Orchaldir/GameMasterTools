package at.orchaldir.gm.core.model.appearance

import at.orchaldir.gm.core.model.appearance.Rarity.Unavailable
import at.orchaldir.gm.core.model.appearance.Size.Small
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class EnumRarityTest {

    @Nested
    inner class IsValidTest {

        @Test
        fun `Value not in map is invalid`() {
            val enumRarity = EnumRarity<Size>(setOf())

            assertFalse(enumRarity.isAvailable(Small))
        }

        @Test
        fun `Unavailable is not available`() {
            val enumRarity = EnumRarity(mapOf(Small to Unavailable))

            assertFalse(enumRarity.isAvailable(Small))
        }

        @Test
        fun `Other rarities are available`() {
            Rarity.entries
                .filter { it != Unavailable }
                .forEach {
                    val enumRarity = EnumRarity(mapOf(Small to it))

                    assertTrue(enumRarity.isAvailable(Small))
                }
        }
    }

}