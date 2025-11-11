package at.orchaldir.gm.core.selector.rpg

import at.orchaldir.gm.SHIELD_TYPE_ID_0
import at.orchaldir.gm.EQUIPMENT_ID_0
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.BodyArmour
import at.orchaldir.gm.core.model.item.equipment.Equipment
import at.orchaldir.gm.core.model.item.equipment.Shield
import at.orchaldir.gm.core.model.item.equipment.style.ScaleArmour
import at.orchaldir.gm.core.model.rpg.combat.ShieldStats
import at.orchaldir.gm.core.model.rpg.combat.ShieldType
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ShieldTypeTest {

    @Nested
    inner class CanDeleteTest {
        private val type = ShieldType(SHIELD_TYPE_ID_0)
        private val state = State(
            listOf(
                Storage(type),
            )
        )

        @Test
        fun `Cannot delete a shield type used by an equipment`() {
            val data = Shield(stats = ShieldStats(SHIELD_TYPE_ID_0))
            val element = Equipment(EQUIPMENT_ID_0, data = data)
            val newState = state.updateStorage(Storage(element))

            failCanDelete(newState, EQUIPMENT_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(
                DeleteResult(SHIELD_TYPE_ID_0).addId(blockingId),
                state.canDeleteShieldType(SHIELD_TYPE_ID_0)
            )
        }
    }

}