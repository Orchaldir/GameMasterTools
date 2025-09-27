package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.CALENDAR0
import at.orchaldir.gm.COLOR_SCHEME_ID_0
import at.orchaldir.gm.REALM_ID_0
import at.orchaldir.gm.core.action.UpdateAction
import at.orchaldir.gm.core.model.State
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
            val action = UpdateAction(ColorScheme(COLOR_SCHEME_ID_0))

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }


        @Test
        fun `Update is valid`() {
            val scheme = ColorScheme(COLOR_SCHEME_ID_0, OneColor(Color.Red))
            val action = UpdateAction(scheme)

            assertEquals(scheme, REDUCER.invoke(STATE, action).first.getColorSchemeStorage().get(COLOR_SCHEME_ID_0))
        }
    }

}