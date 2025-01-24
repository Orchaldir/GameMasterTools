package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.organization.Organization
import at.orchaldir.gm.core.model.util.CreatedByBusiness
import at.orchaldir.gm.core.model.util.CreatedByCharacter
import at.orchaldir.gm.core.model.util.CreatedByOrganization
import at.orchaldir.gm.core.model.util.CreatedByTown
import at.orchaldir.gm.core.model.world.town.Town
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private val STATE = State(
    listOf(
        Storage(Business(BUSINESS_ID_0, startDate = DAY1)),
        Storage(CALENDAR0),
        Storage(Character(CHARACTER_ID_0, birthDate = DAY1)),
        Storage(Organization(ORGANIZATION_ID_0, date = DAY1)),
        Storage(Town(TOWN_ID_0, foundingDate = DAY1)),
    )
)

private val BUILD_BY_BUSINESS = CreatedByBusiness(BUSINESS_ID_0)
private val BUILD_BY_CHARACTER = CreatedByCharacter(CHARACTER_ID_0)
private val BUILD_BY_ORGANIZATION = CreatedByOrganization(ORGANIZATION_ID_0)
private val BUILD_BY_TOWN = CreatedByTown(TOWN_ID_0)

class CreatorTest {

    @Nested
    inner class CreatedByBusinessTest {

        @Test
        fun `Creator is an unknown business`() {
            val state = STATE.removeStorage(BUSINESS_ID_0)

            assertIllegalArgument("Cannot use an unknown business 0 as Builder!") {
                checkCreator(state, BUILD_BY_BUSINESS, BUILDING_ID_0, DAY0, "Builder")
            }
        }

        @Test
        fun `A business cannot create itself`() {
            assertIllegalArgument("A business cannot create itself!") {
                checkCreator(STATE, BUILD_BY_BUSINESS, BUSINESS_ID_0, DAY0, "Builder")
            }
        }

        @Test
        fun `Creator doesn't exist yet`() {
            assertIllegalArgument("Builder (business 0) is not open!") {
                checkCreator(STATE, BUILD_BY_BUSINESS, BUILDING_ID_0, DAY0, "Builder")
            }
        }

        @Test
        fun `Creator is valid`() {
            checkCreator(STATE, BUILD_BY_BUSINESS, BUILDING_ID_0, DAY2, "Builder")
        }
    }

    @Nested
    inner class CreatedByCharacterTest {

        @Test
        fun `Creator is an unknown character`() {
            val state = STATE.removeStorage(CHARACTER_ID_0)

            assertIllegalArgument("Cannot use an unknown character 0 as Builder!") {
                checkCreator(state, BUILD_BY_CHARACTER, BUILDING_ID_0, DAY0, "Builder")
            }
        }

        @Test
        fun `Creator doesn't exist yet`() {
            assertIllegalArgument("Builder (character 0) is not alive!") {
                checkCreator(STATE, BUILD_BY_CHARACTER, BUILDING_ID_0, DAY0, "Builder")
            }
        }

        @Test
        fun `Creator is valid`() {
            checkCreator(STATE, BUILD_BY_CHARACTER, BUILDING_ID_0, DAY2, "Builder")
        }
    }

    @Nested
    inner class CreatedByOrganizationTest {

        @Test
        fun `Creator is an unknown organization`() {
            val state = STATE.removeStorage(ORGANIZATION_ID_0)

            assertIllegalArgument("Cannot use an unknown organization 0 as Builder!") {
                checkCreator(state, BUILD_BY_ORGANIZATION, BUILDING_ID_0, DAY0, "Builder")
            }
        }

        @Test
        fun `An organization cannot create itself`() {
            assertIllegalArgument("An organization cannot create itself!") {
                checkCreator(STATE, BUILD_BY_ORGANIZATION, ORGANIZATION_ID_0, DAY0, "Builder")
            }
        }

        @Test
        fun `Creator doesn't exist yet`() {
            assertIllegalArgument("Builder (organization 0) is not alive!") {
                checkCreator(STATE, BUILD_BY_ORGANIZATION, BUILDING_ID_0, DAY0, "Builder")
            }
        }

        @Test
        fun `Creator is valid`() {
            checkCreator(STATE, BUILD_BY_ORGANIZATION, BUILDING_ID_0, DAY2, "Builder")
        }
    }

    @Nested
    inner class CreatedByTownTest {

        @Test
        fun `Creator is an unknown town`() {
            val state = STATE.removeStorage(TOWN_ID_0)

            assertIllegalArgument("Cannot use an unknown town 0 as Builder!") {
                checkCreator(state, BUILD_BY_TOWN, BUILDING_ID_0, DAY0, "Builder")
            }
        }

        @Test
        fun `A town cannot create itself`() {
            assertIllegalArgument("A town cannot create itself!") {
                checkCreator(STATE, BUILD_BY_TOWN, TOWN_ID_0, DAY0, "Builder")
            }
        }

        @Test
        fun `Creator doesn't exist yet`() {
            assertIllegalArgument("Builder (town 0) is not alive!") {
                checkCreator(STATE, BUILD_BY_TOWN, BUILDING_ID_0, DAY0, "Builder")
            }
        }

        @Test
        fun `Creator is valid`() {
            checkCreator(STATE, BUILD_BY_TOWN, BUILDING_ID_0, DAY2, "Builder")
        }
    }
}