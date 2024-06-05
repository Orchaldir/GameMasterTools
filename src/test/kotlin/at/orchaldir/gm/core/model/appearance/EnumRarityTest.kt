package at.orchaldir.gm.core.model.appearance

import at.orchaldir.gm.core.model.appearance.Rarity.Unavailable
import at.orchaldir.gm.core.model.appearance.Size.Small
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class EnumRarityTest {

    private val emptyEnumRarity = EnumRarity<Size>(setOf())
    private val unavailableEnumRarity = EnumRarity(mapOf(Small to Unavailable))

    @Nested
    inner class HasValidValuesTest {

        @Test
        fun `An empty map is not valid`() {
            assertFalse(emptyEnumRarity.hasValidValues())
        }

        @Test
        fun `Unavailable is not available`() {
            assertFalse(unavailableEnumRarity.hasValidValues())
        }

        @Test
        fun `Other rarities are available`() {
            Rarity.entries
                .filter { it != Unavailable }
                .forEach {
                    val enumRarity = EnumRarity(mapOf(Small to it))

                    assertTrue(enumRarity.hasValidValues())
                }
        }
    }

    @Nested
    inner class IsValidTest {

        @Test
        fun `Value not in map is invalid`() {
            assertFalse(emptyEnumRarity.isAvailable(Small))
        }

        @Test
        fun `Unavailable is not available`() {
            assertFalse(unavailableEnumRarity.isAvailable(Small))
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