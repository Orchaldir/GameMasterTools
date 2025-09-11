package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteCharacter
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.culture.CULTURE_TYPE
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.language.Language
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.health.Disease
import at.orchaldir.gm.core.model.item.text.Text
import at.orchaldir.gm.core.model.magic.MagicTradition
import at.orchaldir.gm.core.model.magic.Spell
import at.orchaldir.gm.core.model.organization.Organization
import at.orchaldir.gm.core.model.realm.Catastrophe
import at.orchaldir.gm.core.model.realm.CreatedCatastrophe
import at.orchaldir.gm.core.model.realm.Realm
import at.orchaldir.gm.core.model.realm.Town
import at.orchaldir.gm.core.model.religion.God
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.model.util.origin.CreatedElement
import at.orchaldir.gm.core.model.util.origin.TranslatedElement
import at.orchaldir.gm.core.model.util.quote.Quote
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.core.selector.item.canDeleteUniform
import at.orchaldir.gm.core.selector.util.isCreator
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CreatorTest {

    private val STATE = State(
        listOf(
            Storage(Business(BUSINESS_ID_0, startDate = DAY1)),
            Storage(CALENDAR0),
            Storage(Character(CHARACTER_ID_0, birthDate = DAY1)),
            Storage(Culture(CULTURE_ID_0)),
            Storage(God(GOD_ID_0)),
            Storage(Organization(ORGANIZATION_ID_0, date = DAY1)),
            Storage(Realm(REALM_ID_0, date = DAY1)),
            Storage(Town(TOWN_ID_0, foundingDate = DAY1)),
        )
    )

    private val createdByBusiness = BusinessReference(BUSINESS_ID_0)
    private val createdByCharacter = CharacterReference(CHARACTER_ID_0)
    private val createdByCulture = CultureReference(CULTURE_ID_0)
    private val createdByGod = GodReference(GOD_ID_0)
    private val createdByOrganization = OrganizationReference(ORGANIZATION_ID_0)
    private val createdByRealm = RealmReference(REALM_ID_0)
    private val createdByTown = TownReference(TOWN_ID_0)

    @Nested
    inner class CanDeleteCreatorTest {
        private val action = DeleteCharacter(CHARACTER_ID_0)

        @Test
        fun `Created a building`() {
            test(Building(BUILDING_ID_0, builder = createdByCharacter))
        }

        @Test
        fun `Created a business`() {
            test(Business(BUSINESS_ID_0, founder = createdByCharacter))
        }

        @Test
        fun `Created a catastrophe`() {
            val cause = CreatedCatastrophe(CharacterReference(CHARACTER_ID_0))

            test(Catastrophe(CATASTROPHE_ID_0, cause = cause))
        }

        @Test
        fun `Created a disease`() {
            val origin = CreatedElement(createdByCharacter)

            test(Disease(DISEASE_ID_0, origin = origin))
        }

        @Test
        fun `Created a language`() {
            val origin = CreatedElement(createdByCharacter)

            test(Language(LANGUAGE_ID_0, date = DAY0, origin = origin))
        }

        @Test
        fun `Created a magic tradition`() {
            test(MagicTradition(UNKNOWN_MAGIC_TRADITION_ID, founder = createdByCharacter))
        }

        @Test
        fun `Created a quote`() {
            test(Quote(QUOTE_ID_0, source = createdByCharacter))
        }

        @Test
        fun `Created a realm`() {
            test(Realm(REALM_ID_0, founder = createdByCharacter))
        }

        @Test
        fun `Created a spell`() {
            test(Spell(SPELL_ID_0, origin = CreatedElement(createdByCharacter)))
        }

        @Test
        fun `Created an original text`() {
            val origin = CreatedElement(createdByCharacter)

            test(Text(TEXT_ID_0, origin = origin))
        }

        @Test
        fun `Created an translated text`() {
            val origin = TranslatedElement(TEXT_ID_1, createdByCharacter)

            test(Text(TEXT_ID_0, origin = origin))
        }

        @Test
        fun `Created a town`() {
            test(Town(TOWN_ID_0, founder = createdByCharacter))
        }

        private fun <ID : Id<ID>, ELEMENT : Element<ID>> test(element: ELEMENT) {
            val newState = STATE.updateStorage(Storage(element))

            assertTrue(newState.isCreator(CHARACTER_ID_0))
        }
    }

    @Nested
    inner class ValidateCreatorTest {

        @Nested
        inner class CreatedByBusinessTest {

            @Test
            fun `Creator is an unknown business`() {
                val state = STATE.removeStorage(BUSINESS_ID_0)

                assertIllegalArgument("Requires unknown Builder (Business 0)!") {
                    validateCreator(state, createdByBusiness, BUILDING_ID_0, DAY0, "Builder")
                }
            }

            @Test
            fun `Creator cannot create itself`() {
                assertIllegalArgument("The Builder (Business 0) cannot create itself!") {
                    validateCreator(STATE, createdByBusiness, BUSINESS_ID_0, null, "Builder")
                }
            }

            @Test
            fun `Creator doesn't exist yet`() {
                assertIllegalArgument("The Builder (Business 0) doesn't exist at the required date!") {
                    validateCreator(STATE, createdByBusiness, BUILDING_ID_0, DAY0, "Builder")
                }
            }

            @Test
            fun `Creator is valid`() {
                validateCreator(STATE, createdByBusiness, BUILDING_ID_0, DAY2, "Builder")
            }
        }

        @Nested
        inner class CreatedByCharacterTest {

            @Test
            fun `Creator is an unknown character`() {
                val state = STATE.removeStorage(CHARACTER_ID_0)

                assertIllegalArgument("Requires unknown Builder (Character 0)!") {
                    validateCreator(state, createdByCharacter, BUILDING_ID_0, DAY0, "Builder")
                }
            }

            @Test
            fun `Creator doesn't exist yet`() {
                assertIllegalArgument("The Builder (Character 0) doesn't exist at the required date!") {
                    validateCreator(STATE, createdByCharacter, BUILDING_ID_0, DAY0, "Builder")
                }
            }

            @Test
            fun `Creator is valid`() {
                validateCreator(STATE, createdByCharacter, BUILDING_ID_0, DAY2, "Builder")
            }
        }

        @Nested
        inner class CreatedByCultureTest {

            @Test
            fun `Creator is an unknown culture`() {
                val state = STATE.removeStorage(CULTURE_TYPE)

                assertIllegalArgument("Requires unknown Builder (Culture 0)!") {
                    validateCreator(state, createdByCulture, BUILDING_ID_0, DAY0, "Builder")
                }
            }

            @Test
            fun `Creator is valid`() {
                validateCreator(STATE, createdByCulture, BUILDING_ID_0, DAY2, "Builder")
            }
        }

        @Nested
        inner class CreatedByGodTest {

            @Test
            fun `Creator is an unknown god`() {
                val state = STATE.removeStorage(GOD_ID_0)

                assertIllegalArgument("Requires unknown Builder (God 0)!") {
                    validateCreator(state, createdByGod, BUILDING_ID_0, DAY0, "Builder")
                }
            }

            @Test
            fun `Creator is valid`() {
                validateCreator(STATE, createdByGod, BUILDING_ID_0, DAY2, "Builder")
            }
        }

        @Nested
        inner class CreatedByOrganizationTest {

            @Test
            fun `Creator is an unknown organization`() {
                val state = STATE.removeStorage(ORGANIZATION_ID_0)

                assertIllegalArgument("Requires unknown Builder (Organization 0)!") {
                    validateCreator(state, createdByOrganization, BUILDING_ID_0, DAY0, "Builder")
                }
            }

            @Test
            fun `Creator cannot create itself`() {
                assertIllegalArgument("The Builder (Organization 0) cannot create itself!") {
                    validateCreator(STATE, createdByOrganization, ORGANIZATION_ID_0, null, "Builder")
                }
            }

            @Test
            fun `Creator doesn't exist yet`() {
                assertIllegalArgument("The Builder (Organization 0) doesn't exist at the required date!") {
                    validateCreator(STATE, createdByOrganization, BUILDING_ID_0, DAY0, "Builder")
                }
            }

            @Test
            fun `Creator is valid`() {
                validateCreator(STATE, createdByOrganization, BUILDING_ID_0, DAY2, "Builder")
            }
        }

        @Nested
        inner class CreatedByRealmTest {

            @Test
            fun `Creator is an unknown realm`() {
                val state = STATE.removeStorage(REALM_ID_0)

                assertIllegalArgument("Requires unknown Builder (Realm 0)!") {
                    validateCreator(state, createdByRealm, BUILDING_ID_0, DAY0, "Builder")
                }
            }

            @Test
            fun `Creator cannot create itself`() {
                assertIllegalArgument("The Builder (Realm 0) cannot create itself!") {
                    validateCreator(STATE, createdByRealm, REALM_ID_0, null, "Builder")
                }
            }

            @Test
            fun `Creator doesn't exist yet`() {
                assertIllegalArgument("The Builder (Realm 0) doesn't exist at the required date!") {
                    validateCreator(STATE, createdByRealm, BUILDING_ID_0, DAY0, "Builder")
                }
            }

            @Test
            fun `Creator is valid`() {
                validateCreator(STATE, createdByRealm, BUILDING_ID_0, DAY2, "Builder")
            }
        }

        @Nested
        inner class CreatedByTownTest {

            @Test
            fun `Creator is an unknown town`() {
                val state = STATE.removeStorage(TOWN_ID_0)

                assertIllegalArgument("Requires unknown Builder (Town 0)!") {
                    validateCreator(state, createdByTown, BUILDING_ID_0, DAY0, "Builder")
                }
            }

            @Test
            fun `Creator cannot create itself`() {
                assertIllegalArgument("The Builder (Town 0) cannot create itself!") {
                    validateCreator(STATE, createdByTown, TOWN_ID_0, null, "Builder")
                }
            }

            @Test
            fun `Creator doesn't exist yet`() {
                assertIllegalArgument("The Builder (Town 0) doesn't exist at the required date!") {
                    validateCreator(STATE, createdByTown, BUILDING_ID_0, DAY0, "Builder")
                }
            }

            @Test
            fun `Creator is valid`() {
                validateCreator(STATE, createdByTown, BUILDING_ID_0, DAY2, "Builder")
            }
        }
    }
}