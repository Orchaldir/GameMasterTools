package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.STREET_TYPE_ID_0
import at.orchaldir.gm.TOWN_ID_0
import at.orchaldir.gm.assertFailMessage
import at.orchaldir.gm.core.action.DeleteStreetType
import at.orchaldir.gm.core.action.UpdateStreetType
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.world.street.StreetType
import at.orchaldir.gm.core.model.world.town.StreetTile
import at.orchaldir.gm.core.model.world.town.Town
import at.orchaldir.gm.core.model.world.town.TownTile
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.map.TileMap2d
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class StreetTypeTest {

    @Nested
    inner class DeleteTest {

        @Test
        fun `Can delete an existing street`() {
            val state = State(Storage(StreetType(STREET_TYPE_ID_0)))
            val action = DeleteStreetType(STREET_TYPE_ID_0)

            assertEquals(0, REDUCER.invoke(state, action).first.getStreetTypeStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeleteStreetType(STREET_TYPE_ID_0)

            assertFailMessage<IllegalArgumentException>("Requires unknown Street Type 0!") {
                REDUCER.invoke(
                    State(),
                    action
                )
            }
        }

        @Test
        fun `Cannot delete, if used by a town`() {
            val action = DeleteStreetType(STREET_TYPE_ID_0)
            val state = State(
                listOf(
                    Storage(StreetType(STREET_TYPE_ID_0)),
                    Storage(Town(TOWN_ID_0, map = TileMap2d(TownTile(construction = StreetTile(STREET_TYPE_ID_0)))))
                )
            )

            assertFailMessage<IllegalArgumentException>("Street Type 0 is used") { REDUCER.invoke(state, action) }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateStreetType(StreetType(STREET_TYPE_ID_0))

            assertFailMessage<IllegalArgumentException>("Requires unknown Street Type 0!") {
                REDUCER.invoke(
                    State(),
                    action
                )
            }
        }

        @Test
        fun `Update is valid`() {
            val state = State(Storage(StreetType(STREET_TYPE_ID_0)))
            val street = StreetType(STREET_TYPE_ID_0, "Test", Color.Gold)
            val action = UpdateStreetType(street)

            assertEquals(street, REDUCER.invoke(state, action).first.getStreetTypeStorage().get(STREET_TYPE_ID_0))
        }
    }

}