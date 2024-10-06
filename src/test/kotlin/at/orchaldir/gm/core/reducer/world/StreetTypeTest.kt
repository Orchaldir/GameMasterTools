package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.assertFailMessage
import at.orchaldir.gm.core.action.DeleteStreetType
import at.orchaldir.gm.core.action.UpdateStreetType
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.world.street.Street
import at.orchaldir.gm.core.model.world.street.StreetId
import at.orchaldir.gm.core.model.world.street.StreetType
import at.orchaldir.gm.core.model.world.street.StreetTypeId
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private val ID0 = StreetTypeId(0)

class StreetTypeTest {

    @Nested
    inner class DeleteTest {

        @Test
        fun `Can delete an existing street`() {
            val state = State(Storage(StreetType(ID0)))
            val action = DeleteStreetType(ID0)

            assertEquals(0, REDUCER.invoke(state, action).first.getStreetTypeStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeleteStreetType(ID0)

            assertFailMessage<IllegalArgumentException>("Requires unknown Street Type 0!") {
                REDUCER.invoke(
                    State(),
                    action
                )
            }
        }

        @Test
        fun `Cannot delete, if used by a street`() {
            val action = DeleteStreetType(ID0)
            val state = State(
                listOf(
                    Storage(StreetType(ID0)),
                    Storage(Street(StreetId(9), type = ID0)),
                )
            )

            assertFailMessage<IllegalArgumentException>("Street Type 0 is used") { REDUCER.invoke(state, action) }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateStreetType(StreetType(ID0))

            assertFailMessage<IllegalArgumentException>("Requires unknown Street Type 0!") {
                REDUCER.invoke(
                    State(),
                    action
                )
            }
        }

        @Test
        fun `Update is valid`() {
            val state = State(Storage(StreetType(ID0)))
            val street = StreetType(ID0, "Test", Color.Gold)
            val action = UpdateStreetType(street)

            assertEquals(street, REDUCER.invoke(state, action).first.getStreetTypeStorage().get(ID0))
        }
    }

}