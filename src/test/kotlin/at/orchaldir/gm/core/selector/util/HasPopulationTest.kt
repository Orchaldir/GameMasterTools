package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.DISTRICT_ID_0
import at.orchaldir.gm.RACE_ID_0
import at.orchaldir.gm.REALM_ID_0
import at.orchaldir.gm.REALM_ID_1
import at.orchaldir.gm.TOWN_ID_0
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.District
import at.orchaldir.gm.core.model.realm.Realm
import at.orchaldir.gm.core.model.realm.Town
import at.orchaldir.gm.core.model.util.population.AbstractPopulation
import at.orchaldir.gm.core.model.util.population.ElementDistribution
import at.orchaldir.gm.core.model.util.population.Population
import at.orchaldir.gm.core.model.util.population.PopulationDistribution
import at.orchaldir.gm.core.model.util.population.TotalPopulation
import at.orchaldir.gm.core.selector.race.canDeleteRace
import at.orchaldir.gm.utils.Id
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
    inner class CanDeletePopulationOfTest {

        private val state = State()

        @Test
        fun `Cannot delete a race used by the population of a district`() {
            val district = District(DISTRICT_ID_0, population = abstractPopulation)
            val newState = state.updateStorage(Storage(district))

            failCanDelete(newState, DISTRICT_ID_0)
        }

        @Test
        fun `Cannot delete a race used by the population of a realm`() {
            val realm = Realm(REALM_ID_0, population = populationDistribution)
            val newState = state.updateStorage(Storage(realm))

            failCanDelete(newState, REALM_ID_0)
        }

        @Test
        fun `Cannot delete a race used by the population of a town`() {
            val town = Town(TOWN_ID_0, population = totalPopulation)
            val newState = state.updateStorage(Storage(town))

            failCanDelete(newState, TOWN_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            val result = DeleteResult(RACE_ID_0)

            state.canDeletePopulationOf(RACE_ID_0, result)

            assertEquals(DeleteResult(RACE_ID_0).addId(blockingId), result)
        }

    }

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