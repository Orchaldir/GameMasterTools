package at.orchaldir.gm.core.reducer.rpg

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.*
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class AmorTypeTest {

    private val STATE = State(
        listOf(
            Storage(listOf(DamageType(DAMAGE_TYPE_ID_0))),
        )
    )

    @Nested
    inner class DamageResistanceTest {
        @Test
        fun `Damage Resistance must be greater than 0`() {
            val protection = DamageResistance(0)

            assertInvalidType(protection, "Damage Resistance needs to be greater 0!")
        }
    }

    @Nested
    inner class DamageResistancesTest {
        @Test
        fun `Damage Resistance must be greater or equal to 0`() {
            val protection = DamageResistances(-1)

            assertInvalidType(protection, "Damage Resistance needs to be >= 0!")
        }

        @Test
        fun `Specific Damage Resistance must be greater or equal to 0`() {
            val protection = DamageResistances(4, mapOf(DAMAGE_TYPE_ID_0 to -1))

            assertInvalidType(protection, "Damage Resistance for Damage Type 0 needs to be >= 0!")
        }

    }

    private fun assertInvalidType(protection: Protection, message: String) {
        val type = ArmorType(ARMOR_TYPE_ID_0, protection = protection)

        assertIllegalArgument(message) { type.validate(STATE) }
    }

}