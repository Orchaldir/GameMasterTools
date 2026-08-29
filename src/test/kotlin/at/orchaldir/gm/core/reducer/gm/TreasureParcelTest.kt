package at.orchaldir.gm.core.reducer.gm

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.UpdateAction
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.gm.treasure.AmmunitionParcel
import at.orchaldir.gm.core.model.gm.treasure.EquipmentParcel
import at.orchaldir.gm.core.model.gm.treasure.MoneyParcel
import at.orchaldir.gm.core.model.gm.treasure.TextParcel
import at.orchaldir.gm.core.model.gm.treasure.TreasureEntry
import at.orchaldir.gm.core.model.gm.treasure.TreasureParcel
import at.orchaldir.gm.core.model.gm.treasure.TreasureParcelLookup
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
        fun `Cannot contain an unknown ammunition`() {
            failUpdate(AmmunitionParcel(UNKNOWN_AMMUNITION), "Requires unknown Ammunition 99!")
        }

        @Test
        fun `Cannot contain an unknown currency unit`() {
            failUpdate(MoneyParcel(UNKNOWN_CURRENCY_UNIT_ID), "Requires unknown Currency Unit 99!")
        }

        @Test
        fun `Cannot contain an unknown equipment`() {
            failUpdate(EquipmentParcel(UNKNOWN_EQUIPMENT_ID), "Requires unknown Equipment 99!")
        }

        @Test
        fun `Cannot contain an unknown text`() {
            failUpdate(TextParcel(UNKNOWN_TEXT_ID), "Requires unknown Text 99!")
        }

        @Test
        fun `Cannot contain an unknown treasure parcel`() {
            failUpdate(TreasureParcelLookup(UNKNOWN_TREASURE_PARCEL_ID), "Requires unknown Treasure Parcel 99!")
        }

        @Test
        fun `Valid update`() {
            val entry = EquipmentParcel(EQUIPMENT_ID_0)
            val parcel = TreasureParcel(TREASURE_PARCEL_ID_0, entry = entry)
            val action = UpdateAction(parcel)

            REDUCER.invoke(stat, action)
        }

        private fun failUpdate(entry: TreasureEntry, message: String) {
            val parcel = TreasureParcel(TREASURE_PARCEL_ID_0, entry = entry)
            val action = UpdateAction(parcel)

            assertIllegalArgument(message) { REDUCER.invoke(stat, action) }
        }
    }

}