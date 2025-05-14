package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteCharacter
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.organization.Organization
import at.orchaldir.gm.core.model.realm.Realm
import at.orchaldir.gm.core.model.time.date.Day
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.model.world.street.StreetTemplate
import at.orchaldir.gm.core.model.world.town.StreetTile
import at.orchaldir.gm.core.model.world.town.Town
import at.orchaldir.gm.core.model.world.town.TownTile
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.map.MapSize2d
import at.orchaldir.gm.utils.map.TileMap2d
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class OwnerTest {

    private val STREET_TILE_0 = TownTile(construction = StreetTile(STREET_TYPE_ID_0))
    private val STREET_TILE_1 = TownTile(construction = StreetTile(STREET_TYPE_ID_1))

    private val STATE = State(
        listOf(
            Storage(CALENDAR0),
            Storage(Business(BUSINESS_ID_0, startDate = DAY0)),
            Storage(Character(CHARACTER_ID_2, birthDate = DAY0)),
            Storage(Organization(ORGANIZATION_ID_0, date = DAY0)),
            Storage(Realm(REALM_ID_0, date = DAY0)),
            Storage(listOf(StreetTemplate(STREET_TYPE_ID_0), StreetTemplate(STREET_TYPE_ID_0))),
            Storage(
                Town(
                    TOWN_ID_0,
                    foundingDate = DAY0,
                    map = TileMap2d(MapSize2d(2, 1), listOf(STREET_TILE_0, STREET_TILE_1))
                )
            ),
        )
    )
    private val OWNED_BY_BUSINESS = History<Owner>(OwnedByBusiness(BUSINESS_ID_0))
    private val OWNED_BY_CHARACTER = History<Owner>(OwnedByCharacter(CHARACTER_ID_2))
    private val OWNED_BY_ORGANIZATION = History<Owner>(OwnedByOrganization(ORGANIZATION_ID_0))
    private val OWNED_BY_REALM = History<Owner>(OwnedByRealm(REALM_ID_0))
    private val OWNED_BY_TOWN = History<Owner>(OwnedByTown(TOWN_ID_0))
    private val CHARACTER_AS_PREVIOUS = History(
        OwnedByTown(TOWN_ID_0),
        HistoryEntry(OwnedByCharacter(CHARACTER_ID_2), DAY1),
    )
    private val ORGANIZATION_AS_PREVIOUS = History(
        OwnedByCharacter(CHARACTER_ID_2),
        HistoryEntry(OwnedByOrganization(ORGANIZATION_ID_0), DAY1),
    )
    private val TOWN_AS_PREVIOUS = History(
        OwnedByCharacter(CHARACTER_ID_2),
        HistoryEntry(OwnedByTown(TOWN_ID_0), DAY1),
    )

    @Nested
    inner class CanDeleteOwnerTest {
        private val action = DeleteCharacter(CHARACTER_ID_2)
        val ownedByCharacter = OwnedByCharacter(CHARACTER_ID_2)
        private val owner = History<Owner>(ownedByCharacter)
        private val previousOwner = History(UndefinedOwner, listOf(HistoryEntry(ownedByCharacter, Day(0))))

        @Test
        fun `Owns a building`() {
            val building = Building(BUILDING_ID_0, ownership = owner)
            val newState = STATE.updateStorage(Storage(building))

            assertIllegalArgument("Cannot delete Character 2, because of owned elements (Building)!") {
                REDUCER.invoke(newState, action)
            }
        }

        @Test
        fun `Owned a building`() {
            val building = Building(BUILDING_ID_0, ownership = previousOwner)
            val newState = STATE.updateStorage(Storage(building))

            assertIllegalArgument("Cannot delete Character 2, because of previously owned elements (Building)!") {
                REDUCER.invoke(newState, action)
            }
        }

        @Test
        fun `Owns a business`() {
            val business = Business(BUSINESS_ID_0, ownership = owner)
            val newState = STATE.updateStorage(Storage(business))

            assertIllegalArgument("Cannot delete Character 2, because of owned elements (Business)!") {
                REDUCER.invoke(newState, action)
            }
        }

        @Test
        fun `Owned a business`() {
            val business = Business(BUSINESS_ID_0, ownership = previousOwner)
            val newState = STATE.updateStorage(Storage(business))

            assertIllegalArgument("Cannot delete Character 2, because of previously owned elements (Business)!") {
                REDUCER.invoke(newState, action)
            }
        }
    }

    @Nested
    inner class CheckOwnershipTest {

        @Test
        fun `Owner is an unknown business`() {
            val state = STATE.removeStorage(BUSINESS_ID_0)

            assertIllegalArgument("Cannot use an unknown Business 0 as owner!") {
                checkOwnership(state, OWNED_BY_BUSINESS, DAY0)
            }
        }

        @Test
        fun `Owner is an unknown character`() {
            val state = STATE.removeStorage(CHARACTER_ID_0)

            assertIllegalArgument("Cannot use an unknown Character 2 as owner!") {
                checkOwnership(state, OWNED_BY_CHARACTER, DAY0)
            }
        }

        @Test
        fun `Owner is an unknown organization`() {
            val state = STATE.removeStorage(ORGANIZATION_ID_0)

            assertIllegalArgument("Cannot use an unknown Organization 0 as owner!") {
                checkOwnership(state, OWNED_BY_ORGANIZATION, DAY0)
            }
        }

        @Test
        fun `Owner is an unknown Realm`() {
            val state = STATE.removeStorage(REALM_ID_0)

            assertIllegalArgument("Cannot use an unknown Realm 0 as owner!") {
                checkOwnership(state, OWNED_BY_REALM, DAY0)
            }
        }

        @Test
        fun `Owner is an unknown town`() {
            val state = STATE.removeStorage(TOWN_ID_0)

            assertIllegalArgument("Cannot use an unknown Town 0 as owner!") {
                checkOwnership(state, OWNED_BY_TOWN, DAY0)
            }
        }

        @Test
        fun `Previous owner is an unknown character`() {
            val state = STATE.removeStorage(CHARACTER_ID_0)

            assertIllegalArgument("Cannot use an unknown Character 2 as 1.previous owner!") {
                checkOwnership(state, CHARACTER_AS_PREVIOUS, DAY0)
            }
        }

        @Test
        fun `Previous owner is an unknown organization`() {
            val state = STATE.removeStorage(ORGANIZATION_ID_0)

            assertIllegalArgument("Cannot use an unknown Organization 0 as 1.previous owner!") {
                checkOwnership(state, ORGANIZATION_AS_PREVIOUS, DAY0)
            }
        }

        @Test
        fun `Previous owner is an unknown town`() {
            val state = STATE.removeStorage(TOWN_ID_0)

            assertIllegalArgument("Cannot use an unknown Town 0 as 1.previous owner!") {
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
                OwnedByTown(TOWN_ID_0),
                listOf(
                    HistoryEntry(OwnedByCharacter(CHARACTER_ID_2), DAY2),
                    HistoryEntry(OwnedByTown(TOWN_ID_0), DAY1)
                )
            )

            assertIllegalArgument("2.previous owner's until is too early!") {
                checkOwnership(STATE, ownership, DAY0)
            }
        }

        @Test
        fun `Character owns a building before his birth`() {
            val state = STATE.updateStorage(Storage(Character(CHARACTER_ID_2, birthDate = DAY1)))

            assertIllegalArgument("The owner didn't exist at the start of their ownership!") {
                checkOwnership(state, OWNED_BY_CHARACTER, DAY0)
            }
        }

        @Test
        fun `First owner didn't exist yet`() {
            val state = STATE.updateStorage(Storage(Town(TOWN_ID_0, foundingDate = DAY1)))

            assertIllegalArgument("The 1.previous owner didn't exist at the start of their ownership!") {
                checkOwnership(state, TOWN_AS_PREVIOUS, DAY0)
            }
        }

        @Test
        fun `Second owner didn't exist yet`() {
            val ownership = History(
                NoOwner,
                listOf(
                    HistoryEntry(OwnedByTown(TOWN_ID_0), DAY1),
                    HistoryEntry(OwnedByCharacter(CHARACTER_ID_2), DAY2)
                )
            )
            val state = STATE.updateStorage(Storage(Character(CHARACTER_ID_2, birthDate = DAY2)))

            assertIllegalArgument("The 2.previous owner didn't exist at the start of their ownership!") {
                checkOwnership(state, ownership, DAY0)
            }
        }

        @Test
        fun `Successfully updated with business as owner`() {
            testSuccess(OWNED_BY_BUSINESS)
        }

        @Test
        fun `Successfully updated with character as owner`() {
            testSuccess(OWNED_BY_CHARACTER)
        }

        @Test
        fun `Successfully updated with organization as owner`() {
            testSuccess(OWNED_BY_ORGANIZATION)
        }

        @Test
        fun `Successfully updated with realm as owner`() {
            testSuccess(OWNED_BY_REALM)
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
        fun `Successfully updated with organization as previous owner`() {
            testSuccess(ORGANIZATION_AS_PREVIOUS)
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
                        HistoryEntry(OwnedByTown(TOWN_ID_0), DAY1),
                        HistoryEntry(OwnedByCharacter(CHARACTER_ID_2), DAY2)
                    )
                )
            )
        }

        private fun testSuccess(ownership: History<Owner>) {
            checkOwnership(STATE, ownership, DAY0)
        }
    }
}