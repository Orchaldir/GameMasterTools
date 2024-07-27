package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.DeleteFashion
import at.orchaldir.gm.core.action.UpdateFashion
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.appearance.GenderMap
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.fashion.Fashion
import at.orchaldir.gm.core.model.fashion.FashionId
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private val ID0 = FashionId(0)
private val CULTURE0 = CultureId(0)

class FashionTest {

    @Nested
    inner class DeleteTest {

        @Test
        fun `Can delete an existing fashion`() {
            val state = State(fashion = Storage(listOf(Fashion(ID0))))
            val action = DeleteFashion(ID0)

            assertEquals(0, REDUCER.invoke(state, action).first.fashion.getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeleteFashion(ID0)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot delete a fashion used by a culture`() {
            val culture = Culture(CULTURE0, clothingStyles = GenderMap(ID0))
            val state = State(
                cultures = Storage(listOf(culture)),
                fashion = Storage(listOf(Fashion(ID0)))
            )
            val action = DeleteFashion(ID0)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Successfully update a fashion`() {
            val state = State(fashion = Storage(listOf(Fashion(ID0))))
            val fashion = Fashion(ID0, "Test")
            val action = UpdateFashion(fashion)

            assertEquals(fashion, REDUCER.invoke(state, action).first.fashion.get(ID0))
        }

        @Test
        fun `Cannot update unknown fashion`() {
            val action = UpdateFashion(Fashion(ID0))

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }
    }

}