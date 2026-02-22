package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.AgeViaBirthdate
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.organization.Organization
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.realm.Realm
import at.orchaldir.gm.core.model.realm.Settlement
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.model.world.street.StreetTemplate
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class OwnerTest {

    private val STATE = State(
        listOf(
            Storage(CALENDAR0),
            Storage(Business(BUSINESS_ID_0, date = DAY0)),
            Storage(Character(CHARACTER_ID_2)),
            Storage(Organization(ORGANIZATION_ID_0, date = DAY0)),
            Storage(Race(RACE_ID_0)),
            Storage(Realm(REALM_ID_0, date = DAY0)),
            Storage(listOf(StreetTemplate(STREET_TEMPLATE_ID_0), StreetTemplate(STREET_TEMPLATE_ID_0))),
            Storage(Settlement(SETTLEMENT_ID_0, date = DAY0)),
        )
    )
    private val OWNED_BY_BUSINESS = History<Reference>(BusinessReference(BUSINESS_ID_0))
    private val OWNED_BY_CHARACTER = History<Reference>(CharacterReference(CHARACTER_ID_2))
    private val OWNED_BY_ORGANIZATION = History<Reference>(OrganizationReference(ORGANIZATION_ID_0))
    private val OWNED_BY_REALM = History<Reference>(RealmReference(REALM_ID_0))
    private val OWNED_BY_TOWN = History<Reference>(SettlementReference(SETTLEMENT_ID_0))
    private val CHARACTER_AS_PREVIOUS = History(
        SettlementReference(SETTLEMENT_ID_0),
        HistoryEntry(CharacterReference(CHARACTER_ID_2), DAY1),
    )
    private val ORGANIZATION_AS_PREVIOUS = History(
        CharacterReference(CHARACTER_ID_2),
        HistoryEntry(OrganizationReference(ORGANIZATION_ID_0), DAY1),
    )
    private val TOWN_AS_PREVIOUS = History(
        CharacterReference(CHARACTER_ID_2),
        HistoryEntry(SettlementReference(SETTLEMENT_ID_0), DAY1),
    )

    @Nested
    inner class CheckOwnershipTest {

        @Test
        fun `Reference is an unknown business`() {
            val state = STATE.removeStorage(BUSINESS_ID_0)

            assertIllegalArgument("Requires unknown owner (Business 0)!") {
                checkOwnership(state, OWNED_BY_BUSINESS, DAY0)
            }
        }

        @Test
        fun `Reference is an unknown character`() {
            val state = STATE.removeStorage(CHARACTER_ID_0)

            assertIllegalArgument("Requires unknown owner (Character 2)!") {
                checkOwnership(state, OWNED_BY_CHARACTER, DAY0)
            }
        }

        @Test
        fun `Reference is an unknown organization`() {
            val state = STATE.removeStorage(ORGANIZATION_ID_0)

            assertIllegalArgument("Requires unknown owner (Organization 0)!") {
                checkOwnership(state, OWNED_BY_ORGANIZATION, DAY0)
            }
        }

        @Test
        fun `Reference is an unknown Realm`() {
            val state = STATE.removeStorage(REALM_ID_0)

            assertIllegalArgument("Requires unknown owner (Realm 0)!") {
                checkOwnership(state, OWNED_BY_REALM, DAY0)
            }
        }

        @Test
        fun `Reference is an unknown town`() {
            val state = STATE.removeStorage(SETTLEMENT_ID_0)

            assertIllegalArgument("Requires unknown owner (Town 0)!") {
                checkOwnership(state, OWNED_BY_TOWN, DAY0)
            }
        }

        @Test
        fun `Previous owner is an unknown character`() {
            val state = STATE.removeStorage(CHARACTER_ID_0)

            assertIllegalArgument("Requires unknown 1.previous owner (Character 2)!") {
                checkOwnership(state, CHARACTER_AS_PREVIOUS, DAY0)
            }
        }

        @Test
        fun `Previous owner is an unknown organization`() {
            val state = STATE.removeStorage(ORGANIZATION_ID_0)

            assertIllegalArgument("Requires unknown 1.previous owner (Organization 0)!") {
                checkOwnership(state, ORGANIZATION_AS_PREVIOUS, DAY0)
            }
        }

        @Test
        fun `Previous owner is an unknown town`() {
            val state = STATE.removeStorage(SETTLEMENT_ID_0)

            assertIllegalArgument("Requires unknown 1.previous owner (Town 0)!") {
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
                SettlementReference(SETTLEMENT_ID_0),
                listOf(
                    HistoryEntry(CharacterReference(CHARACTER_ID_2), DAY2),
                    HistoryEntry(SettlementReference(SETTLEMENT_ID_0), DAY1)
                )
            )

            assertIllegalArgument("2.previous owner's until is too early!") {
                checkOwnership(STATE, ownership, DAY0)
            }
        }

        @Test
        fun `Character owns a building before his birth`() {
            val age = AgeViaBirthdate(DAY1)
            val state = STATE.updateStorage(Character(CHARACTER_ID_2, age = age))

            assertIllegalArgument("The owner (Character 2) doesn't exist at the required date!") {
                checkOwnership(state, OWNED_BY_CHARACTER, DAY0)
            }
        }

        @Test
        fun `First owner didn't exist yet`() {
            val state = STATE.updateStorage(Settlement(SETTLEMENT_ID_0, date = DAY1))

            assertIllegalArgument("The 1.previous owner (Town 0) doesn't exist at the required date!") {
                checkOwnership(state, TOWN_AS_PREVIOUS, DAY0)
            }
        }

        @Test
        fun `Second owner didn't exist yet`() {
            val ownership = History(
                NoReference,
                listOf(
                    HistoryEntry(SettlementReference(SETTLEMENT_ID_0), DAY1),
                    HistoryEntry(CharacterReference(CHARACTER_ID_2), DAY2)
                )
            )
            val age = AgeViaBirthdate(DAY2)
            val state = STATE.updateStorage(Character(CHARACTER_ID_2, age = age))

            assertIllegalArgument("The 2.previous owner (Character 2) doesn't exist at the required date!") {
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
                    NoReference,
                    listOf(
                        HistoryEntry(SettlementReference(SETTLEMENT_ID_0), DAY1),
                        HistoryEntry(CharacterReference(CHARACTER_ID_2), DAY2)
                    )
                )
            )
        }

        private fun testSuccess(ownership: History<Reference>) {
            checkOwnership(STATE, ownership, DAY0)
        }
    }
}