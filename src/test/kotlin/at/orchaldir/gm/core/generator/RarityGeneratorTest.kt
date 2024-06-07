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
        val counter = Counter()

        assertNull(generator.generate(rarityMap, counter))
    }

    @Test
    fun `Value for every rarity`() {
        val generator = RarityGenerator.empty()
        val counter = Counter()

        assertNumbers(
            generator,
            counter,
            listOf(Common, Common, Common, Common, Uncommon, Uncommon, Uncommon, Rare, Rare, VeryRare, Common)
        )
    }

    private fun assertNumbers(generator: RarityGenerator, counter: Counter, results: List<Rarity>) {
        results.forEach { assertEquals(it, generator.generate(rarityMap, counter)) }
    }

}