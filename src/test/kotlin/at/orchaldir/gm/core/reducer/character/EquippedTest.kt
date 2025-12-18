package at.orchaldir.gm.core.reducer.character

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.EquippedEquipment
import at.orchaldir.gm.core.model.character.EquippedUniform
import at.orchaldir.gm.core.model.item.Uniform
import at.orchaldir.gm.core.model.item.equipment.*
import at.orchaldir.gm.core.model.rpg.statblock.UndefinedStatblockLookup
import at.orchaldir.gm.core.model.util.render.ColorScheme
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class EquippedTest {
    val state = State(
        listOf(
            Storage(Character(CHARACTER_ID_0)),
            Storage(ColorScheme(COLOR_SCHEME_ID_0)),
            Storage(Equipment(EQUIPMENT_ID_0, data = Hat())),
            Storage(Uniform(UNIFORM_ID_0)),
        )
    )

    @Nested
    inner class EquippedEquipmentTest {

        private val equipmentMap = EquipmentMap
            .from(BodySlot.Head, EQUIPMENT_ID_0, COLOR_SCHEME_ID_0)
        private val equipped = EquippedEquipment(equipmentMap)

        @Test
        fun `Use equipment`() {
            validateEquipped(state, equipped, UndefinedStatblockLookup)
        }

        @Test
        fun `Cannot use unknown equipment`() {
            val state = state.removeStorage(EQUIPMENT_ID_0)

            assertIllegalArgument("Requires unknown Equipment 0!") { validateEquipped(state, equipped, UndefinedStatblockLookup) }
        }

        @Test
        fun `Cannot use unknown color scheme`() {
            val state = state.removeStorage(COLOR_SCHEME_ID_0)

            assertIllegalArgument("Requires unknown Color Scheme 0!") { validateEquipped(state, equipped, UndefinedStatblockLookup) }
        }

        @Test
        fun `Cannot use equipment with wrong slots`() {
            val state = state.updateStorage(Storage(listOf(Equipment(EQUIPMENT_ID_0, data = Dress()))))

            assertIllegalArgument("Equipment 0 uses wrong slots!") { validateEquipped(state, equipped, UndefinedStatblockLookup) }
        }

        @Test
        fun `Cannot occupy equipment slot twice`() {
            val state = state.updateStorage(
                Storage(
                    listOf(
                        Equipment(EQUIPMENT_ID_0, data = Dress()),
                        Equipment(EQUIPMENT_ID_1, data = Shirt())
                    )
                )
            )
            val equipmentMap = EquipmentMap.fromSlotAsValueMap<EquipmentIdPair>(
                mapOf(
                    EQUIPMENT_ID_0 to setOf(setOf(BodySlot.Bottom, BodySlot.InnerTop)),
                    EQUIPMENT_ID_1 to setOf(setOf(BodySlot.InnerTop)),
                )
                    .mapKeys { Pair(it.key, COLOR_SCHEME_ID_0) }
            )

            assertIllegalArgument("Body slot InnerTop is occupied multiple times!") {
                validateEquipped(
                    state,
                    EquippedEquipment(equipmentMap),
                    UndefinedStatblockLookup,
                )
            }
        }
    }

    @Nested
    inner class EquippedUniformTest {

        private val equipped = EquippedUniform(UNIFORM_ID_0)

        @Test
        fun `Use uniform`() {
            validateEquipped(state, equipped, UndefinedStatblockLookup)
        }

        @Test
        fun `Cannot use unknown uniform`() {
            val state = state.removeStorage(UNIFORM_ID_0)

            assertIllegalArgument("Requires unknown Uniform 0!") { validateEquipped(state, equipped, UndefinedStatblockLookup) }
        }
    }
}