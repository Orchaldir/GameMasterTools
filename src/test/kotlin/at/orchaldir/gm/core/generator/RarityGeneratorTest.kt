package at.orchaldir.gm.core.generator

import at.orchaldir.gm.core.model.appearance.Rarity
import at.orchaldir.gm.core.model.appearance.Rarity.*
import at.orchaldir.gm.core.model.appearance.RarityMap
import at.orchaldir.gm.utils.Counter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class RarityGeneratorTest {

    private val rarityMap = RarityMap(Rarity.entries.associateBy { it })

    @Test
    fun `Empty values map`() {
        val generator = RarityGenerator(mapOf())
        val fixedNumber = Counter()

        assertNull(generator.generate(rarityMap, fixedNumber))
    }

    @Test
    fun `Value for every rarity`() {
        var value = 0u
        val generator = RarityGenerator(Rarity.entries.toList().reversed().associateWith { value++ })
        val fixedNumber = Counter()

        assertEquals(Common, generator.generate(rarityMap, fixedNumber))
        assertEquals(Common, generator.generate(rarityMap, fixedNumber))
        assertEquals(Common, generator.generate(rarityMap, fixedNumber))
        assertEquals(Common, generator.generate(rarityMap, fixedNumber))
        assertEquals(Uncommon, generator.generate(rarityMap, fixedNumber))
        assertEquals(Uncommon, generator.generate(rarityMap, fixedNumber))
        assertEquals(Uncommon, generator.generate(rarityMap, fixedNumber))
        assertEquals(Rare, generator.generate(rarityMap, fixedNumber))
        assertEquals(Rare, generator.generate(rarityMap, fixedNumber))
        assertEquals(VeryRare, generator.generate(rarityMap, fixedNumber))
        assertEquals(Common, generator.generate(rarityMap, fixedNumber))
    }

}