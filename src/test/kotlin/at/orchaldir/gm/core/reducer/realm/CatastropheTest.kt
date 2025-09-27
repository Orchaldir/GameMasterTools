package at.orchaldir.gm.core.reducer.realm

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.UpdateAction
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.AccidentalCatastrophe
import at.orchaldir.gm.core.model.realm.Catastrophe
import at.orchaldir.gm.core.model.realm.CreatedCatastrophe
import at.orchaldir.gm.core.model.util.CharacterReference
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CatastropheTest {

    private val STATE = State(
        listOf(
            Storage(CALENDAR0),
            Storage(Catastrophe(CATASTROPHE_ID_0)),
        )
    )
    private val creator = CharacterReference(UNKNOWN_CHARACTER_ID)

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateAction(Catastrophe(UNKNOWN_CATASTROPHE_ID))

            assertIllegalArgument("Requires unknown Catastrophe 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Cannot use unknown creator for an accidental catastrophe`() {
            val action = UpdateAction(Catastrophe(CATASTROPHE_ID_0, cause = AccidentalCatastrophe(creator)))

            assertIllegalArgument("Requires unknown creator (Character 99)!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Cannot use unknown creator for a created catastrophe`() {
            val action = UpdateAction(Catastrophe(CATASTROPHE_ID_0, cause = CreatedCatastrophe(creator)))

            assertIllegalArgument("Requires unknown creator (Character 99)!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Update a catastrophe`() {
            val catastrophe = Catastrophe(CATASTROPHE_ID_0, NAME)
            val action = UpdateAction(catastrophe)

            assertEquals(catastrophe, REDUCER.invoke(STATE, action).first.getCatastropheStorage().get(CATASTROPHE_ID_0))
        }
    }

}