package at.orchaldir.gm.core.reducer.character

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.item.Uniform
import at.orchaldir.gm.core.model.item.equipment.*
import at.orchaldir.gm.core.model.rpg.statblock.UndefinedStatblockLookup
import at.orchaldir.gm.core.model.rpg.statblock.UseStatblockOfTemplate
import at.orchaldir.gm.core.model.util.render.ColorScheme
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class EquippedTest {
    private val state = State(
        listOf(
            Storage(Character(CHARACTER_ID_0)),
            Storage(CharacterTemplate(CHARACTER_TEMPLATE_ID_0, race = RACE_ID_0)),
            Storage(ColorScheme(COLOR_SCHEME_ID_0)),
            Storage(listOf(Equipment(EQUIPMENT_ID_0, data = Hat()), Equipment(EQUIPMENT_ID_1, data = Footwear()))),
            Storage(Uniform(UNIFORM_ID_0)),
        )
    )

    @Nested
    inner class EquippedEquipmentTest {

        private val equipmentMap = EquipmentMap
            .from(BodySlot.Head, EQUIPMENT_ID_0, COLOR_SCHEME_ID_0)
        private val equipped = UniqueEquipment(equipmentMap)

        @Test
        fun `Use equipment`() {
            validateEquipped(state, equipped, UndefinedStatblockLookup)
        }

        @Test
        fun `Cannot use unknown equipment`() {
            val state = state.removeStorage(EQUIPMENT_ID_0)

            assertIllegalArgument("Requires unknown Equipment 0!") {
                validateEquipped(
                    state,
                    equipped,
                    UndefinedStatblockLookup
                )
            }
        }

        @Test
        fun `Cannot use unknown color scheme`() {
            val state = state.removeStorage(COLOR_SCHEME_ID_0)

            assertIllegalArgument("Requires unknown Color Scheme 0!") {
                validateEquipped(
                    state,
                    equipped,
                    UndefinedStatblockLookup
                )
            }
        }

        @Test
        fun `Cannot use equipment with wrong slots`() {
            val state = state.updateStorage(Equipment(EQUIPMENT_ID_0, data = Dress()))

            assertIllegalArgument("Equipment 0 uses wrong slots!") {
                validateEquipped(
                    state,
                    equipped,
                    UndefinedStatblockLookup
                )
            }
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
                    UniqueEquipment(equipmentMap),
                    UndefinedStatblockLookup,
                )
            }
        }
    }

    @Nested
    inner class EquippedUniformTest {

        private val equipped = UseUniform(UNIFORM_ID_0)

        @Test
        fun `Use uniform`() {
            validateEquipped(state, equipped, UndefinedStatblockLookup)
        }

        @Test
        fun `Cannot use unknown uniform`() {
            val state = state.removeStorage(UNIFORM_ID_0)

            assertIllegalArgument("Requires unknown Uniform 0!") {
                validateEquipped(
                    state,
                    equipped,
                    UndefinedStatblockLookup
                )
            }
        }
    }

    @Nested
    inner class ModifiedUniformTest {

        @Test
        fun `Add an equipment`() {
            val uniformEquipped = UniqueEquipment(map0)
            val newState = state.updateStorage(Uniform(UNIFORM_ID_0, equipped = uniformEquipped))
            val equipped = ModifyUniform(UNIFORM_ID_0, EquipmentMapUpdate(added = map1))

            validateEquipped(newState, equipped, UndefinedStatblockLookup)
        }

        @Test
        fun `Add an unknown equipment`() {
            val uniformEquipped = UniqueEquipment(map0)
            val newState = state.updateStorage(Uniform(UNIFORM_ID_0, equipped = uniformEquipped))
            val equipped = ModifyUniform(
                UNIFORM_ID_0,
                EquipmentMapUpdate(added = EquipmentIdMap.from(BodySlot.Foot, UNKNOWN_EQUIPMENT_ID))
            )

            assertIllegalArgument("Requires unknown Equipment 99!") {
                validateEquipped(newState, equipped, UndefinedStatblockLookup)
            }
        }

        @Test
        fun `Add the same equipment`() {
            val uniformEquipped = UniqueEquipment(map0)
            val newState = state.updateStorage(Uniform(UNIFORM_ID_0, equipped = uniformEquipped))
            val equipped = ModifyUniform(UNIFORM_ID_0, EquipmentMapUpdate(added = map0))

            assertIllegalArgument("Couldn't add [[Head]] again!") {
                validateEquipped(newState, equipped, UndefinedStatblockLookup)
            }
        }

        @Test
        fun `Remove an equipment`() {
            val uniformEquipped = UniqueEquipment(map01)
            val newState = state.updateStorage(Uniform(UNIFORM_ID_0, equipped = uniformEquipped))
            val equipped = ModifyUniform(UNIFORM_ID_0, EquipmentMapUpdate(removed = setOf(setOf(BodySlot.Foot))))

            validateEquipped(newState, equipped, UndefinedStatblockLookup)
        }

        @Test
        fun `Remove an equipment that is not part of the uniform`() {
            val uniformEquipped = UniqueEquipment(map0)
            val newState = state.updateStorage(Uniform(UNIFORM_ID_0, equipped = uniformEquipped))
            val equipped = ModifyUniform(UNIFORM_ID_0, EquipmentMapUpdate(removed = setOf(setOf(BodySlot.Foot))))

            assertIllegalArgument("Couldn't remove [[Foot]]!") {
                validateEquipped(newState, equipped, UndefinedStatblockLookup)
            }
        }

        @Test
        fun `Cannot use unknown uniform`() {
            val equipped = ModifyUniform(UNKNOWN_UNIFORM_ID, EquipmentMapUpdate())

            assertIllegalArgument("Requires unknown Uniform 99!") {
                validateEquipped(
                    state,
                    equipped,
                    UndefinedStatblockLookup
                )
            }
        }
    }

    @Nested
    inner class UseEquipmentFromTemplateTest {

        @Test
        fun `Use with template`() {
            validateEquipped(state, UseEquipmentFromTemplate, UseStatblockOfTemplate(CHARACTER_TEMPLATE_ID_0))
        }

        @Test
        fun `Fail without template`() {
            val state = state.removeStorage(UNIFORM_ID_0)

            assertIllegalArgument("Cannot use equipment from the template without a template!") {
                validateEquipped(state, UseEquipmentFromTemplate, UndefinedStatblockLookup)
            }
        }
    }

    @Nested
    inner class ModifyEquipmentFromTemplateTest {
        val equipped = ModifyEquipmentFromTemplate(EquipmentMapUpdate())

        @Test
        fun `Use with template`() {
            validateEquipped(state, equipped, UseStatblockOfTemplate(CHARACTER_TEMPLATE_ID_0))
        }

        @Test
        fun `Add an unknown equipment`() {
            val equipped = ModifyEquipmentFromTemplate(EquipmentMapUpdate(added = unknownMap))

            assertIllegalArgument("Requires unknown Equipment 99!") {
                validateEquipped(state, equipped, UseStatblockOfTemplate(CHARACTER_TEMPLATE_ID_0))
            }
        }

        @Test
        fun `Fail without template`() {
            assertIllegalArgument("Cannot use equipment from the template without a template!") {
                validateEquipped(state, equipped, UndefinedStatblockLookup)
            }
        }
    }
}