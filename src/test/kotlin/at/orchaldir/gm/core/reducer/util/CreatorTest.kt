package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteCharacter
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.item.text.OriginalText
import at.orchaldir.gm.core.model.item.text.Text
import at.orchaldir.gm.core.model.item.text.TranslatedText
import at.orchaldir.gm.core.model.language.InventedLanguage
import at.orchaldir.gm.core.model.language.Language
import at.orchaldir.gm.core.model.magic.InventedSpell
import at.orchaldir.gm.core.model.magic.MagicTradition
import at.orchaldir.gm.core.model.magic.Spell
import at.orchaldir.gm.core.model.organization.Organization
import at.orchaldir.gm.core.model.quote.Quote
import at.orchaldir.gm.core.model.realm.Realm
import at.orchaldir.gm.core.model.religion.God
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.model.world.town.Town
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class CreatorTest {

    private val STATE = State(
        listOf(
            Storage(Business(BUSINESS_ID_0, startDate = DAY1)),
            Storage(CALENDAR0),
            Storage(Character(CHARACTER_ID_0, birthDate = DAY1)),
            Storage(God(GOD_ID_0)),
            Storage(Organization(ORGANIZATION_ID_0, date = DAY1)),
            Storage(Town(TOWN_ID_0, foundingDate = DAY1)),
        )
    )

    private val createdByBusiness = CreatedByBusiness(BUSINESS_ID_0)
    private val createdByCharacter = CreatedByCharacter(CHARACTER_ID_0)
    private val createdByGod = CreatedByGod(GOD_ID_0)
    private val createdByOrganization = CreatedByOrganization(ORGANIZATION_ID_0)
    private val createdByTown = CreatedByTown(TOWN_ID_0)

    @Nested
    inner class CanDeleteCreatorTest {
        private val action = DeleteCharacter(CHARACTER_ID_0)

        @Test
        fun `Created a building`() {
            val building = Building(BUILDING_ID_0, builder = createdByCharacter)
            val newState = STATE.updateStorage(Storage(building))

            assertIllegalArgument("Cannot delete Character 0, because of created elements (Building)!") {
                REDUCER.invoke(newState, action)
            }
        }

        @Test
        fun `Created a business`() {
            val business = Business(BUSINESS_ID_0, founder = createdByCharacter)
            val newState = STATE.updateStorage(Storage(business))

            assertIllegalArgument("Cannot delete Character 0, because of created elements (Business)!") {
                REDUCER.invoke(newState, action)
            }
        }

        @Test
        fun `Created a language`() {
            val origin = InventedLanguage(createdByCharacter, DAY0)
            val newState = STATE.updateStorage(Storage(Language(LANGUAGE_ID_0, origin = origin)))

            assertIllegalArgument("Cannot delete Character 0, because of created elements (Language)!") {
                REDUCER.invoke(newState, action)
            }
        }

        @Test
        fun `Created a magic tradition`() {
            val tradition = MagicTradition(UNKNOWN_MAGIC_TRADITION_ID, founder = createdByCharacter)
            val newState = STATE.updateStorage(Storage(tradition))

            assertIllegalArgument("Cannot delete Character 0, because of created elements (Magic Tradition)!") {
                REDUCER.invoke(newState, action)
            }
        }

        @Test
        fun `Created a quote`() {
            val quote = Quote(QUOTE_ID_0, source = createdByCharacter)
            val newState = STATE.updateStorage(Storage(quote))

            assertIllegalArgument("Cannot delete Character 0, because of created elements (Quote)!") {
                REDUCER.invoke(newState, action)
            }
        }

        @Test
        fun `Created a realm`() {
            val realm = Realm(REALM_ID_0, founder = createdByCharacter)
            val newState = STATE.updateStorage(Storage(realm))

            assertIllegalArgument("Cannot delete Character 0, because of created elements (Realm)!") {
                REDUCER.invoke(newState, action)
            }
        }

        @Test
        fun `Created a spell`() {
            val spell = Spell(SPELL_ID_0, origin = InventedSpell(createdByCharacter))
            val newState = STATE.updateStorage(Storage(spell))

            assertIllegalArgument("Cannot delete Character 0, because of created elements (Spell)!") {
                REDUCER.invoke(newState, action)
            }
        }

        @Test
        fun `Created an original text`() {
            val origin = OriginalText(createdByCharacter)
            val newState = STATE.updateStorage(Storage(Text(TEXT_ID_0, origin = origin)))

            assertIllegalArgument("Cannot delete Character 0, because of created elements (Text)!") {
                REDUCER.invoke(newState, action)
            }
        }

        @Test
        fun `Created an translated text`() {
            val origin = TranslatedText(TEXT_ID_1, createdByCharacter)
            val newState = STATE.updateStorage(Storage(Text(TEXT_ID_0, origin = origin)))

            assertIllegalArgument("Cannot delete Character 0, because of created elements (Text)!") {
                REDUCER.invoke(newState, action)
            }
        }

        @Test
        fun `Created a town`() {
            val town = Town(TOWN_ID_0, founder = createdByCharacter)
            val newState = STATE.updateStorage(Storage(town))

            assertIllegalArgument("Cannot delete Character 0, because of created elements (Town)!") {
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

                assertIllegalArgument("Cannot use an unknown business 0 as Builder!") {
                    validateCreator(state, createdByBusiness, BUILDING_ID_0, DAY0, "Builder")
                }
            }

            @Test
            fun `A business cannot create itself`() {
                assertIllegalArgument("The business cannot create itself!") {
                    validateCreator(STATE, createdByBusiness, BUSINESS_ID_0, DAY0, "Builder")
                }
            }

            @Test
            fun `Creator doesn't exist yet`() {
                assertIllegalArgument("Builder (business 0) does not exist!") {
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

                assertIllegalArgument("Cannot use an unknown character 0 as Builder!") {
                    validateCreator(state, createdByCharacter, BUILDING_ID_0, DAY0, "Builder")
                }
            }

            @Test
            fun `Creator doesn't exist yet`() {
                assertIllegalArgument("Builder (character 0) does not exist!") {
                    validateCreator(STATE, createdByCharacter, BUILDING_ID_0, DAY0, "Builder")
                }
            }

            @Test
            fun `Creator is valid`() {
                validateCreator(STATE, createdByCharacter, BUILDING_ID_0, DAY2, "Builder")
            }
        }

        @Nested
        inner class CreatedByGodTest {

            @Test
            fun `Creator is an unknown god`() {
                val state = STATE.removeStorage(GOD_ID_0)

                assertIllegalArgument("Cannot use an unknown god 0 as Builder!") {
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

                assertIllegalArgument("Cannot use an unknown organization 0 as Builder!") {
                    validateCreator(state, createdByOrganization, BUILDING_ID_0, DAY0, "Builder")
                }
            }

            @Test
            fun `An organization cannot create itself`() {
                assertIllegalArgument("The organization cannot create itself!") {
                    validateCreator(STATE, createdByOrganization, ORGANIZATION_ID_0, DAY0, "Builder")
                }
            }

            @Test
            fun `Creator doesn't exist yet`() {
                assertIllegalArgument("Builder (organization 0) does not exist!") {
                    validateCreator(STATE, createdByOrganization, BUILDING_ID_0, DAY0, "Builder")
                }
            }

            @Test
            fun `Creator is valid`() {
                validateCreator(STATE, createdByOrganization, BUILDING_ID_0, DAY2, "Builder")
            }
        }

        @Nested
        inner class CreatedByTownTest {

            @Test
            fun `Creator is an unknown town`() {
                val state = STATE.removeStorage(TOWN_ID_0)

                assertIllegalArgument("Cannot use an unknown town 0 as Builder!") {
                    validateCreator(state, createdByTown, BUILDING_ID_0, DAY0, "Builder")
                }
            }

            @Test
            fun `A town cannot create itself`() {
                assertIllegalArgument("The town cannot create itself!") {
                    validateCreator(STATE, createdByTown, TOWN_ID_0, DAY0, "Builder")
                }
            }

            @Test
            fun `Creator doesn't exist yet`() {
                assertIllegalArgument("Builder (town 0) does not exist!") {
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