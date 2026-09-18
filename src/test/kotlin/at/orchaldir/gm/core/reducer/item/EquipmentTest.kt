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
import at.orchaldir.gm.core.model.economy.money.MIN_PRICE
import at.orchaldir.gm.core.model.economy.money.UserDefinedPrice
import at.orchaldir.gm.core.model.item.equipment.*
import at.orchaldir.gm.core.model.rpg.equipment.EquipmentStats
import at.orchaldir.gm.core.model.util.part.MadeFromCord
import at.orchaldir.gm.core.model.util.part.MadeFromFabric
import at.orchaldir.gm.core.model.util.render.*
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.unit.ONE_GRAM
import at.orchaldir.gm.utils.math.unit.UserDefinedWeight
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
            val oldItem = Equipment(EQUIPMENT_ID_0, appearance = Pants(main = MadeFromFabric(MATERIAL_ID_0)))
            val newItem = Equipment(EQUIPMENT_ID_0, appearance = Shirt(main = MadeFromFabric(MATERIAL_ID_0)))
            val state = State(
                listOf(
                    Storage(oldItem),
                    Storage(Character(CHARACTER_ID_0, equipped = EQUIPMENT_MAP)),
                    Storage(Material(MATERIAL_ID_0)),
                )
            )

            assertInvalid(newItem, "Cannot change equipment 0 while it is equipped", state)
        }

        @Test
        fun `Can change equipment details while equipped`() {
            val oldItem = Equipment(EQUIPMENT_ID_0, appearance = Shirt(main = MadeFromFabric(MATERIAL_ID_0)))
            val newItem = Equipment(EQUIPMENT_ID_0, appearance = Shirt(main = MadeFromFabric(MATERIAL_ID_1)))
            val state = State(
                listOf(
                    Storage(oldItem),
                    Storage(Character(CHARACTER_ID_0, equipped = EQUIPMENT_MAP)),
                    Storage(listOf(Material(MATERIAL_ID_0), Material(MATERIAL_ID_1))),
                )
            )

            success(newItem, state)
        }

        @Test
        fun `Material must exist`() {
            val item = createItem(material = UNKNOWN_MATERIAL_ID)

            assertInvalid(item, "Requires unknown Material 99!")
        }

        @Test
        fun `Color scheme must exist`() {
            val item = createItem(UNKNOWN_COLOR_SCHEME_ID)

            assertInvalid(item, "Requires unknown Color Scheme 99!")
        }

        @Test
        fun `Color scheme group must exist`() {
            val item = createWithGroup(UNKNOWN_COLOR_SCHEME_GROUP_ID)

            assertInvalid(item, "Requires unknown Color Scheme Group 99!")
        }

        @Nested
        inner class StatsTest {

            @Test
            fun `Equipment stats must have valid modifiers`() {
                val stats = EquipmentStats(modifiers = setOf(UNKNOWN_EQUIPMENT_MODIFIER))
                val item = Equipment(EQUIPMENT_ID_0, stats = stats)

                assertInvalid(item, "Requires unknown Equipment Modifier 99!")
            }

            @Test
            fun `Equipment type must exist`() {
                val item = Equipment(EQUIPMENT_ID_0, stats = EquipmentStats(UNKNOWN_EQUIPMENT_TYPE))

                assertInvalid(item, "Requires unknown Equipment Type 99!")
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

            private fun success(scheme: ColorSchemeId, lookup: ColorLookup) =
                success(createItem(scheme, lookup = lookup))

            private fun fail(scheme: ColorSchemeId, lookup: ColorLookup) {
                val item = createItem(scheme, lookup = lookup)

                assertInvalid(item, "${scheme.print()} has too few colors!")
            }
        }

        @Nested
        inner class PriceTest {

            @Test
            fun `Cannot have a price above the maximum`() {
                val price = UserDefinedPrice(MAX_EQUIPMENT_PRICE + MIN_PRICE)
                val equipment = Equipment(EQUIPMENT_ID_0, price = price)

                assertInvalid(equipment, "The Price is too large!")
            }
        }

        @Nested
        inner class WeightTest {

            @Test
            fun `Cannot have a weight below the minimum`() {
                val weight = UserDefinedWeight(MIN_EQUIPMENT_WEIGHT - ONE_GRAM)
                val equipment = Equipment(EQUIPMENT_ID_0, weight = weight)

                assertInvalid(equipment, "The Weight is too small!")
            }

            @Test
            fun `Cannot have a weight above the maximum`() {
                val weight = UserDefinedWeight(MAX_EQUIPMENT_WEIGHT + ONE_GRAM)
                val equipment = Equipment(EQUIPMENT_ID_0, weight = weight)

                assertInvalid(equipment, "The Weight is too large!")
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
            success(createItem(lookup = LookupSchema0))
        }

        private fun createItem(
            scheme: ColorSchemeId = COLOR_SCHEME_ID_1,
            material: MaterialId = MATERIAL_ID_0,
            lookup: ColorLookup = LookupMaterial,
        ) = Equipment(
            EQUIPMENT_ID_0,
            colorSchemes = UseColorSchemes(scheme),
            appearance = Glasses(frame = MadeFromCord(material, lookup)),
        )

        private fun createWithGroup(
            group: ColorSchemeGroupId = COLOR_SCHEME_GROUP_ID_0,
            material: MaterialId = MATERIAL_ID_0,
            lookup: ColorLookup = LookupMaterial,
        ) = Equipment(
            EQUIPMENT_ID_0,
            colorSchemes = UseColorSchemeGroup(group),
            appearance = Glasses(frame = MadeFromCord(material, lookup)),
        )

        private fun success(equipment: Equipment, state: State = STATE) {
            val action = UpdateAction(equipment)

            assertEquals(equipment, REDUCER.invoke(state, action).first.getEquipmentStorage().get(EQUIPMENT_ID_0))
        }

        private fun assertInvalid(equipment: Equipment, message: String, state: State = STATE) {
            val action = UpdateAction(equipment)

            assertIllegalArgument(message) { REDUCER.invoke(state, action) }
        }
    }

}