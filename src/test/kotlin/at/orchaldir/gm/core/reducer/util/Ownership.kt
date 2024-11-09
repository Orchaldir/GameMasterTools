package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.calendar.Calendar
import at.orchaldir.gm.core.model.calendar.CalendarId
import at.orchaldir.gm.core.model.calendar.MonthDefinition
import at.orchaldir.gm.core.model.character.CHARACTER
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.time.Day
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.model.world.building.BuildingId
import at.orchaldir.gm.core.model.world.street.Street
import at.orchaldir.gm.core.model.world.street.StreetId
import at.orchaldir.gm.core.model.world.town.*
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.map.MapSize2d
import at.orchaldir.gm.utils.map.TileMap2d
import org.junit.jupiter.api.Test

private val ID0 = BuildingId(0)
private val ID1 = BuildingId(1)
private val TOWN0 = TownId(0)
private val STREET0 = StreetId(0)
private val STREET1 = StreetId(1)
private val STREET_TILE_0 = TownTile(construction = StreetTile(STREET0))
private val STREET_TILE_1 = TownTile(construction = StreetTile(STREET1))
private val DAY0 = Day(100)
private val DAY1 = Day(200)
private val DAY2 = Day(300)
private val CHARACTER0 = CharacterId(2)

private val CALENDAR = Calendar(CalendarId(0), months = listOf(MonthDefinition("a")))
private val STREET_NOT_IN_TOWN = StreetId(199)
private val STATE = State(
    listOf(
        Storage(listOf(Building(ID0), Building(ID1))),
        Storage(CALENDAR),
        Storage(Character(CHARACTER0)),
        Storage(listOf(Street(STREET0), Street(STREET1), Street(STREET_NOT_IN_TOWN))),
        Storage(Town(TOWN0, map = TileMap2d(MapSize2d(2, 1), listOf(STREET_TILE_0, STREET_TILE_1)))),
    )
)
private val OWNED_BY_CHARACTER = History<Owner>(OwnedByCharacter(CHARACTER0))
private val OWNED_BY_TOWN = History<Owner>(OwnedByTown(TOWN0))
private val CHARACTER_AS_PREVIOUS =
    History(OwnedByTown(TOWN0), HistoryEntry(OwnedByCharacter(CHARACTER0), DAY1))
private val TOWN_AS_PREVIOUS = History(OwnedByCharacter(CHARACTER0), HistoryEntry(OwnedByTown(TOWN0), DAY1))

class OwnerTest {

    @Test
    fun `Owner is an unknown character`() {
        val state = STATE.removeStorage(CHARACTER)

        assertIllegalArgument("Cannot use an unknown character 2 as owner!") {
            checkOwnership(
                state,
                OWNED_BY_CHARACTER,
                DAY0
            )
        }
    }

    @Test
    fun `Owner is an unknown town`() {
        val state = STATE.removeStorage(TOWN)

        assertIllegalArgument("Cannot use an unknown town 0 as owner!") { checkOwnership(state, OWNED_BY_TOWN, DAY0) }
    }

    @Test
    fun `Previous owner is an unknown character`() {
        val state = STATE.removeStorage(CHARACTER)

        assertIllegalArgument("Cannot use an unknown character 2 as 1.previous owner!") {
            checkOwnership(state, CHARACTER_AS_PREVIOUS, DAY0)
        }
    }

    @Test
    fun `Previous owner is an unknown town`() {
        val state = STATE.removeStorage(TOWN)

        assertIllegalArgument("Cannot use an unknown town 0 as 1.previous owner!") {
            checkOwnership(state, TOWN_AS_PREVIOUS, DAY0)
        }
    }

    @Test
    fun `First Previous ownership ended before the construction`() {
        assertIllegalArgument("1.previous owner's until is too early!") {
            checkOwnership(
                STATE,
                CHARACTER_AS_PREVIOUS,
                DAY2
            )
        }
    }

    @Test
    fun `A previous ownership ended before the one before it`() {
        val ownership = History(
            OwnedByTown(TOWN0),
            listOf(
                HistoryEntry(OwnedByCharacter(CHARACTER0), DAY2),
                HistoryEntry(OwnedByTown(TOWN0), DAY1)
            )
        )

        assertIllegalArgument("2.previous owner's until is too early!") {
            checkOwnership(STATE, ownership, DAY0)
        }
    }

    @Test
    fun `Character owns a building before his birth`() {
        val state = STATE.updateStorage(Storage(Character(CHARACTER0, birthDate = DAY1)))

        assertIllegalArgument("The owner didn't exist at the start of their ownership!") {
            checkOwnership(state, OWNED_BY_CHARACTER, DAY0)
        }
    }

    @Test
    fun `First owner didn't exist yet`() {
        val state = STATE.updateStorage(Storage(Town(TOWN0, foundingDate = DAY1)))

        assertIllegalArgument("The 1.previous owner didn't exist at the start of their ownership!") {
            checkOwnership(state, TOWN_AS_PREVIOUS, DAY0)
        }
    }

    @Test
    fun `Second owner didn't exist yet`() {
        val ownership = History(
            NoOwner,
            listOf(
                HistoryEntry(OwnedByTown(TOWN0), DAY1),
                HistoryEntry(OwnedByCharacter(CHARACTER0), DAY2)
            )
        )
        val state = STATE.updateStorage(Storage(Character(CHARACTER0, birthDate = DAY2)))

        assertIllegalArgument("The 2.previous owner didn't exist at the start of their ownership!") {
            checkOwnership(state, ownership, DAY0)
        }
    }

    @Test
    fun `Successfully updated with character as owner`() {
        testSuccess(OWNED_BY_CHARACTER)
    }

    @Test
    fun `Successfully updated with town as owner`() {
        testSuccess(OWNED_BY_TOWN)
    }

    @Test
    fun `Successfully updated with character as previous owner`() {
        testSuccess(CHARACTER_AS_PREVIOUS)
    }

    @Test
    fun `Successfully updated with town as previous owner`() {
        testSuccess(TOWN_AS_PREVIOUS)
    }

    @Test
    fun `Successfully updated with 2 previous owners`() {
        testSuccess(
            History(
                NoOwner,
                listOf(
                    HistoryEntry(OwnedByTown(TOWN0), DAY1),
                    HistoryEntry(OwnedByCharacter(CHARACTER0), DAY2)
                )
            )
        )
    }

    private fun testSuccess(ownership: History<Owner>) {
        checkOwnership(STATE, ownership, DAY0)
    }
}