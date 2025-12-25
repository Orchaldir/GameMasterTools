package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.RACE_ID_0
import at.orchaldir.gm.REALM_ID_0
import at.orchaldir.gm.REALM_ID_1
import at.orchaldir.gm.core.model.realm.Realm
import at.orchaldir.gm.core.model.util.population.AbstractPopulation
import at.orchaldir.gm.core.model.util.population.ElementDistribution
import at.orchaldir.gm.core.model.util.population.Population
import at.orchaldir.gm.core.model.util.population.PopulationDistribution
import at.orchaldir.gm.core.model.util.population.TotalPopulation
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.HALF
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class HasPopulationTest {

    private val abstractPopulation = AbstractPopulation(races = setOf(RACE_ID_0))
    private val populationDistribution = PopulationDistribution(100, ElementDistribution(mapOf(RACE_ID_0 to HALF)))
    private val totalPopulation = TotalPopulation(100, setOf(RACE_ID_0))

    @Nested
    inner class GetPopulationsTest {

        @Test
        fun `Test abstract population`() {
            assertGetPopulations(abstractPopulation)
        }

        @Test
        fun `Test population per race`() {
            assertGetPopulations(populationDistribution)
        }

        @Test
        fun `Test total population`() {
            assertGetPopulations(totalPopulation)
        }

        private fun assertGetPopulations(population: Population) {
            val realm0 = Realm(REALM_ID_0, population = population)
            val realm1 = Realm(REALM_ID_1)
            val storage = Storage(listOf(realm0, realm1))

            assertEquals(
                listOf(realm0),
                getPopulations(storage) { it.population().contains(RACE_ID_0) },
            )
        }
    }

}