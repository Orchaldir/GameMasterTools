package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.AddBuilding
import at.orchaldir.gm.core.action.DeleteBuilding
import at.orchaldir.gm.core.action.UpdateBuilding
import at.orchaldir.gm.core.action.UpdateBuildingLot
import at.orchaldir.gm.core.model.Data
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.time.Time
import at.orchaldir.gm.core.model.time.date.Day
import at.orchaldir.gm.core.model.time.date.Year
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.model.world.building.*
import at.orchaldir.gm.core.model.world.street.Street
import at.orchaldir.gm.core.model.world.street.StreetId
import at.orchaldir.gm.core.model.world.town.*
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.map.MapSize2d
import at.orchaldir.gm.utils.map.MapSize2d.Companion.square
import at.orchaldir.gm.utils.map.TileMap2d
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

class BuildingTest {

    private val BUILDING_TILE_0 = TownTile(construction = BuildingTile(BUILDING_ID_0))
    private val BUILDING_TILE_1 = TownTile(construction = BuildingTile(BUILDING_ID_1))
    private val STREET_TILE_0 = TownTile(construction = StreetTile(STREET_TYPE_ID_0, STREET_ID_0))
    private val STREET_TILE_1 = TownTile(construction = StreetTile(STREET_TYPE_ID_1, STREET_ID_1))
    private val BIG_SIZE = MapSize2d(2, 1)
    private val BIG_SQUARE = square(2)
    private val OWNERSHIP = History<Owner>(UndefinedOwner)
    private val data = Data(time = Time(currentDate = Day(42)))

    @Nested
    inner class AddBuildingTest {

        @Test
        fun `Cannot update unknown town`() {
            val action = AddBuilding(TOWN_MAP_ID_0, 0, square(1))

            assertIllegalArgument("Requires unknown Town Map 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Tile is outside the map`() {
            val town = TownMap(TOWN_MAP_ID_0)
            val state = State(Storage(town))
            val action = AddBuilding(TOWN_MAP_ID_0, 100, square(1))

            assertIllegalState("Lot with index 100 & size 1 x 1 is outside the map!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Big lot is outside the map`() {
            val town = TownMap(TOWN_MAP_ID_0)
            val state = State(Storage(town))
            val action = AddBuilding(TOWN_MAP_ID_0, 9, square(2))

            assertIllegalState("Lot with index 9 & size 2 x 2 is outside the map!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Tile is already a building`() {
            testTileNotEmpty(BUILDING_TILE_0)
        }

        @Test
        fun `Tile is already a street`() {
            testTileNotEmpty(STREET_TILE_0)
        }

        private fun testTileNotEmpty(townTile: TownTile) {
            val map = TileMap2d(townTile)
            val town = TownMap(TOWN_MAP_ID_0, map = map)
            val state = State(listOf(Storage(listOf(Street(STREET_ID_0))), Storage(town)))
            val action = AddBuilding(TOWN_MAP_ID_0, 0, square(1))

            assertIllegalArgument("Tile 0 is not empty!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Big lot has another building`() {
            testBigLotNotEmpty(BUILDING_TILE_0)
        }

        @Test
        fun `Big lot has a street`() {
            testBigLotNotEmpty(STREET_TILE_0)
        }

        private fun testBigLotNotEmpty(townTile: TownTile) {
            val map = TileMap2d(BIG_SIZE, listOf(TownTile(), townTile))
            val town = TownMap(TOWN_MAP_ID_0, map = map)
            val state = State(listOf(Storage(listOf(Street(STREET_ID_0))), Storage(town)))
            val action = AddBuilding(TOWN_MAP_ID_0, 0, BIG_SIZE)

            assertIllegalArgument("Tile 1 is not empty!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Successfully added a building`() {
            val map = TileMap2d(TownTile())
            val town = TownMap(TOWN_MAP_ID_0, map = map)
            val state = State(Storage(town), data = data)
            val action = AddBuilding(TOWN_MAP_ID_0, 0, square(1))

            val result = REDUCER.invoke(state, action).first

            assertEquals(
                Building(BUILDING_ID_0, lot = BuildingLot(TOWN_MAP_ID_0, 0, square(1)), constructionDate = Day(42)),
                result.getBuildingStorage().getOrThrow(BUILDING_ID_0)
            )
            assertEquals(
                BuildingTile(BUILDING_ID_0),
                result.getTownMapStorage().get(TOWN_MAP_ID_0)?.map?.getTile(0)?.construction
            )
        }

        @Test
        fun `Successfully added a big building`() {
            val map = TileMap2d(BIG_SQUARE, TownTile())
            val town = TownMap(TOWN_MAP_ID_0, map = map)
            val state = State(Storage(town), data = data)
            val action = AddBuilding(TOWN_MAP_ID_0, 0, BIG_SIZE)

            val result = REDUCER.invoke(state, action).first
            val tilemap = result.getTownMapStorage().getOrThrow(TOWN_MAP_ID_0).map

            assertEquals(
                Building(BUILDING_ID_0, lot = BuildingLot(TOWN_MAP_ID_0, 0, BIG_SIZE), constructionDate = Day(42)),
                result.getBuildingStorage().getOrThrow(BUILDING_ID_0)
            )
            assertEquals(BuildingTile(BUILDING_ID_0), tilemap.getRequiredTile(0).construction)
            assertEquals(BuildingTile(BUILDING_ID_0), tilemap.getRequiredTile(1).construction)
            assertEquals(NoConstruction, tilemap.getRequiredTile(2).construction)
            assertEquals(NoConstruction, tilemap.getRequiredTile(3).construction)
        }
    }

    @Nested
    inner class DeleteBuildingTileTest {

        private val building = Building(BUILDING_ID_0, lot = BuildingLot(TOWN_MAP_ID_0))
        private val town = TownMap(TOWN_MAP_ID_0, map = TileMap2d(BUILDING_TILE_0))
        private val state = State(listOf(Storage(building), Storage(town)))
        private val action = DeleteBuilding(BUILDING_ID_0)

        @Test
        fun `Cannot update unknown town`() {
            val state = State(listOf(Storage(building)))

            assertIllegalArgument("Requires unknown Town Map 0!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Tile delete unknown building`() {
            val town = TownMap(TOWN_MAP_ID_0)
            val state = State(Storage(town))

            assertIllegalArgument("Requires unknown Building 0!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Successfully removed a building`() {
            val result = REDUCER.invoke(state, action).first

            assertFalse(result.getBuildingStorage().contains(BUILDING_ID_0))
            assertFree(result, square(1))
        }

        @Test
        fun `Successfully removed a big building`() {
            val building = Building(BUILDING_ID_0, lot = BuildingLot(TOWN_MAP_ID_0, 0, BIG_SIZE))
            val town =
                TownMap(
                    TOWN_MAP_ID_0,
                    map = TileMap2d(BIG_SQUARE, listOf(BUILDING_TILE_0, BUILDING_TILE_0, TownTile(), TownTile()))
                )
            val state = State(listOf(Storage(building), Storage(town)))

            val result = REDUCER.invoke(state, action).first

            assertFalse(result.getBuildingStorage().contains(BUILDING_ID_0))
            assertFree(result, BIG_SQUARE)
        }

        @Test
        fun `Successfully removed a building in multiple places`() {
            val town =
                TownMap(
                    TOWN_MAP_ID_0,
                    map = TileMap2d(BIG_SQUARE, listOf(BUILDING_TILE_0, TownTile(), TownTile(), BUILDING_TILE_0))
                )
            val state = State(listOf(Storage(building), Storage(town)))

            val result = REDUCER.invoke(state, action).first

            assertFalse(result.getBuildingStorage().contains(BUILDING_ID_0))
            assertFree(result, BIG_SQUARE)
        }

        @Test
        fun `Cannot delete a single family house, if someone lives inside`() {
            val state =
                state.updateStorage(Storage(Character(CHARACTER_ID_0, housingStatus = History(InHouse(BUILDING_ID_0)))))

            assertIllegalArgument("Cannot delete Building 0, because it has inhabitants!") {
                REDUCER.invoke(state, action)
            }
        }

        @Test
        fun `Cannot delete a single family house, if someone lived inside`() {
            val housingStatus = History(Homeless, HistoryEntry(InHouse(BUILDING_ID_0), DAY0))
            val state = state.updateStorage(Storage(Character(CHARACTER_ID_0, housingStatus = housingStatus)))

            assertIllegalArgument("Cannot delete Building 0, because it had inhabitants!") {
                REDUCER.invoke(state, action)
            }
        }
    }

    @Nested
    inner class UpdateTest {

        private val UNKNOWN_STREET = StreetId(99)
        private val STREET_NOT_IN_TOWN = StreetId(199)
        private val STYLE = ArchitecturalStyleId(0)
        private val UNKNOWN_STYLE = ArchitecturalStyleId(1)
        private val STATE = State(
            listOf(
                Storage(listOf(ArchitecturalStyle(STYLE, start = YEAR0))),
                Storage(listOf(Building(BUILDING_ID_0, style = STYLE), Building(BUILDING_ID_1, style = STYLE))),
                Storage(CALENDAR0),
                Storage(Character(CHARACTER_ID_0, birthDate = DAY0)),
                Storage(listOf(Street(STREET_ID_0), Street(STREET_ID_1), Street(STREET_NOT_IN_TOWN))),
                Storage(TownMap(TOWN_MAP_ID_0, map = TileMap2d(MapSize2d(2, 1), listOf(STREET_TILE_0, STREET_TILE_1)))),
            )
        )
        private val OWNED_BY_CHARACTER = History<Owner>(OwnedByCharacter(CHARACTER_ID_0))
        private val ACTION = UpdateBuilding(
            BUILDING_ID_0,
            Name.init("New"),
            NoAddress,
            DAY0,
            OWNED_BY_CHARACTER,
            STYLE,
            SingleFamilyHouse,
            UndefinedCreator
        )

        @Test
        fun `Cannot update unknown id`() {
            val state = STATE.removeStorage(BUILDING_ID_0)

            assertIllegalArgument("Requires unknown Building 0!") { REDUCER.invoke(state, ACTION) }
        }

        @Test
        fun `Architectural style is unknown`() {
            val action = ACTION.copy(style = UNKNOWN_STYLE)

            assertIllegalArgument("Requires unknown Architectural Style 1!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Architectural style didn't exist yet`() {
            val state = STATE.updateStorage(Storage(ArchitecturalStyle(STYLE, start = Year(2000))))

            assertIllegalArgument("Architectural Style 0 didn't exist yet, when building 0 was build!") {
                REDUCER.invoke(
                    state,
                    ACTION
                )
            }
        }

        @Test
        fun `Owner is an unknown character`() {
            val state = STATE.removeStorage(CHARACTER_ID_0)

            assertIllegalArgument("Cannot use an unknown Character 0 as owner!") { REDUCER.invoke(state, ACTION) }
        }

        @Test
        fun `Founder is an unknown character`() {
            val action = ACTION.copy(builder = CreatedByCharacter(CHARACTER_ID_0), ownership = History(UndefinedOwner))
            val state = STATE.removeStorage(CHARACTER_TYPE)

            assertIllegalArgument("Cannot use an unknown Character 0 as Builder!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Date is in the future`() {
            val action = ACTION.copy(constructionDate = FUTURE_DAY_0)

            assertIllegalArgument("Date (Building) is in the future!") { REDUCER.invoke(STATE, action) }
        }

        @Nested
        inner class NameTest {

            @Test
            fun `Null is valid`() {
                val action = ACTION.copy(name = null)

                assertNull(REDUCER.invoke(STATE, action).first.getBuildingStorage().getOrThrow(BUILDING_ID_0).name)
            }
        }

        @Nested
        inner class AddressTest {

            private val action = UpdateBuilding(
                BUILDING_ID_0,
                Name.init("New"),
                NoAddress,
                DAY0,
                OWNERSHIP,
                STYLE,
                SingleFamilyHouse,
                UndefinedCreator
            )

            @Test
            fun `Updated crossing address`() {
                testSuccessful(CrossingAddress(listOf(STREET_ID_0, STREET_ID_1)))
            }

            @Test
            fun `Updated crossing address with the other order`() {
                testSuccessful(CrossingAddress(listOf(STREET_ID_1, STREET_ID_0)))
            }

            @Test
            fun `Can reuse the same crossing address`() {
                val address = CrossingAddress(listOf(STREET_ID_0, STREET_ID_1))
                val state = testSuccessful(address)

                testSuccessful(address, state, BUILDING_ID_1)
            }

            @Test
            fun `Cannot add the same street multiple times to a crossing`() {
                val address = CrossingAddress(listOf(STREET_ID_1, STREET_ID_1))
                val action = action.copy(address = address)

                assertIllegalArgument("List of streets contains duplicates!") { REDUCER.invoke(STATE, action) }
            }

            @Test
            fun `Street of crossing must be part of the town`() {
                val address = CrossingAddress(listOf(STREET_ID_0, STREET_NOT_IN_TOWN))
                val action = action.copy(address = address)

                assertIllegalArgument("Street 199 is not part of town 0!") { REDUCER.invoke(STATE, action) }
            }

            @Test
            fun `A crossing with an unknown street`() {
                val address = CrossingAddress(listOf(STREET_ID_0, UNKNOWN_STREET))
                val action = action.copy(address = address)

                assertIllegalArgument("Requires unknown Street 99!") { REDUCER.invoke(STATE, action) }
            }

            @Test
            fun `Updated street address`() {
                testSuccessful(StreetAddress(STREET_ID_0, 1))
            }

            @Test
            fun `A street address with an unknown street`() {
                val address = StreetAddress(UNKNOWN_STREET, 1)
                val action = action.copy(address = address)

                assertIllegalArgument("Requires unknown Street 99!") { REDUCER.invoke(STATE, action) }
            }

            @Test
            fun `Street must be part of the town`() {
                val address = StreetAddress(STREET_NOT_IN_TOWN, 1)
                val action = action.copy(address = address)

                assertIllegalArgument("Street 199 is not part of town 0!") { REDUCER.invoke(STATE, action) }
            }

            @Test
            fun `Cannot reuse the same street address`() {
                val address = StreetAddress(STREET_ID_0, 1)
                val state = testSuccessful(address)
                val action = action.copy(id = BUILDING_ID_1, address = address)

                assertIllegalArgument("House number 1 already used for street 0!") { REDUCER.invoke(state, action) }
            }

            @Test
            fun `Can reuse the same street address for the same house`() {
                val address = StreetAddress(STREET_ID_0, 1)
                val state = testSuccessful(address)

                testSuccessful(address, state)
            }

            @Test
            fun `Can reuse the same street with a different house number`() {
                val state = testSuccessful(StreetAddress(STREET_ID_0, 1))

                testSuccessful(StreetAddress(STREET_ID_0, 2), state, BUILDING_ID_1)
            }

            @Test
            fun `Updated town address`() {
                testSuccessful(TownAddress(1))
            }

            @Test
            fun `Cannot reuse the same town address`() {
                val address = TownAddress(1)
                val state = testSuccessful(address)
                val action = action.copy(id = BUILDING_ID_1, address = address)

                assertIllegalArgument("House number 1 already used for the town!") { REDUCER.invoke(state, action) }
            }

            @Test
            fun `Can reuse the same town address for the same house`() {
                val address = TownAddress(1)
                val state = testSuccessful(address)

                testSuccessful(address, state)
            }

            private fun testSuccessful(address: Address, state: State = STATE, id: BuildingId = BUILDING_ID_0): State {
                val action =
                    UpdateBuilding(id, null, address, DAY0, OWNERSHIP, STYLE, SingleFamilyHouse, UndefinedCreator)

                val result = REDUCER.invoke(state, action).first

                assertEquals(
                    Building(id, null, address = address, constructionDate = DAY0, style = STYLE),
                    result.getBuildingStorage().get(id)
                )

                return result
            }
        }

        @Nested
        inner class PurposeTest {

            private val action0 = UpdateBuilding(
                BUILDING_ID_0,
                null,
                NoAddress,
                DAY0,
                OWNED_BY_CHARACTER,
                STYLE,
                SingleFamilyHouse,
                UndefinedCreator
            )

            @Test
            fun `A home needs to stay a home, while characters are living in it`() {
                val state = STATE.updateStorage(
                    Storage(
                        Character(
                            CHARACTER_ID_0,
                            birthDate = DAY0,
                            housingStatus = History(InHouse(BUILDING_ID_0))
                        )
                    )
                )
                val action = action0.copy(purpose = ApartmentHouse(3))

                assertIllegalArgument("Cannot change the purpose, while characters are living in it!") {
                    REDUCER.invoke(state, action)
                }
            }

            @Test
            fun `A home can become another type of home, while characters are living in it`() {
                val state = STATE.updateStorage(
                    Storage(
                        Character(
                            CHARACTER_ID_0,
                            birthDate = DAY0,
                            housingStatus = History(InHouse(BUILDING_ID_0))
                        )
                    )
                )
                val action = action0.copy(purpose = BusinessAndHome(BUSINESS_ID_0))

                assertEquals(
                    BusinessAndHome(BUSINESS_ID_0),
                    REDUCER.invoke(state, action).first.getBuildingStorage().getOrThrow(BUILDING_ID_0).purpose
                )
            }

            @Test
            fun `Cannot delete apartments, while characters are living in it`() {
                (3..4).forEach {
                    val state = STATE
                        .updateStorage(
                            Storage(
                                Character(
                                    CHARACTER_ID_0,
                                    birthDate = DAY0,
                                    housingStatus = History(InApartment(BUILDING_ID_0, 4))
                                )
                            )
                        )
                        .updateStorage(Storage(Building(BUILDING_ID_0, purpose = ApartmentHouse(5))))
                    val action = action0.copy(purpose = ApartmentHouse(it))

                    assertIllegalArgument("The apartment house 0 requires at least 5 apartments!") {
                        REDUCER.invoke(state, action)
                    }
                }
            }

            @Test
            fun `An apartment house requires at least 2 apartments`() {
                (-1..1).forEach {
                    val action = action0.copy(purpose = ApartmentHouse(it))

                    assertIllegalArgument("The apartment house 0 requires at least 2 apartments!") {
                        REDUCER.invoke(STATE, action)
                    }
                }
            }

        }
    }

    @Nested
    inner class UpdateLotTest {

        private val STATE = State(
            listOf(
                Storage(listOf(Building(BUILDING_ID_0), Building(BUILDING_ID_1))),
                Storage(
                    TownMap(
                        TOWN_MAP_ID_0,
                        map = TileMap2d(square(2), listOf(BUILDING_TILE_0, TownTile(), TownTile(), BUILDING_TILE_1))
                    )
                ),
            )
        )

        @Test
        fun `Cannot update unknown building`() {
            val action = UpdateBuildingLot(BUILDING_ID_0, 0, MapSize2d(2, 1))

            assertIllegalArgument("Requires unknown Building 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Resize is blocked by other building`() {
            val action = UpdateBuildingLot(BUILDING_ID_0, 0, MapSize2d(2, 2))

            assertIllegalArgument("Tile 3 is not empty!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Change nothing`() {
            val action = UpdateBuildingLot(BUILDING_ID_0, 0, square(1))

            assertEquals(STATE, REDUCER.invoke(STATE, action).first)
        }

        @Test
        fun `Move the building`() {
            assertSuccess(
                2, square(1),
                listOf(TownTile(), TownTile(), BUILDING_TILE_0, BUILDING_TILE_1)
            )
        }

        @Test
        fun `Resize the building`() {
            assertSuccess(
                0, MapSize2d(2, 1),
                listOf(BUILDING_TILE_0, BUILDING_TILE_0, TownTile(), BUILDING_TILE_1)
            )
        }

        private fun assertSuccess(tileIndex: Int, size: MapSize2d, tiles: List<TownTile>) {
            val action = UpdateBuildingLot(BUILDING_ID_0, tileIndex, size)
            val lot = BuildingLot(TOWN_MAP_ID_0, tileIndex, size)

            assertEquals(
                State(
                    listOf(
                        Storage(listOf(Building(BUILDING_ID_0, lot = lot), Building(BUILDING_ID_1))),
                        Storage(TownMap(TOWN_MAP_ID_0, map = TileMap2d(square(2), tiles)))
                    )
                ),
                REDUCER.invoke(STATE, action).first
            )
        }
    }

    private fun assertFree(result: State, mapSize: MapSize2d) {
        assertEquals(
            TileMap2d(mapSize, TownTile()),
            result.getTownMapStorage().getOrThrow(TOWN_MAP_ID_0).map,
        )
    }
}