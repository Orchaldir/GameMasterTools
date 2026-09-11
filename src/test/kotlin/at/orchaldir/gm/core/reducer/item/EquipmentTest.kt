package at.orchaldir.gm.core.reducer.item

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.UpdateAction
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.UniqueEquipment
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.economy.material.MaterialProperties
import at.orchaldir.gm.core.model.economy.material.Metal
import at.orchaldir.gm.core.model.item.equipment.*
import at.orchaldir.gm.core.model.rpg.equipment.EquipmentStats
import at.orchaldir.gm.core.model.util.part.MadeFromCord
import at.orchaldir.gm.core.model.util.part.MadeFromFabric
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
            Storage(Material(MATERIAL_ID_0, properties = MaterialProperties(Metal()))),
        ),
    )
    private val EQUIPMENT_MAP = UniqueEquipment(
        EquipmentMap
            .from(BodySlot.Head, EQUIPMENT_ID_0, COLOR_SCHEME_ID_0)
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
            val oldItem = Equipment(EQUIPMENT_ID_0, data = Pants(main = MadeFromFabric(MATERIAL_ID_0)))
            val newItem = Equipment(EQUIPMENT_ID_0, data = Shirt(main = MadeFromFabric(MATERIAL_ID_0)))
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
            val oldItem = Equipment(EQUIPMENT_ID_0, data = Shirt(main = MadeFromFabric(MATERIAL_ID_0)))
            val newItem = Equipment(EQUIPMENT_ID_0, data = Shirt(main = MadeFromFabric(MATERIAL_ID_1)))
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

        @Test
        fun `Color scheme group must exist`() {
            val item = createWithGroup(UNKNOWN_COLOR_SCHEME_GROUP_ID)
            val action = UpdateAction(item)

            assertIllegalArgument("Requires unknown Color Scheme Group 99!") { REDUCER.invoke(STATE, action) }
        }

        @Nested
        inner class StatsTest {

            @Test
            fun `Equipment stats must have valid modifiers`() {
                val item = Equipment(EQUIPMENT_ID_0, stats = EquipmentStats(modifiers = setOf(UNKNOWN_EQUIPMENT_MODIFIER)))
                val action = UpdateAction(item)

                assertIllegalArgument("Requires unknown Equipment Modifier 99!") { REDUCER.invoke(STATE, action) }
            }

            @Test
            fun `Equipment type must exist`() {
                val item = Equipment(EQUIPMENT_ID_0, stats = EquipmentStats(UNKNOWN_EQUIPMENT_TYPE))
                val action = UpdateAction(item)

                assertIllegalArgument("Requires unknown Equipment Type 99!") { REDUCER.invoke(STATE, action) }
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
            colorSchemes = UseColorSchemes(scheme),
            data = Glasses(frame = MadeFromCord(material, lookup)),
        )

        private fun createWithGroup(
            group: ColorSchemeGroupId = COLOR_SCHEME_GROUP_ID_0,
            material: MaterialId = MATERIAL_ID_0,
            lookup: ColorLookup = LookupMaterial,
        ) = Equipment(
            EQUIPMENT_ID_0,
            colorSchemes = UseColorSchemeGroup(group),
            data = Glasses(frame = MadeFromCord(material, lookup)),
        )
    }

}