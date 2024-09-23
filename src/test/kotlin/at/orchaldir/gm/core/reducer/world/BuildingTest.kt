package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.assertIllegalState
import at.orchaldir.gm.core.action.AddBuilding
import at.orchaldir.gm.core.action.DeleteBuilding
import at.orchaldir.gm.core.action.UpdateBuilding
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.time.Day
import at.orchaldir.gm.core.model.time.Time
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

private val ID0 = BuildingId(0)
private val TOWN0 = TownId(0)
private val STREET0 = StreetId(0)
private val BUILDING_TILE = TownTile(construction = BuildingTile(ID0))
private val STREET_TILE = TownTile(construction = StreetTile(STREET0))
private val BIG_SIZE = MapSize2d(2, 1)
private val BIG_SQUARE = square(2)
private val DAY0 = Day(100)
private val DAY1 = Day(200)
private val CHARACTER0 = CharacterId(2)
private val CHARACTER1 = CharacterId(3)

class BuildingTest {

    @Nested
    inner class AddBuildingTest {

        @Test
        fun `Cannot update unknown town`() {
            val action = AddBuilding(TOWN0, 0, square(1))

            assertIllegalArgument("Unknown Town 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Tile is outside the map`() {
            val town = Town(TOWN0)
            val state = State(Storage(town))
            val action = AddBuilding(TOWN0, 100, square(1))

            assertIllegalState("Lot with index 100 is outside the map!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Tile is already a building`() {
            testTileNotEmpty(BUILDING_TILE)
        }

        @Test
        fun `Tile is already a street`() {
            testTileNotEmpty(STREET_TILE)
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
            testBigLotNotEmpty(BUILDING_TILE)
        }

        @Test
        fun `Big lot has a street`() {
            testBigLotNotEmpty(STREET_TILE)
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

        @Test
        fun `Cannot update unknown town`() {
            val building = Building(ID0, lot = BuildingLot(TOWN0))
            val state = State(listOf(Storage(building)))
            val action = DeleteBuilding(ID0)

            assertIllegalArgument("Unknown Town 0!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Tile delete unknown building`() {
            val town = Town(TOWN0)
            val state = State(Storage(town))
            val action = DeleteBuilding(ID0)

            assertIllegalArgument("Unknown Building 0!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Successfully removed a building`() {
            val building = Building(ID0, lot = BuildingLot(TOWN0))
            val town = Town(TOWN0, map = TileMap2d(BUILDING_TILE))
            val state = State(listOf(Storage(building), Storage(town)))
            val action = DeleteBuilding(ID0)

            val result = REDUCER.invoke(state, action).first

            assertFalse(result.getBuildingStorage().contains(ID0))
            assertFree(result, square(1))
        }

        @Test
        fun `Successfully removed a big building`() {
            val building = Building(ID0, lot = BuildingLot(TOWN0, 0, BIG_SIZE))
            val town =
                Town(TOWN0, map = TileMap2d(BIG_SQUARE, listOf(BUILDING_TILE, BUILDING_TILE, TownTile(), TownTile())))
            val state = State(listOf(Storage(building), Storage(town)))
            val action = DeleteBuilding(ID0)

            val result = REDUCER.invoke(state, action).first

            assertFalse(result.getBuildingStorage().contains(ID0))
            assertFree(result, BIG_SQUARE)
        }

        @Test
        fun `Successfully removed a building in multiple places`() {
            val building = Building(ID0, lot = BuildingLot(TOWN0))
            val town =
                Town(TOWN0, map = TileMap2d(BIG_SQUARE, listOf(BUILDING_TILE, TownTile(), TownTile(), BUILDING_TILE)))
            val state = State(listOf(Storage(building), Storage(town)))
            val action = DeleteBuilding(ID0)

            val result = REDUCER.invoke(state, action).first

            assertFalse(result.getBuildingStorage().contains(ID0))
            assertFree(result, BIG_SQUARE)
        }
    }

    @Nested
    inner class UpdateTest {

        private val OWNERSHIP = Ownership(OwnedByCharacter(CHARACTER0))
        private val OWNED_BY_TOWN = Ownership(OwnedByTown(TOWN0))
        private val CHARACTER_AS_PREVIOUS =
            Ownership(OwnedByTown(TOWN0), PreviousOwner(OwnedByCharacter(CHARACTER0), DAY1))
        private val TOWN_AS_PREVIOUS = Ownership(OwnedByCharacter(CHARACTER0), PreviousOwner(OwnedByTown(TOWN0), DAY1))

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateBuilding(ID0, "New", DAY0, OWNERSHIP)
            val state = State(Storage(Character(CHARACTER0)))

            assertIllegalArgument("Unknown Building 0!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Owner is an unknown character`() {
            val action = UpdateBuilding(ID0, "New", DAY0, OWNERSHIP)
            val state = State(listOf(Storage(Building(ID0))))

            assertIllegalArgument("Cannot use an unknown character 2 as owner!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Owner is an unknown town`() {
            val action = UpdateBuilding(ID0, "New", DAY0, OWNED_BY_TOWN)
            val state = State(listOf(Storage(Building(ID0))))

            assertIllegalArgument("Cannot use an unknown town 0 as owner!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Previous owner is an unknown character`() {
            val action = UpdateBuilding(ID0, "New", DAY0, CHARACTER_AS_PREVIOUS)
            val state = State(listOf(Storage(Building(ID0)), Storage(Town(TOWN0))))

            assertIllegalArgument("Cannot use an unknown character 2 as previous owner!") {
                REDUCER.invoke(
                    state,
                    action
                )
            }
        }

        @Test
        fun `Previous owner is an unknown town`() {
            val action = UpdateBuilding(ID0, "New", DAY0, TOWN_AS_PREVIOUS)
            val state = State(listOf(Storage(Building(ID0)), Storage(Character(CHARACTER0))))

            assertIllegalArgument("Cannot use an unknown town 0 as previous owner!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Successfully updated with character as owner`() {
            val action = UpdateBuilding(ID0, "New", DAY0, OWNERSHIP)
            val state = State(listOf(Storage(Building(ID0)), Storage(Character(CHARACTER0))))

            assertEquals(
                Building(ID0, "New", constructionDate = DAY0, ownership = OWNERSHIP),
                REDUCER.invoke(state, action).first.getBuildingStorage().get(ID0)
            )
        }

        @Test
        fun `Successfully updated with town as owner`() {
            val action = UpdateBuilding(ID0, "New", DAY0, OWNED_BY_TOWN)
            val state = State(listOf(Storage(Building(ID0)), Storage(Town(TOWN0))))

            assertEquals(
                Building(ID0, "New", constructionDate = DAY0, ownership = OWNED_BY_TOWN),
                REDUCER.invoke(state, action).first.getBuildingStorage().get(ID0)
            )
        }

        @Test
        fun `Successfully updated with character as previous owner`() {
            val action = UpdateBuilding(ID0, "New", DAY0, CHARACTER_AS_PREVIOUS)
            val state = State(listOf(Storage(Building(ID0)), Storage(Character(CHARACTER0)), Storage(Town(TOWN0))))

            assertEquals(
                Building(ID0, "New", constructionDate = DAY0, ownership = CHARACTER_AS_PREVIOUS),
                REDUCER.invoke(state, action).first.getBuildingStorage().get(ID0)
            )
        }
    }

    private fun assertFree(result: State, mapSize: MapSize2d) {
        assertEquals(TileMap2d(mapSize, TownTile()), result.getTownStorage().getOrThrow(TOWN0).map)
    }
}