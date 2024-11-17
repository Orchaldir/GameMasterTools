package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.calendar.Calendar
import at.orchaldir.gm.core.model.calendar.CalendarId
import at.orchaldir.gm.core.model.calendar.MonthDefinition
import at.orchaldir.gm.core.model.character.CHARACTER
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.economy.business.BUSINESS
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.economy.business.BusinessId
import at.orchaldir.gm.core.model.time.Day
import at.orchaldir.gm.core.model.util.CreatedByBusiness
import at.orchaldir.gm.core.model.util.CreatedByCharacter
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class BuilderTest {

    private val BUSINESS0 = BusinessId(2)
    private val CHARACTER0 = CharacterId(3)

    private val CALENDAR = Calendar(CalendarId(0), months = listOf(MonthDefinition("a")))
    private val DAY0 = Day(100)
    private val DAY1 = Day(200)
    private val DAY2 = Day(300)

    private val STATE = State(
        listOf(
            Storage(Business(BUSINESS0, startDate = DAY1)),
            Storage(CALENDAR),
            Storage(Character(CHARACTER0, birthDate = DAY1)),
        )
    )

    private val BUILD_BY_BUSINESS = CreatedByBusiness(BUSINESS0)
    private val BUILD_BY_CHARACTER = CreatedByCharacter(CHARACTER0)

    @Nested
    inner class BuildByBusinessTest {

        @Test
        fun `Builder is an unknown business`() {
            val state = STATE.removeStorage(BUSINESS)

            assertIllegalArgument("Cannot use an unknown business 2 as builder!") {
                checkCreator(state, BUILD_BY_BUSINESS, DAY0, "Builder")
            }
        }

        @Test
        fun `Builder doesn't exist yet`() {
            assertIllegalArgument("Builder (business 2) is not open!") {
                checkCreator(STATE, BUILD_BY_BUSINESS, DAY0, "Builder")
            }
        }

        @Test
        fun `Builder is valid`() {
            checkCreator(STATE, BUILD_BY_BUSINESS, DAY2, "Builder")
        }
    }

    @Nested
    inner class BuildByCharacterTest {

        @Test
        fun `Builder is an unknown character`() {
            val state = STATE.removeStorage(CHARACTER)

            assertIllegalArgument("Cannot use an unknown character 3 as builder!") {
                checkCreator(state, BUILD_BY_CHARACTER, DAY0, "Builder")
            }
        }

        @Test
        fun `Builder doesn't exist yet`() {
            assertIllegalArgument("Builder (character 3) is not alive!") {
                checkCreator(STATE, BUILD_BY_CHARACTER, DAY0, "Builder")
            }
        }

        @Test
        fun `Builder is valid`() {
            checkCreator(STATE, BUILD_BY_CHARACTER, DAY2, "Builder")
        }
    }
}