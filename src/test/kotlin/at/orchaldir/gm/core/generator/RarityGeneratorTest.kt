package at.orchaldir.gm.core.generator

import at.orchaldir.gm.core.model.appearance.OneOf
import at.orchaldir.gm.core.model.appearance.Rarity
import at.orchaldir.gm.core.model.appearance.Rarity.*
import at.orchaldir.gm.utils.Counter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class RarityGeneratorTest {

    private val rarityMap = OneOf(Rarity.entries.associateBy { it })

    @Test
    fun `Empty map is invalid`() {
        assertFailsWith<IllegalArgumentException> { RarityGenerator(mapOf()) }
    }

    @Test
    fun `Requires one rarity that is not unavailable`() {
        assertFailsWith<IllegalArgumentException> { RarityGenerator(mapOf(Unavailable to 7)) }
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