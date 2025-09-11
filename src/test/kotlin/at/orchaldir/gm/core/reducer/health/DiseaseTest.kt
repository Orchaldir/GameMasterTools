package at.orchaldir.gm.core.reducer.health

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteDisease
import at.orchaldir.gm.core.action.UpdateDisease
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.health.Disease
import at.orchaldir.gm.core.model.util.CharacterReference
import at.orchaldir.gm.core.model.util.Dead
import at.orchaldir.gm.core.model.util.DeathByDisease
import at.orchaldir.gm.core.model.util.UndefinedReference
import at.orchaldir.gm.core.model.util.origin.CreatedElement
import at.orchaldir.gm.core.model.util.origin.EvolvedElement
import at.orchaldir.gm.core.model.util.origin.ModifiedElement
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

private val disease0 = Disease(DISEASE_ID_0)
private val STATE = State(
    listOf(
        Storage(CALENDAR0),
        Storage(disease0),
    )
)

class DiseaseTest {

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateDisease(Disease(DISEASE_ID_0))
            val state = STATE.removeStorage(DISEASE_ID_0)

            assertIllegalArgument("Requires unknown Disease 0!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot modify an unknown disease`() {
            val disease = Disease(DISEASE_ID_0, origin = ModifiedElement(UNKNOWN_DISEASE_ID))
            val action = UpdateDisease(disease)

            assertIllegalArgument("Requires unknown parent Disease 99!") {
                REDUCER.invoke(STATE, action)
            }
        }

        @Test
        fun `Cannot evolve from an unknown disease`() {
            val disease = Disease(DISEASE_ID_0, origin = EvolvedElement(UNKNOWN_DISEASE_ID))
            val action = UpdateDisease(disease)

            assertIllegalArgument("Requires unknown parent Disease 99!") {
                REDUCER.invoke(STATE, action)
            }
        }

        @Test
        fun `Modifier must exist`() {
            val origin = CreatedElement(CharacterReference(UNKNOWN_CHARACTER_ID))
            val disease = Disease(DISEASE_ID_0, origin = origin)
            val action = UpdateDisease(disease)

            assertIllegalArgument("Requires unknown Creator (Character 99)!") {
                REDUCER.invoke(STATE, action)
            }
        }

        @Test
        fun `Date is in the future`() {
            val action = UpdateDisease(Disease(DISEASE_ID_0, date = FUTURE_DAY_0))

            assertIllegalArgument("Date (Disease) is in the future!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Update a disease`() {
            val disease = Disease(DISEASE_ID_0, NAME)
            val action = UpdateDisease(disease)

            assertEquals(disease, REDUCER.invoke(STATE, action).first.getDiseaseStorage().get(DISEASE_ID_0))
        }
    }

}