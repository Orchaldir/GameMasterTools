package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.STREET_ID_0
import at.orchaldir.gm.STREET_TYPE_ID_0
import at.orchaldir.gm.TOWN_MAP_ID_0
import at.orchaldir.gm.assertFailMessage
import at.orchaldir.gm.core.action.DeleteStreet
import at.orchaldir.gm.core.action.UpdateStreet
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.world.street.Street
import at.orchaldir.gm.core.model.world.town.StreetTile
import at.orchaldir.gm.core.model.world.town.TownMap
import at.orchaldir.gm.core.model.world.town.TownTile
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.map.TileMap2d
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

private val STATE = State(Storage(Street(STREET_ID_0)))

class StreetTest {

    @Nested
    inner class DeleteTest {

        @Test
        fun `Can delete an existing street`() {
            val action = DeleteStreet(STREET_ID_0)

            assertEquals(0, REDUCER.invoke(STATE, action).first.getStreetStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeleteStreet(STREET_ID_0)

            assertFailMessage<IllegalArgumentException>("Requires unknown Street 0!") {
                REDUCER.invoke(
                    State(),
                    action
                )
            }
        }

        @Test
        fun `Cannot delete, if used by a town`() {
            val action = DeleteStreet(STREET_ID_0)
            val map = TileMap2d(TownTile(construction = StreetTile(STREET_TYPE_ID_0, STREET_ID_0)))
            val state = State(
                listOf(
                    Storage(Street(STREET_ID_0)),
                    Storage(TownMap(TOWN_MAP_ID_0, map = map))
                )
            )

            assertFailMessage<IllegalArgumentException>("Cannot delete Street 0, because it is used!") {
                REDUCER.invoke(
                    state,
                    action
                )
            }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateStreet(Street(STREET_ID_0))

            assertFailMessage<IllegalArgumentException>("Requires unknown Street 0!") {
                REDUCER.invoke(
                    State(),
                    action
                )
            }
        }

        @Test
        fun `Update is valid`() {
            val street = Street(STREET_ID_0, Name.init("Test"))
            val action = UpdateStreet(street)

            assertEquals(street, REDUCER.invoke(STATE, action).first.getStreetStorage().get(STREET_ID_0))
        }
    }

}