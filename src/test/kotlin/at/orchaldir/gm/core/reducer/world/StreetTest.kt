package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.assertFailMessage
import at.orchaldir.gm.core.action.DeleteStreet
import at.orchaldir.gm.core.action.UpdateStreet
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.street.Street
import at.orchaldir.gm.core.model.world.street.StreetId
import at.orchaldir.gm.core.model.world.town.StreetTile
import at.orchaldir.gm.core.model.world.town.Town
import at.orchaldir.gm.core.model.world.town.TownId
import at.orchaldir.gm.core.model.world.town.TownTile
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.map.TileMap2d
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

private val ID0 = StreetId(0)


class StreetTest {

    @Nested
    inner class DeleteTest {

        @Test
        fun `Can delete an existing street`() {
            val state = State(Storage(Street(ID0)))
            val action = DeleteStreet(ID0)

            assertEquals(0, REDUCER.invoke(state, action).first.getStreetStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeleteStreet(ID0)

            assertFailMessage<IllegalArgumentException>("Requires unknown Street 0!") {
                REDUCER.invoke(
                    State(),
                    action
                )
            }
        }

        @Test
        fun `Cannot delete, if used by a town`() {
            val action = DeleteStreet(ID0)
            val state = State(
                listOf(
                    Storage(Street(ID0)),
                    Storage(Town(TownId(0), map = TileMap2d(TownTile(construction = StreetTile(ID0)))))
                )
            )

            assertFailMessage<IllegalArgumentException>("Street 0 is used") { REDUCER.invoke(state, action) }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateStreet(Street(ID0))

            assertFailMessage<IllegalArgumentException>("Requires unknown Street 0!") {
                REDUCER.invoke(
                    State(),
                    action
                )
            }
        }

        @Test
        fun `Update is valid`() {
            val state = State(Storage(Street(ID0)))
            val street = Street(ID0, "Test")
            val action = UpdateStreet(street)

            assertEquals(street, REDUCER.invoke(state, action).first.getStreetStorage().get(ID0))
        }
    }

}