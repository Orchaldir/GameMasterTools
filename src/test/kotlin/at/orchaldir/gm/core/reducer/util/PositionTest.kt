package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.District
import at.orchaldir.gm.core.model.realm.Realm
import at.orchaldir.gm.core.model.realm.Town
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.model.world.building.ApartmentHouse
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.model.world.building.BuildingPurpose
import at.orchaldir.gm.core.model.world.building.SingleFamilyHouse
import at.orchaldir.gm.core.model.world.town.TownMap
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class PositionTest {

    private val inApartment = InApartment(BUILDING_ID_0, 0)
    private val inBuilding = InBuilding(BUILDING_ID_0)
    private val inDistrict = InDistrict(DISTRICT_ID_0)
    private val inRealm = InRealm(REALM_ID_0)
    private val inTown = InTown(TOWN_ID_0)
    private val inTownMap = InTownMap(TOWN_MAP_ID_0, 0)

    @Nested
    inner class ApartmentTest {

        @Test
        fun `Cannot use unknown building as apartment house`() {
            val history = History<Position>(InApartment(UNKNOWN_BUILDING_ID, 9))

            assertIllegalArgument("Requires unknown home!") {
                checkPositionHistory(createState(), history, DAY0)
            }
        }

        @Test
        fun `Cannot use an apartment number higher than the building allows`() {
            val state = createState(ApartmentHouse(2))
            val history = History<Position>(InApartment(BUILDING_ID_0, 2))

            assertIllegalArgument("The home's apartment index is too high!") {
                checkPositionHistory(state, history, DAY0)
            }
        }

        @Test
        fun `The apartment house doesn't exist yet`() {
            val state = createState(ApartmentHouse(2), DAY1)

            assertIllegalArgument("The home doesn't exist at the required date!") {
                checkPositionHistory(state, History(inApartment), DAY0)
            }
        }

        @Test
        fun `Live in a valid apartment`() {
            val count = 3
            val state = createState(ApartmentHouse(count))

            repeat(count) {
                val history = History<Position>(InApartment(BUILDING_ID_0, it))

                checkPositionHistory(state, history, DAY0)
            }
        }
    }

    @Nested
    inner class HouseTest {

        @Test
        fun `Cannot use unknown building as home`() {
            val state = createState()
            val history = History<Position>(InBuilding(UNKNOWN_BUILDING_ID))

            assertIllegalArgument("Requires unknown home!") {
                checkPositionHistory(state, history, DAY0)
            }
        }

        @Test
        fun `Cannot use unknown building as a previous home`() {
            val state = createState()
            val entry = HistoryEntry<Position>(InBuilding(UNKNOWN_BUILDING_ID), DAY1)
            val history = History(inBuilding, entry)

            assertIllegalArgument("Requires unknown 1.previous home!") {
                checkPositionHistory(state, history, DAY0)
            }
        }

        @Test
        fun `Living in an house requires a house`() {
            val state = createState(ApartmentHouse(2))

            assertIllegalArgument("The home is not a home!") {
                checkPositionHistory(state, History(inBuilding), DAY0)
            }
        }

        @Test
        fun `The house doesn't exist yet`() {
            val state = createState(date = DAY1)

            assertIllegalArgument("The home doesn't exist at the required date!") {
                checkPositionHistory(state, History(inBuilding), DAY0)
            }
        }

        @Test
        fun `Live in a valid single family house`() {
            checkPositionHistory(createState(), History(inBuilding), DAY0)
        }
    }

    @Nested
    inner class DistrictTest {

        @Test
        fun `Cannot use unknown district as home`() {
            val history = History<Position>(InDistrict(UNKNOWN_DISTRICT_ID))

            assertIllegalArgument("Requires unknown home!") {
                checkPositionHistory(createState(), history, DAY0)
            }
        }

        @Test
        fun `Cannot use unknown district as a previous home`() {
            val entry = HistoryEntry<Position>(InDistrict(UNKNOWN_DISTRICT_ID), DAY1)
            val history = History(inBuilding, entry)

            assertIllegalArgument("Requires unknown 1.previous home!") {
                checkPositionHistory(createState(), history, DAY0)
            }
        }

        @Test
        fun `The district doesn't exist yet`() {
            val state = createDistrictState(date = DAY1)

            assertIllegalArgument("The home doesn't exist at the required date!") {
                checkPositionHistory(state, History(inDistrict), DAY0)
            }
        }

        @Test
        fun `Live in a valid district`() {
            checkPositionHistory(createDistrictState(), History(inDistrict), DAY0)
        }

        private fun createDistrictState(date: Date = DAY0) = State(
            listOf(
                Storage(Building(BUILDING_ID_0)),
                Storage(CALENDAR0),
                Storage(District(DISTRICT_ID_0, foundingDate = date)),
            )
        )
    }

    @Nested
    inner class RealmTest {

        @Test
        fun `Cannot use unknown realm as home`() {
            val history = History<Position>(InRealm(UNKNOWN_REALM_ID))

            assertIllegalArgument("Requires unknown home!") {
                checkPositionHistory(createState(), history, DAY0)
            }
        }

        @Test
        fun `Cannot use unknown realm as a previous home`() {
            val entry = HistoryEntry<Position>(InRealm(UNKNOWN_REALM_ID), DAY1)
            val history = History(inBuilding, entry)

            assertIllegalArgument("Requires unknown 1.previous home!") {
                checkPositionHistory(createState(), history, DAY0)
            }
        }

        @Test
        fun `The realm doesn't exist yet`() {
            val state = createRealmState(date = DAY1)

            assertIllegalArgument("The home doesn't exist at the required date!") {
                checkPositionHistory(state, History(inRealm), DAY0)
            }
        }

        @Test
        fun `Live in a valid realm`() {
            checkPositionHistory(createRealmState(), History(inRealm), DAY0)
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
            val history = History<Position>(InTown(UNKNOWN_TOWN_ID))

            assertIllegalArgument("Requires unknown home!") {
                checkPositionHistory(createState(), history, DAY0)
            }
        }

        @Test
        fun `Cannot use unknown town as a previous home`() {
            val entry = HistoryEntry<Position>(InTown(UNKNOWN_TOWN_ID), DAY1)
            val history = History(inBuilding, entry)

            assertIllegalArgument("Requires unknown 1.previous home!") {
                checkPositionHistory(createState(), history, DAY0)
            }
        }

        @Test
        fun `The town doesn't exist yet`() {
            val state = createTownState(date = DAY1)

            assertIllegalArgument("The home doesn't exist at the required date!") {
                checkPositionHistory(state, History(inTown), DAY0)
            }
        }

        @Test
        fun `Live in a valid town`() {
            checkPositionHistory(createTownState(), History(inTown), DAY0)
        }

        private fun createTownState(date: Date = DAY0) = State(
            listOf(
                Storage(Building(BUILDING_ID_0)),
                Storage(CALENDAR0),
                Storage(Town(TOWN_ID_0, foundingDate = date)),
            )
        )
    }

    @Nested
    inner class TownMapTest {
        val inUnknownTownMap = InTownMap(UNKNOWN_TOWN_MAP_ID, 0)

        @Test
        fun `Cannot use unknown town map as home`() {
            val history = History<Position>(inUnknownTownMap)

            assertIllegalArgument("Requires unknown home!") {
                checkPositionHistory(createState(), history, DAY0)
            }
        }

        @Test
        fun `Cannot use unknown town map as a previous home`() {
            val entry = HistoryEntry<Position>(inUnknownTownMap, DAY1)
            val history = History(inBuilding, entry)

            assertIllegalArgument("Requires unknown 1.previous home!") {
                checkPositionHistory(createState(), history, DAY0)
            }
        }

        @Test
        fun `The town map doesn't exist yet`() {
            val state = createTownMapState(date = DAY1)

            assertIllegalArgument("The home doesn't exist at the required date!") {
                checkPositionHistory(state, History(inTownMap), DAY0)
            }
        }

        @Test
        fun `Tile index must be positive`() {
            val state = createTownMapState()
            val inTownMap = InTownMap(TOWN_MAP_ID_0, -1)

            assertIllegalArgument("The home's tile index -1 is outside the town map!") {
                checkPositionHistory(state, History(inTownMap), DAY0)
            }
        }

        @Test
        fun `Tile index must be smaller than the size`() {
            val state = createTownMapState()
            val inTownMap = InTownMap(TOWN_MAP_ID_0, 100)

            assertIllegalArgument("The home's tile index 100 is outside the town map!") {
                checkPositionHistory(state, History(inTownMap), DAY0)
            }
        }

        @Test
        fun `Live in a valid town`() {
            checkPositionHistory(createTownMapState(), History(inTownMap), DAY0)
        }

        private fun createTownMapState(date: Date = DAY0) = State(
            listOf(
                Storage(Building(BUILDING_ID_0)),
                Storage(CALENDAR0),
                Storage(TownMap(TOWN_MAP_ID_0, date = date)),
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