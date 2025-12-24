package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.realm.Realm
import at.orchaldir.gm.core.model.util.population.AbstractPopulation
import at.orchaldir.gm.core.model.util.population.Population
import at.orchaldir.gm.core.model.util.population.PopulationPerRace
import at.orchaldir.gm.core.model.util.population.TotalPopulation
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.HALF
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class HasPopulationTest {

    @Nested
    inner class GetPopulationsTest {

        @Test
        fun `Test abstract population`() {
            assertGetPopulations(AbstractPopulation(races = setOf(RACE_ID_0)))
        }

        @Test
        fun `Test population per race`() {
            assertGetPopulations(PopulationPerRace(100, mapOf(RACE_ID_0 to HALF)))
        }

        @Test
        fun `Test total population`() {
            assertGetPopulations(TotalPopulation(100, setOf(RACE_ID_0)))
        }

        private fun assertGetPopulations(population: Population) {
            val realm0 = Realm(REALM_ID_0, population = population)
            val realm1 = Realm(REALM_ID_1)
            val storage = Storage(listOf(realm0, realm1))

            assertEquals(listOf(realm0), getPopulations(storage, RACE_ID_0))
        }
    }

}