package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.NAME
import at.orchaldir.gm.STREET_TYPE_ID_0
import at.orchaldir.gm.TOWN_MAP_ID_0
import at.orchaldir.gm.assertFailMessage
import at.orchaldir.gm.core.action.DeleteStreetTemplate
import at.orchaldir.gm.core.action.UpdateStreetTemplate
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.world.street.StreetTemplate
import at.orchaldir.gm.core.model.world.town.StreetTile
import at.orchaldir.gm.core.model.world.town.TownMap
import at.orchaldir.gm.core.model.world.town.TownTile
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.map.TileMap2d
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class StreetTemplateTest {

    @Nested
    inner class DeleteTest {

        @Test
        fun `Can delete an existing street`() {
            val state = State(Storage(StreetTemplate(STREET_TYPE_ID_0)))
            val action = DeleteStreetTemplate(STREET_TYPE_ID_0)

            assertEquals(0, REDUCER.invoke(state, action).first.getStreetTemplateStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeleteStreetTemplate(STREET_TYPE_ID_0)

            assertFailMessage<IllegalArgumentException>("Requires unknown Street Template 0!") {
                REDUCER.invoke(
                    State(),
                    action
                )
            }
        }

        @Test
        fun `Cannot delete, if used by a town`() {
            val action = DeleteStreetTemplate(STREET_TYPE_ID_0)
            val state = State(
                listOf(
                    Storage(StreetTemplate(STREET_TYPE_ID_0)),
                    Storage(
                        TownMap(
                            TOWN_MAP_ID_0,
                            map = TileMap2d(TownTile(construction = StreetTile(STREET_TYPE_ID_0)))
                        )
                    )
                )
            )

            assertFailMessage<IllegalArgumentException>("Cannot delete Street Template 0, because it is used!") {
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
            val action = UpdateStreetTemplate(StreetTemplate(STREET_TYPE_ID_0))

            assertFailMessage<IllegalArgumentException>("Requires unknown Street Template 0!") {
                REDUCER.invoke(
                    State(),
                    action
                )
            }
        }

        @Test
        fun `Update is valid`() {
            val state = State(Storage(StreetTemplate(STREET_TYPE_ID_0)))
            val street = StreetTemplate(STREET_TYPE_ID_0, NAME, Color.Gold)
            val action = UpdateStreetTemplate(street)

            assertEquals(street, REDUCER.invoke(state, action).first.getStreetTemplateStorage().get(STREET_TYPE_ID_0))
        }
    }

}