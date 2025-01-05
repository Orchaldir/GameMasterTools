package at.orchaldir.gm.core.reducer.economy

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteBusiness
import at.orchaldir.gm.core.action.UpdateBusiness
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.Employed
import at.orchaldir.gm.core.model.character.Unemployed
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.economy.job.JobId
import at.orchaldir.gm.core.model.item.text.Text
import at.orchaldir.gm.core.model.item.text.OriginalText
import at.orchaldir.gm.core.model.language.InventedLanguage
import at.orchaldir.gm.core.model.language.Language
import at.orchaldir.gm.core.model.name.NameWithReference
import at.orchaldir.gm.core.model.name.ReferencedFullName
import at.orchaldir.gm.core.model.name.SimpleName
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.model.world.building.SingleBusiness
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith


class BusinessTest {

    @Nested
    inner class DeleteTest {
        val action = DeleteBusiness(BUSINESS_ID_0)

        @Test
        fun `Can delete an existing business`() {
            val state = State(Storage(Business(BUSINESS_ID_0)))

            assertEquals(0, REDUCER.invoke(state, action).first.getBusinessStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            assertIllegalArgument("Requires unknown Business 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot delete an inventor`() {
            val origin = InventedLanguage(CreatedByBusiness(BUSINESS_ID_0), DAY0)
            val state = State(
                listOf(
                    Storage(listOf(Business(BUSINESS_ID_0))),
                    Storage(listOf(Language(LANGUAGE_ID_0, origin = origin)))
                )
            )

            assertIllegalArgument("Cannot delete business 0, because of invented languages!") {
                REDUCER.invoke(state, action)
            }
        }

        @Test
        fun `Cannot delete an author`() {
            val origin = OriginalText(CreatedByBusiness(BUSINESS_ID_0))
            val state = State(
                listOf(
                    Storage(listOf(Business(BUSINESS_ID_0))),
                    Storage(listOf(Text(BOOK_ID_0, origin = origin)))
                )
            )

            assertIllegalArgument("Cannot delete business 0, who is an author!") {
                REDUCER.invoke(state, action)
            }
        }

        @Test
        fun `Cannot delete a business used by a building`() {
            val state = createState(Building(BUILDING_ID_0, purpose = SingleBusiness(BUSINESS_ID_0)))

            assertIllegalArgument("Cannot delete business 0, because it has a building!") {
                REDUCER.invoke(
                    state,
                    action
                )
            }
        }

        @Test
        fun `Cannot delete a business that build a building`() {
            val state = createState(Building(BUILDING_ID_0, builder = CreatedByBusiness(BUSINESS_ID_0)))

            assertIllegalArgument("Cannot delete business 0, because of built buildings!") {
                REDUCER.invoke(
                    state,
                    action
                )
            }
        }

        @Test
        fun `Cannot delete a business where a character is employed`() {
            val state =
                createState(Character(CHARACTER_ID_0, employmentStatus = History(Employed(BUSINESS_ID_0, JobId(0)))))
            val action = DeleteBusiness(BUSINESS_ID_0)

            assertIllegalArgument("Cannot delete business 0, because it has employees!") {
                REDUCER.invoke(
                    state,
                    action
                )
            }
        }

        @Test
        fun `Cannot delete a business where a character was previously employed`() {
            val employmentStatus = History(Unemployed, listOf(HistoryEntry(Employed(BUSINESS_ID_0, JobId(0)), DAY0)))
            val state = createState(Character(CHARACTER_ID_0, employmentStatus = employmentStatus))
            val action = DeleteBusiness(BUSINESS_ID_0)

            assertIllegalArgument("Cannot delete business 0, because it has previous employees!") {
                REDUCER.invoke(
                    state,
                    action
                )
            }
        }

        private fun <ID : Id<ID>, ELEMENT : Element<ID>> createState(element: ELEMENT) = State(
            listOf(
                Storage(listOf(Business(BUSINESS_ID_0))),
                Storage(listOf(element))
            )
        )
    }

    @Nested
    inner class UpdateTest {

        private val STATE = State(
            listOf(
                Storage(Business(BUSINESS_ID_0)),
                Storage(CALENDAR0),
                Storage(Character(CHARACTER_ID_0)),
            )
        )

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateBusiness(Business(BUSINESS_ID_0))
            val state = STATE.removeStorage(BUSINESS_ID_0)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Named after unknown character`() {
            val name = NameWithReference(ReferencedFullName(CHARACTER_ID_0), "A", "B")
            val action = UpdateBusiness(Business(BUSINESS_ID_0, name))
            val state = STATE.removeStorage(CHARACTER_ID_0)

            assertIllegalArgument("Reference for complex name is unknown!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Owner is an unknown character`() {
            val action = UpdateBusiness(Business(BUSINESS_ID_0, ownership = History(OwnedByCharacter(CHARACTER_ID_0))))
            val state = STATE.removeStorage(CHARACTER_ID_0)

            assertIllegalArgument("Cannot use an unknown character 0 as owner!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Founder is an unknown character`() {
            val action = UpdateBusiness(Business(BUSINESS_ID_0, founder = CreatedByCharacter(CHARACTER_ID_0)))
            val state = STATE.removeStorage(CHARACTER_ID_0)

            assertIllegalArgument("Cannot use an unknown character 0 as Founder!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Test Success`() {
            val business = Business(BUSINESS_ID_0, SimpleName("Test"))
            val action = UpdateBusiness(business)

            assertEquals(business, REDUCER.invoke(STATE, action).first.getBusinessStorage().get(BUSINESS_ID_0))
        }
    }

}