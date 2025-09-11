package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteColorScheme
import at.orchaldir.gm.core.action.UpdateColorScheme
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.item.equipment.BodySlot
import at.orchaldir.gm.core.model.item.equipment.Equipment
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap
import at.orchaldir.gm.core.model.realm.Realm
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.util.render.ColorScheme
import at.orchaldir.gm.core.model.util.render.OneColor
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ColorSchemeTest {

    private val STATE = State(
        listOf(
            Storage(CALENDAR0),
            Storage(Realm(REALM_ID_0)),
            Storage(ColorScheme(COLOR_SCHEME_ID_0)),
        )
    )

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateColorScheme(ColorScheme(COLOR_SCHEME_ID_0))

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }


        @Test
        fun `Update is valid`() {
            val scheme = ColorScheme(COLOR_SCHEME_ID_0, OneColor(Color.Red))
            val action = UpdateColorScheme(scheme)

            assertEquals(scheme, REDUCER.invoke(STATE, action).first.getColorSchemeStorage().get(COLOR_SCHEME_ID_0))
        }
    }

}