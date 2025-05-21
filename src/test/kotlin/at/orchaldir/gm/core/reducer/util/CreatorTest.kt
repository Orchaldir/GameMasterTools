package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteCharacter
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.culture.CULTURE_TYPE
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.language.InventedLanguage
import at.orchaldir.gm.core.model.culture.language.Language
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.item.text.OriginalText
import at.orchaldir.gm.core.model.item.text.Text
import at.orchaldir.gm.core.model.item.text.TranslatedText
import at.orchaldir.gm.core.model.magic.MagicTradition
import at.orchaldir.gm.core.model.magic.Spell
import at.orchaldir.gm.core.model.organization.Organization
import at.orchaldir.gm.core.model.realm.Catastrophe
import at.orchaldir.gm.core.model.realm.CreatedCatastrophe
import at.orchaldir.gm.core.model.realm.Realm
import at.orchaldir.gm.core.model.realm.Town
import at.orchaldir.gm.core.model.religion.God
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.model.util.quote.Quote
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.core.selector.util.isCreator
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

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

    private val createdByBusiness = CreatedByBusiness(BUSINESS_ID_0)
    private val createdByCharacter = CreatedByCharacter(CHARACTER_ID_0)
    private val createdByCulture = CreatedByCulture(CULTURE_ID_0)
    private val createdByGod = CreatedByGod(GOD_ID_0)
    private val createdByOrganization = CreatedByOrganization(ORGANIZATION_ID_0)
    private val createdByRealm = CreatedByRealm(REALM_ID_0)
    private val createdByTown = CreatedByTown(TOWN_ID_0)

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
            val cause = CreatedCatastrophe(CreatedByCharacter(CHARACTER_ID_0))

            test(Catastrophe(CATASTROPHE_ID_0, cause = cause))
        }

        @Test
        fun `Created a language`() {
            val origin = InventedLanguage(createdByCharacter, DAY0)

            test(Language(LANGUAGE_ID_0, origin = origin))
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
            test(Spell(SPELL_ID_0, origin = CreatedOrigin(createdByCharacter)))
        }

        @Test
        fun `Created an original text`() {
            val origin = OriginalText(createdByCharacter)

            test(Text(TEXT_ID_0, origin = origin))
        }

        @Test
        fun `Created an translated text`() {
            val origin = TranslatedText(TEXT_ID_1, createdByCharacter)

            test(Text(TEXT_ID_0, origin = origin))
        }

        @Test
        fun `Created a town`() {
            test(Town(TOWN_ID_0, founder = createdByCharacter))
        }

        private fun <ID : Id<ID>, ELEMENT : Element<ID>> test(element: ELEMENT) {
            val newState = STATE.updateStorage(Storage(element))

            assertTrue(newState.isCreator(CHARACTER_ID_0))

            assertIllegalArgument("Cannot delete Character 0, because of created elements (${element.id().type()})!") {
                REDUCER.invoke(newState, action)
            }
        }
    }

    @Nested
    inner class ValidateCreatorTest {

        @Nested
        inner class CreatedByBusinessTest {

            @Test
            fun `Creator is an unknown business`() {
                val state = STATE.removeStorage(BUSINESS_ID_0)

                assertIllegalArgument("Cannot use an unknown Business 0 as Builder!") {
                    validateCreator(state, createdByBusiness, BUILDING_ID_0, DAY0, "Builder")
                }
            }

            @Test
            fun `Creator cannot create itself`() {
                assertIllegalArgument("The Business cannot create itself!") {
                    validateCreator(STATE, createdByBusiness, BUSINESS_ID_0, DAY0, "Builder")
                }
            }

            @Test
            fun `Creator doesn't exist yet`() {
                assertIllegalArgument("Builder (Business 0) does not exist!") {
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

                assertIllegalArgument("Cannot use an unknown Character 0 as Builder!") {
                    validateCreator(state, createdByCharacter, BUILDING_ID_0, DAY0, "Builder")
                }
            }

            @Test
            fun `Creator doesn't exist yet`() {
                assertIllegalArgument("Builder (Character 0) does not exist!") {
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

                assertIllegalArgument("Cannot use an unknown Culture 0 as Builder!") {
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

                assertIllegalArgument("Cannot use an unknown God 0 as Builder!") {
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

                assertIllegalArgument("Cannot use an unknown Organization 0 as Builder!") {
                    validateCreator(state, createdByOrganization, BUILDING_ID_0, DAY0, "Builder")
                }
            }

            @Test
            fun `Creator cannot create itself`() {
                assertIllegalArgument("The Organization cannot create itself!") {
                    validateCreator(STATE, createdByOrganization, ORGANIZATION_ID_0, DAY0, "Builder")
                }
            }

            @Test
            fun `Creator doesn't exist yet`() {
                assertIllegalArgument("Builder (Organization 0) does not exist!") {
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

                assertIllegalArgument("Cannot use an unknown Realm 0 as Builder!") {
                    validateCreator(state, createdByRealm, BUILDING_ID_0, DAY0, "Builder")
                }
            }

            @Test
            fun `Creator cannot create itself`() {
                assertIllegalArgument("The Realm cannot create itself!") {
                    validateCreator(STATE, createdByRealm, REALM_ID_0, DAY0, "Builder")
                }
            }

            @Test
            fun `Creator doesn't exist yet`() {
                assertIllegalArgument("Builder (Realm 0) does not exist!") {
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

                assertIllegalArgument("Cannot use an unknown Town 0 as Builder!") {
                    validateCreator(state, createdByTown, BUILDING_ID_0, DAY0, "Builder")
                }
            }

            @Test
            fun `Creator cannot create itself`() {
                assertIllegalArgument("The Town cannot create itself!") {
                    validateCreator(STATE, createdByTown, TOWN_ID_0, DAY0, "Builder")
                }
            }

            @Test
            fun `Creator doesn't exist yet`() {
                assertIllegalArgument("Builder (Town 0) does not exist!") {
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