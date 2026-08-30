package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.COLOR_SCHEME_GROUP_ID_0
import at.orchaldir.gm.COLOR_SCHEME_ID_0
import at.orchaldir.gm.UNKNOWN_COLOR_SCHEME_ID
import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.core.action.UpdateAction
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.render.ColorScheme
import at.orchaldir.gm.core.model.util.render.ColorSchemeGroup
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ColorSchemeGroupTest {

    private val STATE = State(
        listOf(
            Storage(ColorScheme(COLOR_SCHEME_ID_0)),
            Storage(ColorSchemeGroup(COLOR_SCHEME_GROUP_ID_0)),
        )
    )

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot use unknown scheme`() {
            val group = ColorSchemeGroup(COLOR_SCHEME_GROUP_ID_0, schemes = setOf(UNKNOWN_COLOR_SCHEME_ID))
            val action = UpdateAction(group)

            assertIllegalArgument("Requires unknown Color Scheme 99!") { REDUCER.invoke(STATE, action) }
        }


        @Test
        fun `Update is valid`() {
            val group = ColorSchemeGroup(COLOR_SCHEME_GROUP_ID_0, schemes = setOf(COLOR_SCHEME_ID_0))
            val action = UpdateAction(group)

            assertEquals(
                group,
                REDUCER.invoke(STATE, action).first.getColorSchemeGroupStorage().get(COLOR_SCHEME_GROUP_ID_0),
            )
        }
    }

}