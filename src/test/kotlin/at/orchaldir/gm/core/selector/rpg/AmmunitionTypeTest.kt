package at.orchaldir.gm.core.selector.rpg

import at.orchaldir.gm.AMMUNITION_ID_0
import at.orchaldir.gm.AMMUNITION_TYPE_ID_0
import at.orchaldir.gm.EQUIPMENT_TYPE_ID_0
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.ammunition.Ammunition
import at.orchaldir.gm.core.model.rpg.equipment.AmmunitionType
import at.orchaldir.gm.core.model.rpg.combat.RangedAttack
import at.orchaldir.gm.core.model.rpg.combat.SingleShot
import at.orchaldir.gm.core.model.rpg.equipment.EquipmentType
import at.orchaldir.gm.core.selector.rpg.equipment.canDeleteAmmunitionType
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AmmunitionTypeTest {

    @Nested
    inner class CanDeleteTest {
        private val type = AmmunitionType(AMMUNITION_TYPE_ID_0)
        private val state = State(
            listOf(
                Storage(type),
            )
        )

        @Test
        fun `Cannot delete an ammunition type used by an ammunition`() {
            val element = Ammunition(AMMUNITION_ID_0, type = AMMUNITION_TYPE_ID_0)
            val newState = state.updateStorage(element)

            failCanDelete(newState, AMMUNITION_ID_0)
        }

        @Test
        fun `Cannot delete an ammunition type used by a ranged weapon`() {
            val attack = RangedAttack(shots = SingleShot(AMMUNITION_TYPE_ID_0))
            val element = EquipmentType(EQUIPMENT_TYPE_ID_0, rangedAttacks = listOf(attack))
            val newState = state.updateStorage(element)

            failCanDelete(newState, EQUIPMENT_TYPE_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(
                DeleteResult(AMMUNITION_TYPE_ID_0).addId(blockingId),
                state.canDeleteAmmunitionType(AMMUNITION_TYPE_ID_0)
            )
        }
    }

}