package at.orchaldir.gm.core.selector.economy

import at.orchaldir.gm.BUSINESS_TEMPLATE_ID_0
import at.orchaldir.gm.DISTRICT_ID_0
import at.orchaldir.gm.REALM_ID_0
import at.orchaldir.gm.TOWN_ID_0
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.CommonBusinesses
import at.orchaldir.gm.core.model.economy.EconomyWithNumbers
import at.orchaldir.gm.core.model.economy.EconomyWithPercentages
import at.orchaldir.gm.core.model.realm.District
import at.orchaldir.gm.core.model.realm.Realm
import at.orchaldir.gm.core.model.realm.Town
import at.orchaldir.gm.core.model.util.NumberDistribution
import at.orchaldir.gm.core.model.util.PercentageDistribution
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.HALF
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class HasEconomyTest {

    private val commonBusinesses = CommonBusinesses(setOf(BUSINESS_TEMPLATE_ID_0))
    private val numbers = EconomyWithNumbers(NumberDistribution(mapOf(BUSINESS_TEMPLATE_ID_0 to 100)))
    private val percentages = EconomyWithPercentages(100, PercentageDistribution(mapOf(BUSINESS_TEMPLATE_ID_0 to HALF)))

    @Nested
    inner class CanDeleteEconomyOfTest {

        private val state = State()

        @Test
        fun `Cannot a common business`() {
            val district = District(DISTRICT_ID_0, economy = commonBusinesses)
            val newState = state.updateStorage(district)

            failCanDelete(newState, DISTRICT_ID_0)
        }

        @Test
        fun `Cannot delete a race used by the population of a realm`() {
            val realm = Realm(REALM_ID_0, economy = percentages)
            val newState = state.updateStorage(realm)

            failCanDelete(newState, REALM_ID_0)
        }

        @Test
        fun `Cannot delete a race used by a population with numbers`() {
            val town = Town(TOWN_ID_0, economy = numbers)
            val newState = state.updateStorage(town)

            failCanDelete(newState, TOWN_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            val result = DeleteResult(BUSINESS_TEMPLATE_ID_0)

            state.canDeleteEconomyOf(BUSINESS_TEMPLATE_ID_0, result)

            assertEquals(DeleteResult(BUSINESS_TEMPLATE_ID_0).addId(blockingId), result)
        }

    }

}