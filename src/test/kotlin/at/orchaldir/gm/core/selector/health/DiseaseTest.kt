package at.orchaldir.gm.core.selector.health

import at.orchaldir.gm.CHARACTER_ID_0
import at.orchaldir.gm.DAY0
import at.orchaldir.gm.DISEASE_ID_0
import at.orchaldir.gm.DISEASE_ID_1
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.health.Disease
import at.orchaldir.gm.core.model.util.Dead
import at.orchaldir.gm.core.model.util.DeathByDisease
import at.orchaldir.gm.core.model.util.UndefinedReference
import at.orchaldir.gm.core.model.util.origin.EvolvedElement
import at.orchaldir.gm.core.model.util.origin.ModifiedElement
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DiseaseTest {

    @Nested
    inner class CanDeleteTest {
        private val disease = Disease(DISEASE_ID_0)
        private val state = State(
            listOf(
                Storage(disease),
            )
        )

        @Test
        fun `Cannot delete a modified disease`() {
            val disease1 = Disease(DISEASE_ID_1, origin = ModifiedElement(DISEASE_ID_0, UndefinedReference))
            val newState = state.updateStorage(Storage(listOf(disease, disease1)))

            failCanDelete(newState, DISEASE_ID_1)
        }

        @Test
        fun `Cannot delete an evolved disease`() {
            val disease1 = Disease(DISEASE_ID_1, origin = EvolvedElement(DISEASE_ID_0))
            val newState = state.updateStorage(Storage(listOf(disease, disease1)))

            failCanDelete(newState, DISEASE_ID_1)
        }

        @Test
        fun `Cannot delete a disease that killed a character`() {
            val dead = Dead(DAY0, DeathByDisease(DISEASE_ID_0))
            val character = Character(CHARACTER_ID_0, vitalStatus = dead)
            val newState = state.updateStorage(Storage(character))

            failCanDelete(newState, CHARACTER_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(
                DeleteResult(DISEASE_ID_0).addId(blockingId),
                state.canDeleteDisease(DISEASE_ID_0)
            )
        }
    }

}