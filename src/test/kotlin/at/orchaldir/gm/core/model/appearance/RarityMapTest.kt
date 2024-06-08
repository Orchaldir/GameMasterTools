package at.orchaldir.gm.core.model.appearance

import at.orchaldir.gm.core.model.appearance.Rarity.Unavailable
import at.orchaldir.gm.core.model.appearance.Size.Small
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class RarityMapTest {

    @Test
    fun `An empty map is not valid`() {
        assertFailsWith<IllegalArgumentException> { RarityMap<Size>(setOf()) }
    }

    @Test
    fun `Requires one rarity that is not unavailable`() {
        assertFailsWith<IllegalArgumentException> { RarityMap(mapOf(Small to Unavailable)) }
    }

    @Nested
    inner class IsValidTest {

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