package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CHARACTER
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.economy.business.BUSINESS
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.util.CreatedByBusiness
import at.orchaldir.gm.core.model.util.CreatedByCharacter
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private val STATE = State(
    listOf(
        Storage(Business(BUSINESS_ID_0, startDate = DAY1)),
        Storage(CALENDAR0),
        Storage(Character(CHARACTER_ID_0, birthDate = DAY1)),
    )
)

private val BUILD_BY_BUSINESS = CreatedByBusiness(BUSINESS_ID_0)
private val BUILD_BY_CHARACTER = CreatedByCharacter(CHARACTER_ID_0)

class CreatorTest {

    @Nested
    inner class CreatedByBusinessTest {

        @Test
        fun `Creator is an unknown business`() {
            val state = STATE.removeStorage(BUSINESS)

            assertIllegalArgument("Cannot use an unknown business 0 as Builder!") {
                checkCreator(state, BUILD_BY_BUSINESS, DAY0, "Builder")
            }
        }

        @Test
        fun `Creator doesn't exist yet`() {
            assertIllegalArgument("Builder (business 0) is not open!") {
                checkCreator(STATE, BUILD_BY_BUSINESS, DAY0, "Builder")
            }
        }

        @Test
        fun `Creator is valid`() {
            checkCreator(STATE, BUILD_BY_BUSINESS, DAY2, "Builder")
        }
    }

    @Nested
    inner class CreatedByCharacterTest {

        @Test
        fun `Creator is an unknown character`() {
            val state = STATE.removeStorage(CHARACTER)

            assertIllegalArgument("Cannot use an unknown character 0 as Builder!") {
                checkCreator(state, BUILD_BY_CHARACTER, DAY0, "Builder")
            }
        }

        @Test
        fun `Creator doesn't exist yet`() {
            assertIllegalArgument("Builder (character 0) is not alive!") {
                checkCreator(STATE, BUILD_BY_CHARACTER, DAY0, "Builder")
            }
        }

        @Test
        fun `Creator is valid`() {
            checkCreator(STATE, BUILD_BY_CHARACTER, DAY2, "Builder")
        }
    }
}