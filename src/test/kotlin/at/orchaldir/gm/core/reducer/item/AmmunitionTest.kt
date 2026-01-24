package at.orchaldir.gm.core.reducer.item

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.ammunition.Ammunition
import at.orchaldir.gm.core.model.rpg.combat.AmmunitionType
import at.orchaldir.gm.core.model.rpg.combat.EquipmentModifier
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Test

class AmmunitionTest {

    private val STATE = State(
        listOf(
            Storage(AmmunitionType(AMMUNITION_TYPE_ID_0)),
            Storage(EquipmentModifier(EQUIPMENT_MODIFIER_ID_0)),
        )
    )

    @Test
    fun `Unknown ammunition type`() {
        val ammunition = Ammunition(AMMUNITION_ID_0, type = UNKNOWN_AMMUNITION_TYPE)

        assertIllegalArgument("Requires unknown Ammunition Type 99!") { ammunition.validate(STATE) }
    }

    @Test
    fun `Unknown modifier type`() {
        val ammunition = Ammunition(AMMUNITION_ID_0, modifiers = setOf(UNKNOWN_EQUIPMENT_MODIFIER))

        assertIllegalArgument("Requires unknown Equipment Modifier 99!") { ammunition.validate(STATE) }
    }

    @Test
    fun `A valid ammunition`() {
        val ammunition =
            Ammunition(AMMUNITION_ID_0, type = AMMUNITION_TYPE_ID_0, modifiers = setOf(EQUIPMENT_MODIFIER_ID_0))

        ammunition.validate(STATE)
    }
}