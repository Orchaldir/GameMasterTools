package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.HousingStatus
import at.orchaldir.gm.core.model.character.InApartment
import at.orchaldir.gm.core.model.character.InHouse
import at.orchaldir.gm.core.model.character.InRealm
import at.orchaldir.gm.core.model.character.InTown
import at.orchaldir.gm.core.model.realm.Realm
import at.orchaldir.gm.core.model.realm.Town
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.util.HistoryEntry
import at.orchaldir.gm.core.model.world.building.ApartmentHouse
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.model.world.building.BuildingPurpose
import at.orchaldir.gm.core.model.world.building.SingleFamilyHouse
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class HousingStatusTest {

    private val inApartment = InApartment(BUILDING_ID_0, 0)
    private val inHouse = InHouse(BUILDING_ID_0)
    private val inRealm = InRealm(REALM_ID_0)
    private val inTown = InTown(TOWN_ID_0)

    @Nested
    inner class ApartmentTest {

        @Test
        fun `Cannot use unknown building as apartment house`() {
            val ownership = History<HousingStatus>(InApartment(UNKNOWN_BUILDING_ID, 9))

            assertIllegalArgument("Requires unknown home!") {
                checkHousingStatusHistory(createState(), ownership, DAY0)
            }
        }

        @Test
        fun `Cannot use an apartment number higher than the building allows`() {
            val state = createState(ApartmentHouse(2))
            val ownership = History<HousingStatus>(InApartment(BUILDING_ID_0, 2))

            assertIllegalArgument("The home's apartment index is too high!") {
                checkHousingStatusHistory(state, ownership, DAY0)
            }
        }

        @Test
        fun `The apartment house doesn't exist yet`() {
            val state = createState(ApartmentHouse(2), DAY1)

            assertIllegalArgument("The home doesn't exist at the required date!") {
                checkHousingStatusHistory(state, History(inApartment), DAY0)
            }
        }

        @Test
        fun `Live in a valid apartment`() {
            val count = 3
            val state = createState(ApartmentHouse(count))

            repeat(count) {
                val ownership = History<HousingStatus>(InApartment(BUILDING_ID_0, it))

                checkHousingStatusHistory(state, ownership, DAY0)
            }
        }
    }

    @Nested
    inner class HouseTest {

        @Test
        fun `Cannot use unknown building as home`() {
            val state = createState()
            val ownership = History<HousingStatus>(InHouse(UNKNOWN_BUILDING_ID))

            assertIllegalArgument("Requires unknown home!") {
                checkHousingStatusHistory(state, ownership, DAY0)
            }
        }

        @Test
        fun `Cannot use unknown building as a previous home`() {
            val state = createState()
            val entry = HistoryEntry<HousingStatus>(InHouse(UNKNOWN_BUILDING_ID), DAY1)
            val ownership = History(inHouse, entry)

            assertIllegalArgument("Requires unknown 1.previous home!") {
                checkHousingStatusHistory(state, ownership, DAY0)
            }
        }

        @Test
        fun `Living in an house requires a house`() {
            val state = createState(ApartmentHouse(2))

            assertIllegalArgument("The home is not a home!") {
                checkHousingStatusHistory(state, History(inHouse), DAY0)
            }
        }

        @Test
        fun `The house doesn't exist yet`() {
            val state = createState(date = DAY1)

            assertIllegalArgument("The home doesn't exist at the required date!") {
                checkHousingStatusHistory(state, History(inHouse), DAY0)
            }
        }

        @Test
        fun `Live in a valid single family house`() {
            checkHousingStatusHistory(createState(), History(inHouse), DAY0)
        }
    }

    @Nested
    inner class RealmTest {

        @Test
        fun `Cannot use unknown realm as home`() {
            val ownership = History<HousingStatus>(InRealm(UNKNOWN_REALM_ID))

            assertIllegalArgument("Requires unknown home!") {
                checkHousingStatusHistory(createState(), ownership, DAY0)
            }
        }

        @Test
        fun `Cannot use unknown realm as a previous home`() {
            val entry = HistoryEntry<HousingStatus>(InRealm(UNKNOWN_REALM_ID), DAY1)
            val ownership = History(inHouse, entry)

            assertIllegalArgument("Requires unknown 1.previous home!") {
                checkHousingStatusHistory(createState(), ownership, DAY0)
            }
        }

        @Test
        fun `The realm doesn't exist yet`() {
            val state = createRealmState(date = DAY1)

            assertIllegalArgument("The home doesn't exist at the required date!") {
                checkHousingStatusHistory(state, History(inRealm), DAY0)
            }
        }

        @Test
        fun `Live in a valid realm`() {
            checkHousingStatusHistory(createRealmState(), History(inRealm), DAY0)
        }

        private fun createRealmState(date: Date = DAY0) = State(
            listOf(
                Storage(Building(BUILDING_ID_0)),
                Storage(CALENDAR0),
                Storage(Realm(REALM_ID_0, date = date)),
            )
        )
    }

    @Nested
    inner class TownTest {

        @Test
        fun `Cannot use unknown town as home`() {
            val ownership = History<HousingStatus>(InTown(UNKNOWN_TOWN_ID))

            assertIllegalArgument("Requires unknown home!") {
                checkHousingStatusHistory(createState(), ownership, DAY0)
            }
        }

        @Test
        fun `Cannot use unknown town as a previous home`() {
            val entry = HistoryEntry<HousingStatus>(InTown(UNKNOWN_TOWN_ID), DAY1)
            val ownership = History(inHouse, entry)

            assertIllegalArgument("Requires unknown 1.previous home!") {
                checkHousingStatusHistory(createState(), ownership, DAY0)
            }
        }

        @Test
        fun `The town doesn't exist yet`() {
            val state = createTownState(date = DAY1)

            assertIllegalArgument("The home doesn't exist at the required date!") {
                checkHousingStatusHistory(state, History(inTown), DAY0)
            }
        }

        @Test
        fun `Live in a valid town`() {
            checkHousingStatusHistory(createTownState(), History(inTown), DAY0)
        }

        private fun createTownState(date: Date = DAY0) = State(
            listOf(
                Storage(Building(BUILDING_ID_0)),
                Storage(CALENDAR0),
                Storage(Town(TOWN_ID_0, foundingDate = date)),
            )
        )
    }

    private fun createState(purpose: BuildingPurpose = SingleFamilyHouse, date: Date = DAY0) = State(
        listOf(
            Storage(listOf(create(purpose, date), Building(BUILDING_ID_1))),
            Storage(CALENDAR0),
        )
    )

    private fun create(purpose: BuildingPurpose = SingleFamilyHouse, date: Date = DAY0) =
        Building(BUILDING_ID_0, constructionDate = date, purpose = purpose)
}