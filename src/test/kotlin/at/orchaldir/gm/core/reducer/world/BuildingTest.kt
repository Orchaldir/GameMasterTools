package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.CALENDAR0
import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.assertIllegalState
import at.orchaldir.gm.core.action.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.time.Day
import at.orchaldir.gm.core.model.time.Time
import at.orchaldir.gm.core.model.time.Year
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

private val ID0 = BuildingId(0)
private val ID1 = BuildingId(1)
private val TOWN0 = TownId(0)
private val STREET0 = StreetId(0)
private val STREET1 = StreetId(1)
private val BUILDING_TILE_0 = TownTile(construction = BuildingTile(ID0))
private val BUILDING_TILE_1 = TownTile(construction = BuildingTile(ID1))
private val STREET_TILE_0 = TownTile(construction = StreetTile(STREET0))
private val STREET_TILE_1 = TownTile(construction = StreetTile(STREET1))
private val BIG_SIZE = MapSize2d(2, 1)
private val BIG_SQUARE = square(2)
private val DAY0 = Day(100)
private val CHARACTER0 = CharacterId(2)
private val OWNERSHIP = History<Owner>(UnknownOwner)

class BuildingTest {

    @Nested
    inner class AddBuildingTest {

        @Test
        fun `Cannot update unknown town`() {
            val action = AddBuilding(TOWN0, 0, square(1))

            assertIllegalArgument("Requires unknown Town 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Tile is outside the map`() {
            val town = Town(TOWN0)
            val state = State(Storage(town))
            val action = AddBuilding(TOWN0, 100, square(1))

            assertIllegalState("Lot with index 100 & size 1 x 1 is outside the map!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Big lot is outside the map`() {
            val town = Town(TOWN0)
            val state = State(Storage(town))
            val action = AddBuilding(TOWN0, 9, square(2))

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
            val town = Town(TOWN0, map = map)
            val state = State(listOf(Storage(listOf(Street(STREET0))), Storage(town)))
            val action = AddBuilding(TOWN0, 0, square(1))

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
            val town = Town(TOWN0, map = map)
            val state = State(listOf(Storage(listOf(Street(STREET0))), Storage(town)))
            val action = AddBuilding(TOWN0, 0, BIG_SIZE)

            assertIllegalArgument("Tile 1 is not empty!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Successfully added a building`() {
            val map = TileMap2d(TownTile())
            val town = Town(TOWN0, map = map)
            val state = State(Storage(town), time = Time(currentDate = Day(42)))
            val action = AddBuilding(TOWN0, 0, square(1))

            val result = REDUCER.invoke(state, action).first

            assertEquals(
                Building(ID0, lot = BuildingLot(TOWN0, 0, square(1)), constructionDate = Day(42)),
                result.getBuildingStorage().getOrThrow(ID0)
            )
            assertEquals(BuildingTile(ID0), result.getTownStorage().get(TOWN0)?.map?.getTile(0)?.construction)
        }

        @Test
        fun `Successfully added a big building`() {
            val map = TileMap2d(BIG_SQUARE, TownTile())
            val town = Town(TOWN0, map = map)
            val state = State(Storage(town), time = Time(currentDate = Day(42)))
            val action = AddBuilding(TOWN0, 0, BIG_SIZE)

            val result = REDUCER.invoke(state, action).first
            val tilemap = result.getTownStorage().getOrThrow(TOWN0).map

            assertEquals(
                Building(ID0, lot = BuildingLot(TOWN0, 0, BIG_SIZE), constructionDate = Day(42)),
                result.getBuildingStorage().getOrThrow(ID0)
            )
            assertEquals(BuildingTile(ID0), tilemap.getRequiredTile(0).construction)
            assertEquals(BuildingTile(ID0), tilemap.getRequiredTile(1).construction)
            assertEquals(NoConstruction, tilemap.getRequiredTile(2).construction)
            assertEquals(NoConstruction, tilemap.getRequiredTile(3).construction)
        }
    }

    @Nested
    inner class DeleteBuildingTileTest {

        private val building = Building(ID0, lot = BuildingLot(TOWN0))
        private val town = Town(TOWN0, map = TileMap2d(BUILDING_TILE_0))
        private val state = State(listOf(Storage(building), Storage(town)))
        private val action = DeleteBuilding(ID0)

        @Test
        fun `Cannot update unknown town`() {
            val state = State(listOf(Storage(building)))

            assertIllegalArgument("Requires unknown Town 0!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Tile delete unknown building`() {
            val town = Town(TOWN0)
            val state = State(Storage(town))

            assertIllegalArgument("Requires unknown Building 0!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Successfully removed a building`() {
            val result = REDUCER.invoke(state, action).first

            assertFalse(result.getBuildingStorage().contains(ID0))
            assertFree(result, square(1))
        }

        @Test
        fun `Successfully removed a big building`() {
            val building = Building(ID0, lot = BuildingLot(TOWN0, 0, BIG_SIZE))
            val town =
                Town(
                    TOWN0,
                    map = TileMap2d(BIG_SQUARE, listOf(BUILDING_TILE_0, BUILDING_TILE_0, TownTile(), TownTile()))
                )
            val state = State(listOf(Storage(building), Storage(town)))

            val result = REDUCER.invoke(state, action).first

            assertFalse(result.getBuildingStorage().contains(ID0))
            assertFree(result, BIG_SQUARE)
        }

        @Test
        fun `Successfully removed a building in multiple places`() {
            val town =
                Town(
                    TOWN0,
                    map = TileMap2d(BIG_SQUARE, listOf(BUILDING_TILE_0, TownTile(), TownTile(), BUILDING_TILE_0))
                )
            val state = State(listOf(Storage(building), Storage(town)))

            val result = REDUCER.invoke(state, action).first

            assertFalse(result.getBuildingStorage().contains(ID0))
            assertFree(result, BIG_SQUARE)
        }

        @Test
        fun `Cannot delete a single family house, if someone lives inside`() {
            val state = state.updateStorage(Storage(Character(CHARACTER0, livingStatus = History(InHouse(ID0)))))

            assertIllegalArgument("Cannot delete building 0, because it has inhabitants!") {
                REDUCER.invoke(state, action)
            }
        }

        @Test
        fun `Cannot delete a single family house, if someone lived inside`() {
            val livingStatus = History(Homeless, HistoryEntry(InHouse(ID0), DAY0))
            val state = state.updateStorage(Storage(Character(CHARACTER0, livingStatus = livingStatus)))

            assertIllegalArgument("Cannot delete building 0, because it had inhabitants!") {
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
                Storage(listOf(ArchitecturalStyle(STYLE))),
                Storage(listOf(Building(ID0), Building(ID1))),
                Storage(CALENDAR0),
                Storage(Character(CHARACTER0)),
                Storage(listOf(Street(STREET0), Street(STREET1), Street(STREET_NOT_IN_TOWN))),
                Storage(Town(TOWN0, map = TileMap2d(MapSize2d(2, 1), listOf(STREET_TILE_0, STREET_TILE_1)))),
            )
        )
        private val OWNED_BY_CHARACTER = History<Owner>(OwnedByCharacter(CHARACTER0))
        private val ACTION = UpdateBuilding(
            ID0,
            "New",
            NoAddress,
            DAY0,
            OWNED_BY_CHARACTER,
            STYLE,
            SingleFamilyHouse,
            UndefinedCreator
        )

        @Test
        fun `Cannot update unknown id`() {
            val state = STATE.removeStorage(BUILDING)

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
            val state = STATE.removeStorage(CHARACTER)

            assertIllegalArgument("Cannot use an unknown character 2 as owner!") { REDUCER.invoke(state, ACTION) }
        }

        @Test
        fun `Founder is an unknown character`() {
            val action = ACTION.copy(builder = CreatedByCharacter(CHARACTER0), ownership = History(UnknownOwner))
            val state = STATE.removeStorage(CHARACTER)

            assertIllegalArgument("Cannot use an unknown character 2 as Builder!") { REDUCER.invoke(state, action) }
        }

        @Nested
        inner class NameTest {

            @Test
            fun `An empty string is invalid`() {
                val action = ACTION.copy(name = "")

                assertIllegalArgument("Name is invalid!") {
                    REDUCER.invoke(STATE, action)
                }
            }

            @Test
            fun `Only whitespaces are invalid`() {
                val action = ACTION.copy(name = "  ")

                assertIllegalArgument("Name is invalid!") {
                    REDUCER.invoke(STATE, action)
                }
            }

            @Test
            fun `Null is valid`() {
                val action = ACTION.copy(name = null)

                assertNull(REDUCER.invoke(STATE, action).first.getBuildingStorage().getOrThrow(ID0).name)
            }
        }

        @Nested
        inner class AddressTest {

            @Test
            fun `Updated crossing address`() {
                testSuccessful(CrossingAddress(listOf(STREET0, STREET1)))
            }

            @Test
            fun `Updated crossing address with the other order`() {
                testSuccessful(CrossingAddress(listOf(STREET1, STREET0)))
            }

            @Test
            fun `Can reuse the same crossing address`() {
                val address = CrossingAddress(listOf(STREET0, STREET1))
                val state = testSuccessful(address)

                testSuccessful(address, state, ID1)
            }

            @Test
            fun `Cannot add the same street multiple times to a crossing`() {
                val address = CrossingAddress(listOf(STREET1, STREET1))
                val action =
                    UpdateBuilding(ID0, "New", address, DAY0, OWNERSHIP, STYLE, SingleFamilyHouse, UndefinedCreator)

                assertIllegalArgument("List of streets contains duplicates!") { REDUCER.invoke(STATE, action) }
            }

            @Test
            fun `Street of crossing must be part of the town`() {
                val address = CrossingAddress(listOf(STREET0, STREET_NOT_IN_TOWN))
                val action =
                    UpdateBuilding(ID0, "New", address, DAY0, OWNERSHIP, STYLE, SingleFamilyHouse, UndefinedCreator)

                assertIllegalArgument("Street 199 is not part of town 0!") { REDUCER.invoke(STATE, action) }
            }

            @Test
            fun `A crossing with an unknown street`() {
                val address = CrossingAddress(listOf(STREET0, UNKNOWN_STREET))
                val action =
                    UpdateBuilding(ID0, "New", address, DAY0, OWNERSHIP, STYLE, SingleFamilyHouse, UndefinedCreator)

                assertIllegalArgument("Requires unknown Street 99!") { REDUCER.invoke(STATE, action) }
            }

            @Test
            fun `Updated street address`() {
                testSuccessful(StreetAddress(STREET0, 1))
            }

            @Test
            fun `A street address with an unknown street`() {
                val address = StreetAddress(UNKNOWN_STREET, 1)
                val action =
                    UpdateBuilding(ID0, "New", address, DAY0, OWNERSHIP, STYLE, SingleFamilyHouse, UndefinedCreator)

                assertIllegalArgument("Requires unknown Street 99!") { REDUCER.invoke(STATE, action) }
            }

            @Test
            fun `Street must be part of the town`() {
                val address = StreetAddress(STREET_NOT_IN_TOWN, 1)
                val action =
                    UpdateBuilding(ID0, "New", address, DAY0, OWNERSHIP, STYLE, SingleFamilyHouse, UndefinedCreator)

                assertIllegalArgument("Street 199 is not part of town 0!") { REDUCER.invoke(STATE, action) }
            }

            @Test
            fun `Cannot reuse the same street address`() {
                val address = StreetAddress(STREET0, 1)
                val state = testSuccessful(address)
                val action =
                    UpdateBuilding(ID1, "B2", address, DAY0, OWNERSHIP, STYLE, SingleFamilyHouse, UndefinedCreator)

                assertIllegalArgument("House number 1 already used for street 0!") { REDUCER.invoke(state, action) }
            }

            @Test
            fun `Can reuse the same street address for the same house`() {
                val address = StreetAddress(STREET0, 1)
                val state = testSuccessful(address)

                testSuccessful(address, state)
            }

            @Test
            fun `Can reuse the same street with a different house number`() {
                val state = testSuccessful(StreetAddress(STREET0, 1))

                testSuccessful(StreetAddress(STREET0, 2), state, ID1)
            }

            @Test
            fun `Updated town address`() {
                testSuccessful(TownAddress(1))
            }

            @Test
            fun `Cannot reuse the same town address`() {
                val address = TownAddress(1)
                val state = testSuccessful(address)
                val action =
                    UpdateBuilding(ID1, "B2", address, DAY0, OWNERSHIP, STYLE, SingleFamilyHouse, UndefinedCreator)

                assertIllegalArgument("House number 1 already used for the town!") { REDUCER.invoke(state, action) }
            }

            @Test
            fun `Can reuse the same town address for the same house`() {
                val address = TownAddress(1)
                val state = testSuccessful(address)

                testSuccessful(address, state)
            }

            private fun testSuccessful(address: Address, state: State = STATE, id: BuildingId = ID0): State {
                val action =
                    UpdateBuilding(id, "New", address, DAY0, OWNERSHIP, STYLE, SingleFamilyHouse, UndefinedCreator)

                val result = REDUCER.invoke(state, action).first

                assertEquals(
                    Building(id, "New", address = address, constructionDate = DAY0),
                    result.getBuildingStorage().get(id)
                )

                return result
            }
        }

        @Nested
        inner class PurposeTest {

            @Test
            fun `Cannot change the purpose, while characters are living in it`() {
                val state = STATE.updateStorage(Storage(Character(CHARACTER0, livingStatus = History(InHouse(ID0)))))
                val action = UpdateBuilding(
                    ID0,
                    "New",
                    NoAddress,
                    DAY0,
                    OWNED_BY_CHARACTER,
                    STYLE,
                    ApartmentHouse(3),
                    UndefinedCreator
                )

                assertIllegalArgument("Cannot change the purpose, while characters are living in it!") {
                    REDUCER.invoke(state, action)
                }
            }

            @Test
            fun `Cannot delete apartments, while characters are living in it`() {
                (3..4).forEach {
                    val state = STATE
                        .updateStorage(Storage(Character(CHARACTER0, livingStatus = History(InApartment(ID0, 4)))))
                        .updateStorage(Storage(Building(ID0, purpose = ApartmentHouse(5))))
                    val action =
                        UpdateBuilding(
                            ID0,
                            "New",
                            NoAddress,
                            DAY0,
                            OWNED_BY_CHARACTER,
                            STYLE,
                            ApartmentHouse(it),
                            UndefinedCreator
                        )

                    assertIllegalArgument("The apartment house 0 requires at least 5 apartments!") {
                        REDUCER.invoke(state, action)
                    }
                }
            }

            @Test
            fun `An apartment house requires at least 2 apartments`() {
                (-1..1).forEach {
                    val action =
                        UpdateBuilding(
                            ID0,
                            "New",
                            NoAddress,
                            DAY0,
                            OWNED_BY_CHARACTER,
                            STYLE,
                            ApartmentHouse(it),
                            UndefinedCreator
                        )

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
                Storage(listOf(Building(ID0), Building(ID1))),
                Storage(
                    Town(
                        TOWN0,
                        map = TileMap2d(square(2), listOf(BUILDING_TILE_0, TownTile(), TownTile(), BUILDING_TILE_1))
                    )
                ),
            )
        )

        @Test
        fun `Cannot update unknown building`() {
            val action = UpdateBuildingLot(ID0, 0, MapSize2d(2, 1))

            assertIllegalArgument("Requires unknown Building 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Resize is blocked by other building`() {
            val action = UpdateBuildingLot(ID0, 0, MapSize2d(2, 2))

            assertIllegalArgument("Tile 3 is not empty!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Change nothing`() {
            val action = UpdateBuildingLot(ID0, 0, square(1))

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
            val action = UpdateBuildingLot(ID0, tileIndex, size)
            val lot = BuildingLot(TOWN0, tileIndex, size)

            assertEquals(
                State(
                    listOf(
                        Storage(listOf(Building(ID0, lot = lot), Building(ID1))),
                        Storage(Town(TOWN0, map = TileMap2d(square(2), tiles)))
                    )
                ),
                REDUCER.invoke(STATE, action).first
            )
        }
    }

    private fun assertFree(result: State, mapSize: MapSize2d) {
        assertEquals(TileMap2d(mapSize, TownTile()), result.getTownStorage().getOrThrow(TOWN0).map)
    }
}