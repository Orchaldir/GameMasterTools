package at.orchaldir.gm.core.reducer.gm

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.UpdateAction
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.gm.treasure.EquipmentParcel
import at.orchaldir.gm.core.model.gm.treasure.TreasureParcel
import at.orchaldir.gm.core.model.item.equipment.Equipment
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class TreasureParcelTest {

    private val stat = State(
        listOf(
            Storage(Equipment(EQUIPMENT_ID_0)),
            Storage(listOf(TreasureParcel(TREASURE_PARCEL_ID_0), TreasureParcel(TREASURE_PARCEL_ID_1))),
        )
    )

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot be based on a unknown equipment`() {
            val entry = EquipmentParcel(UNKNOWN_EQUIPMENT_ID)
            val parcel = TreasureParcel(TREASURE_PARCEL_ID_0, entry = entry)
            val action = UpdateAction(parcel)

            assertIllegalArgument("Requires unknown Equipment 99!") { REDUCER.invoke(stat, action) }
        }

        @Test
        fun `Valid update`() {
            val entry = EquipmentParcel(EQUIPMENT_ID_0)
            val parcel = TreasureParcel(TREASURE_PARCEL_ID_0, entry = entry)
            val action = UpdateAction(parcel)

            REDUCER.invoke(stat, action)
        }
    }

}