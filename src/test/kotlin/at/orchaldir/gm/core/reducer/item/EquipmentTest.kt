package at.orchaldir.gm.core.reducer.item

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.UpdateAction
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.EquippedEquipment
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.item.equipment.*
import at.orchaldir.gm.core.model.item.equipment.style.ScaleArmour
import at.orchaldir.gm.core.model.rpg.combat.ArmorStats
import at.orchaldir.gm.core.model.rpg.combat.MeleeWeaponStats
import at.orchaldir.gm.core.model.rpg.combat.ShieldStats
import at.orchaldir.gm.core.model.util.part.ColorSchemeItemPart
import at.orchaldir.gm.core.model.util.part.FillLookupItemPart
import at.orchaldir.gm.core.model.util.render.*
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class EquipmentTest {
    private val ITEM = Equipment(EQUIPMENT_ID_0, NAME)
    private val STATE = State(
        listOf(
            Storage(
                listOf(
                    ColorScheme(COLOR_SCHEME_ID_0, UndefinedColors),
                    ColorScheme(COLOR_SCHEME_ID_1, OneColor(Color.Red)),
                    ColorScheme(COLOR_SCHEME_ID_2, TwoColors.init(Color.Blue, Color.Green)),
                )
            ),
            Storage(Equipment(EQUIPMENT_ID_0)),
            Storage(Material(MATERIAL_ID_0))
        ),
    )
    private val EQUIPMENT_MAP = EquippedEquipment(
        EquipmentMap
            .fromId(EQUIPMENT_ID_0, COLOR_SCHEME_ID_0, BodySlot.Head)
    )

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateAction(ITEM)

            assertIllegalArgument("Requires unknown Equipment 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot change equipment type while equipped`() {
            val oldItem = Equipment(EQUIPMENT_ID_0, data = Pants(main = FillLookupItemPart(MATERIAL_ID_0)))
            val newItem = Equipment(EQUIPMENT_ID_0, data = Shirt(main = FillLookupItemPart(MATERIAL_ID_0)))
            val state = State(
                listOf(
                    Storage(oldItem),
                    Storage(Character(CHARACTER_ID_0, equipped = EQUIPMENT_MAP)),
                    Storage(Material(MATERIAL_ID_0)),
                )
            )
            val action = UpdateAction(newItem)

            assertIllegalArgument("Cannot change equipment 0 while it is equipped") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Can change equipment details while equipped`() {
            val oldItem = Equipment(EQUIPMENT_ID_0, data = Shirt(main = FillLookupItemPart(MATERIAL_ID_0)))
            val newItem = Equipment(EQUIPMENT_ID_0, data = Shirt(main = FillLookupItemPart(MATERIAL_ID_1)))
            val state = State(
                listOf(
                    Storage(oldItem),
                    Storage(Character(CHARACTER_ID_0, equipped = EQUIPMENT_MAP)),
                    Storage(listOf(Material(MATERIAL_ID_0), Material(MATERIAL_ID_1))),
                )
            )
            val action = UpdateAction(newItem)

            assertEquals(newItem, REDUCER.invoke(state, action).first.getEquipmentStorage().get(EQUIPMENT_ID_0))
        }

        @Test
        fun `Material must exist`() {
            val item = createItem(material = UNKNOWN_MATERIAL_ID)
            val action = UpdateAction(item)

            assertIllegalArgument("Requires unknown Material 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Color scheme must exist`() {
            val item = createItem(UNKNOWN_COLOR_SCHEME_ID)
            val action = UpdateAction(item)

            assertIllegalArgument("Requires unknown Color Scheme 99!") { REDUCER.invoke(STATE, action) }
        }

        @Nested
        inner class StatsTest {

            @Test
            fun `Armor stats must have valid modifiers`() {
                val data = BodyArmour(ScaleArmour(), stats = ArmorStats(modifiers = setOf(UNKNOWN_EQUIPMENT_MODIFIER)))
                val item = Equipment(EQUIPMENT_ID_0, data = data)
                val action = UpdateAction(item)

                assertIllegalArgument("Requires unknown Equipment Modifier 99!") { REDUCER.invoke(STATE, action) }
            }

            @Test
            fun `Armor type must exist`() {
                val data = BodyArmour(ScaleArmour(), stats = ArmorStats(UNKNOWN_ARMOR_TYPE))
                val item = Equipment(EQUIPMENT_ID_0, data = data)
                val action = UpdateAction(item)

                assertIllegalArgument("Requires unknown Armor Type 99!") { REDUCER.invoke(STATE, action) }
            }

            @Test
            fun `Melee weapon stats must have valid modifiers`() {
                val data = OneHandedAxe(stats = MeleeWeaponStats(null, setOf(UNKNOWN_EQUIPMENT_MODIFIER)))
                val item = Equipment(EQUIPMENT_ID_0, data = data)
                val action = UpdateAction(item)

                assertIllegalArgument("Requires unknown Equipment Modifier 99!") { REDUCER.invoke(STATE, action) }
            }

            @Test
            fun `Melee weapon type must exist`() {
                val data = OneHandedAxe(stats = MeleeWeaponStats(UNKNOWN_MELEE_WEAPON_TYPE))
                val item = Equipment(EQUIPMENT_ID_0, data = data)
                val action = UpdateAction(item)

                assertIllegalArgument("Requires unknown Melee Weapon Type 99!") { REDUCER.invoke(STATE, action) }
            }

            @Test
            fun `Shield stats must have valid modifiers`() {
                val data = Shield(stats = ShieldStats(null, setOf(UNKNOWN_EQUIPMENT_MODIFIER)))
                val item = Equipment(EQUIPMENT_ID_0, data = data)
                val action = UpdateAction(item)

                assertIllegalArgument("Requires unknown Equipment Modifier 99!") { REDUCER.invoke(STATE, action) }
            }

            @Test
            fun `Shield stats must have an valid shield type`() {
                val data = Shield(stats = ShieldStats(UNKNOWN_SHIELD_TYPE))
                val item = Equipment(EQUIPMENT_ID_0, data = data)
                val action = UpdateAction(item)

                assertIllegalArgument("Requires unknown Shield Type 99!") { REDUCER.invoke(STATE, action) }
            }
        }

        @Nested
        inner class RequiredSchemaColorsTest {

            @Test
            fun `Color scheme has 0 colors and needs 0`() {
                success(COLOR_SCHEME_ID_0, LookupMaterial)
            }

            @Test
            fun `Color scheme has 0 colors, but needs 1`() {
                fail(COLOR_SCHEME_ID_0, LookupSchema0)
            }

            @Test
            fun `Color scheme has 0 colors, but needs 2`() {
                fail(COLOR_SCHEME_ID_0, LookupSchema1)
            }

            @Test
            fun `Color scheme has 1 color and needs 0`() {
                success(COLOR_SCHEME_ID_1, LookupMaterial)
            }

            @Test
            fun `Color scheme has 1 color and needs 1`() {
                success(COLOR_SCHEME_ID_1, LookupSchema0)
            }

            @Test
            fun `Color scheme has 1 color, but needs 2`() {
                fail(COLOR_SCHEME_ID_1, LookupSchema1)
            }

            @Test
            fun `Color scheme has 2 color and needs 0`() {
                success(COLOR_SCHEME_ID_2, LookupMaterial)
            }

            @Test
            fun `Color scheme has 2 color and needs 1`() {
                success(COLOR_SCHEME_ID_2, LookupSchema0)
            }

            @Test
            fun `Color scheme has 2 color and needs 2`() {
                success(COLOR_SCHEME_ID_2, LookupSchema1)
            }

            private fun success(scheme: ColorSchemeId, lookup: ColorLookup) {
                val item = createItem(scheme, lookup = lookup)
                val action = UpdateAction(item)

                REDUCER.invoke(STATE, action)
            }

            private fun fail(scheme: ColorSchemeId, lookup: ColorLookup) {
                val item = createItem(scheme, lookup = lookup)
                val action = UpdateAction(item)

                assertIllegalArgument("${scheme.print()} has too few colors!") { REDUCER.invoke(STATE, action) }
            }
        }

        @Test
        fun `Update template`() {
            val action = UpdateAction(ITEM)

            assertEquals(
                ITEM,
                REDUCER.invoke(STATE, action).first.getEquipmentStorage().get(EQUIPMENT_ID_0)
            )
        }

        @Test
        fun `Update template with material`() {
            val item = createItem(lookup = LookupSchema0)
            val action = UpdateAction(item)

            assertEquals(item, REDUCER.invoke(STATE, action).first.getEquipmentStorage().get(EQUIPMENT_ID_0))
        }

        private fun createItem(
            scheme: ColorSchemeId = COLOR_SCHEME_ID_1,
            material: MaterialId = MATERIAL_ID_0,
            lookup: ColorLookup = LookupMaterial,
        ) = Equipment(
            EQUIPMENT_ID_0,
            colorSchemes = setOf(scheme),
            data = Glasses(frame = ColorSchemeItemPart(material, lookup)),
        )
    }

}