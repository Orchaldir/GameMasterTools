package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.realm.Realm
import at.orchaldir.gm.core.model.util.population.AbstractPopulation
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class HasPopulationTest {

    @Nested
    inner class GetPopulationsTest {

        @Test
        fun `Test abstract population `() {
            val population = AbstractPopulation(races = setOf(RACE_ID_0))
            val realm0 = Realm(REALM_ID_0, population = population)
            val realm1 = Realm(REALM_ID_1)
            val storage = Storage(listOf(realm0, realm1))

            assertEquals(listOf(realm0), getPopulations(storage,RACE_ID_0))
        }
    }

}