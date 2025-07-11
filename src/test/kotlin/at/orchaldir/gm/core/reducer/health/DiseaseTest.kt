package at.orchaldir.gm.core.reducer.health

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteDisease
import at.orchaldir.gm.core.action.UpdateDisease
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.health.CreatedDisease
import at.orchaldir.gm.core.model.health.Disease
import at.orchaldir.gm.core.model.health.EvolvedDisease
import at.orchaldir.gm.core.model.health.ModifiedDisease
import at.orchaldir.gm.core.model.util.CreatedByCharacter
import at.orchaldir.gm.core.model.util.Dead
import at.orchaldir.gm.core.model.util.DeathByDisease
import at.orchaldir.gm.core.model.util.UndefinedCreator
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
    inner class DeleteTest {
        val action = DeleteDisease(DISEASE_ID_0)

        @Test
        fun `Can delete an existing disease`() {
            assertEquals(0, REDUCER.invoke(STATE, action).first.getDiseaseStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            assertIllegalArgument("Requires unknown Disease 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot delete a modified disease`() {
            val disease1 = Disease(DISEASE_ID_1, origin = ModifiedDisease(DISEASE_ID_0, UndefinedCreator))
            val state = STATE.updateStorage(Storage(listOf(disease0, disease1)))

            assertIllegalArgument("Cannot delete Disease 0, because it is used!") {
                REDUCER.invoke(state, action)
            }
        }

        @Test
        fun `Cannot delete an evolved disease`() {
            val disease1 = Disease(DISEASE_ID_1, origin = EvolvedDisease(DISEASE_ID_0))
            val state = STATE.updateStorage(Storage(listOf(disease0, disease1)))

            assertIllegalArgument("Cannot delete Disease 0, because it is used!") {
                REDUCER.invoke(state, action)
            }
        }

        @Test
        fun `Cannot delete a disease that killed a character`() {
            val dead = Dead(DAY0, DeathByDisease(DISEASE_ID_0))
            val character = Character(CHARACTER_ID_0, vitalStatus = dead)
            val newState = STATE.updateStorage(Storage(character))

            assertIllegalArgument("Cannot delete Disease 0, because it is used!") {
                REDUCER.invoke(newState, action)
            }
        }
    }

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
            val disease = Disease(DISEASE_ID_0, origin = ModifiedDisease(DISEASE_ID_1, UndefinedCreator))
            val action = UpdateDisease(disease)

            assertIllegalArgument("Parent disease 1 is unknown!") {
                REDUCER.invoke(STATE, action)
            }
        }

        @Test
        fun `Cannot evolve from an unknown disease`() {
            val disease = Disease(DISEASE_ID_0, origin = EvolvedDisease(DISEASE_ID_1))
            val action = UpdateDisease(disease)

            assertIllegalArgument("Parent disease 1 is unknown!") {
                REDUCER.invoke(STATE, action)
            }
        }

        @Test
        fun `Modifier must exist`() {
            val disease = Disease(DISEASE_ID_0, origin = CreatedDisease(CreatedByCharacter(CHARACTER_ID_0)))
            val action = UpdateDisease(disease)

            assertIllegalArgument("Cannot use an unknown Character 0 as Inventor!") {
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